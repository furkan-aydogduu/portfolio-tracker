package com.frkn.crypto.tracker.bitfinex.controller;

import com.frkn.crypto.tracker.bitfinex.helper.CurrencyPairConverter;
import com.frkn.crypto.tracker.bitfinex.model.Pair;
import com.frkn.crypto.tracker.bitfinex.model.Ticker;
import com.frkn.crypto.tracker.configuration.RestTemplateSecurer;
import com.frkn.crypto.tracker.model.PortfolioEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class BitFinexAPIController {

    private String baseBitFinexURL = "https://api.bitfinex.com/v1/";
    private String symbolsURL = "symbols";
    private String tickerURL = "pubticker/";

    private RestTemplate restTemplate;

    @Autowired
    RestTemplateSecurer restTemplateSecurer;

    @Autowired
    CurrencyPairConverter currencyPairConverter;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.restTemplateSecurer.customize(this.restTemplate);
    }

    public String[] getSymbols() {
        ResponseEntity<String[]> symbolsResponse = restTemplate.exchange(
                baseBitFinexURL + symbolsURL,
                HttpMethod.GET,
                    null,
                    ParameterizedTypeReference.forType(String[].class));

        return symbolsResponse.getBody();
    }

    public Ticker getTicker(String tickerCode) {
        ResponseEntity<Ticker> symbolsResponse = restTemplate.exchange(
                baseBitFinexURL + tickerURL + tickerCode,
                HttpMethod.GET,
                    null,
                    ParameterizedTypeReference.forType(Ticker.class));
        return symbolsResponse.getBody();
    }

    public void calculateCurrencyPricesOfPortfolios(List<PortfolioEntry> currencies){
        String[] symbols = getSymbols();
        //String[] symbols = new String[]{"etheur", "ioteth", "btcusd", "ethusd"};
        List<Pair> pairs = currencyPairConverter.convertSymbolsToPairs(symbols);

        currencies.forEach(portfolio -> {
            Pair pairToConvertTheCurrencyToEuro = currencyPairConverter
                    .findRelatedPairsToConvertTheCurrencyToAnotherCurrency(portfolio.getCryptoCurrencyName(), "eur", pairs);

            //if we found a calculation path from portfolio currency to the euro currency
            if(pairToConvertTheCurrencyToEuro != null){
                if(!pairToConvertTheCurrencyToEuro.isCurrencyItself()){
                    pairToConvertTheCurrencyToEuro.getPossibleConvertablePairs().forEach(
                            relatedPair -> {
                                String legacyPairString = relatedPair.getLegacyPairString();
                                Ticker ticker = getTicker(legacyPairString);
                                relatedPair.setPairPrice(ticker.getMid());
                            }
                    );
                }
                else{
                    pairToConvertTheCurrencyToEuro.setPairPrice(1.0);
                    pairToConvertTheCurrencyToEuro.getPossibleConvertablePairs().clear();
                }
                Double currentPortfolioMarketValue = pairToConvertTheCurrencyToEuro.calculatePrice(portfolio);
                portfolio.setMarketValueAtCurrentTime(currentPortfolioMarketValue);
            }
        });
    }

}
