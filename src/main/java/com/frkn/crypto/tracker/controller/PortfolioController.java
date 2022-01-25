package com.frkn.crypto.tracker.controller;

import com.frkn.crypto.tracker.bitfinex.controller.BitFinexAPIController;
import com.frkn.crypto.tracker.model.PortfolioEntry;
import com.frkn.crypto.tracker.model.ResponseMessage;
import com.frkn.crypto.tracker.model.ResponseMessageType;
import com.frkn.crypto.tracker.service.PortfolioService;
import com.frkn.crypto.tracker.validation.PortfolioAddValidation;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(
        path = "/portfolio",
        consumes = "application/json",
        produces = "application/json")
public class PortfolioController extends BaseController {

    @Autowired
    BitFinexAPIController bitFinexAPIController;

    @Autowired
    PortfolioService portfolioService;

    @PostMapping(path = "/list")
    public ResponseEntity<String> list(){
        List<PortfolioEntry> remainingPortfolioEntries = (List<PortfolioEntry>) portfolioService.findAll();
        bitFinexAPIController.calculateCurrencyPricesOfPortfolios(remainingPortfolioEntries);
        JSONArray result = new JSONArray(remainingPortfolioEntries);
        return ResponseEntity.ok(result.toString());
    }

    @PostMapping(path = "/add")
    public ResponseEntity<String> add(@Validated(value = PortfolioAddValidation.class) @RequestBody PortfolioEntry portfolioEntry){
        bitFinexAPIController.calculateCurrencyPricesOfPortfolios(Arrays.asList(portfolioEntry));
        portfolioEntry.setMarketValueAtPurchasedTime(portfolioEntry.getMarketValueAtCurrentTime());
        PortfolioEntry savedPortfolioEntry = portfolioService.save(portfolioEntry);
        return ResponseEntity.ok(savedPortfolioEntry.toString());
    }

    @PostMapping(path = "/delete/{id}")
    public ResponseEntity<String> delete(@Valid @PathVariable Long id){
        Optional<PortfolioEntry> portfolioById = portfolioService.findById(id);
        if(!portfolioById.isPresent()){
            ResponseMessage responseMessage = ResponseMessage.buildResponseMessage(ResponseMessageType.RECORD_DOES_NOT_EXIST);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(responseMessage.toString());
        }
        portfolioService.deleteById(id);
        ResponseMessage responseMessage = ResponseMessage.buildResponseMessage(ResponseMessageType.DELETED_SUCCESSFULLY);
        return ResponseEntity.ok(responseMessage.toString());
    }

    @PostMapping(path = "/update/{id}")
    public ResponseEntity<String> update(@Valid @PathVariable Long id, @RequestBody PortfolioEntry portfolioEntry){
        Optional<PortfolioEntry> portfolioById = portfolioService.findById(id);
        if(portfolioById.isPresent()){
            PortfolioEntry portfolioEntryToBeUpdated = portfolioById.get();

            portfolioEntryToBeUpdated.updatePortfolioByAnother(portfolioEntry);
            bitFinexAPIController.calculateCurrencyPricesOfPortfolios(Arrays.asList(portfolioEntryToBeUpdated));
            portfolioEntryToBeUpdated.setMarketValueAtPurchasedTime(portfolioEntryToBeUpdated.getMarketValueAtCurrentTime());
            portfolioService.save(portfolioEntryToBeUpdated);
            ResponseMessage responseMessage = ResponseMessage.buildResponseMessage(ResponseMessageType.UPDATED_SUCCESSFULLY);
            return ResponseEntity.ok(responseMessage.toString());
        }
        else{
            ResponseMessage responseMessage = ResponseMessage.buildResponseMessage(ResponseMessageType.RECORD_DOES_NOT_EXIST);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage.toString());
        }
    }

    @PostMapping(path = "/view/{id}")
    public ResponseEntity<String> view(@Valid @PathVariable Long id){
        Optional<PortfolioEntry> portfolioById = portfolioService.findById(id);

        if(portfolioById.isPresent()){
            PortfolioEntry portfolioEntry = portfolioById.get();
            bitFinexAPIController.calculateCurrencyPricesOfPortfolios(Arrays.asList(portfolioEntry));
        }
        else {
            ResponseMessage responseMessage = ResponseMessage.buildResponseMessage(ResponseMessageType.RECORD_DOES_NOT_EXIST);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage.toString());
        }
        return ResponseEntity.ok(portfolioById.get().toString());
    }

    @PostMapping(path = "/list-symbols")
    public ResponseEntity<String[]> listSymbols() {
        String[] symbolsFromBitFinex = bitFinexAPIController.getSymbols();
        return ResponseEntity.ok(symbolsFromBitFinex);
    }

    @PostMapping(path = "/view-portfolio-market-value")
    public ResponseEntity<String> combinedMarketValue() {
        List<PortfolioEntry> remainingPortfolioEntries = (List<PortfolioEntry>) portfolioService.findAll();
        bitFinexAPIController.calculateCurrencyPricesOfPortfolios(remainingPortfolioEntries);

        Optional<Double> combinedMarketValue = remainingPortfolioEntries
                .stream()
                .map(portfolio -> portfolio.getMarketValueAtCurrentTime())
                .reduce((firstVal, secondVal) -> firstVal + secondVal);

        ResponseMessage responseMessage;
        if(combinedMarketValue.isPresent()){
            responseMessage = new ResponseMessage(combinedMarketValue.get());
            return ResponseEntity.ok(responseMessage.toString());
        }
        else{
            responseMessage = new ResponseMessage("Market Value Could Not Be Calculated");
            return ResponseEntity.badRequest().body(responseMessage.toString());
        }
    }

}
