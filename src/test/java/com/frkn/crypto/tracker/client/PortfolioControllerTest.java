package com.frkn.crypto.tracker.client;

import com.frkn.crypto.tracker.model.PortfolioEntry;
import com.frkn.crypto.tracker.model.ResponseMessage;
import com.frkn.crypto.tracker.model.ResponseMessageType;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PortfolioControllerTest extends TrackerApplicationTests {

    public PortfolioControllerTest() {
        this.controllerPath = "portfolio";
    }

    @Test
    public void testList() throws Exception {
        List<PortfolioEntry> portfoliosFromDB = portfolioService.findAllByRecordLimit(5);

        assert portfoliosFromDB.size() > 0 && portfoliosFromDB.size() <= 5;

        ResponseEntity<PortfolioEntry[]> response = this.makeRequest("/list", null, PortfolioEntry[].class);

        PortfolioEntry[] portfoliosFromResponse = response.getBody();

        assert portfoliosFromResponse != null;
        assert portfoliosFromResponse.length > 0;

        for (PortfolioEntry portfolioEntry : portfoliosFromResponse) {
            assertPortfolioValuesForNull(portfolioEntry);
        }

        int existingPortfolioCount = 0;
        for (PortfolioEntry portfolioEntryFromDB : portfoliosFromDB) {
            for (PortfolioEntry portfolioEntryFromResponse : portfoliosFromResponse) {
                if (
                        portfolioEntryFromDB.getId().equals(portfolioEntryFromResponse.getId())
                    && portfolioEntryFromDB.getCryptoCurrencyName().equals(portfolioEntryFromResponse.getCryptoCurrencyName())
                    && portfolioEntryFromDB.getAmountPurchased().equals(portfolioEntryFromResponse.getAmountPurchased())
                    && portfolioEntryFromDB.getWalletLocation().equals(portfolioEntryFromResponse.getWalletLocation())
                    && portfolioEntryFromDB.getUpdatedTime().equals(portfolioEntryFromResponse.getUpdatedTime())
                    && portfolioEntryFromDB.getCreationTime().equals(portfolioEntryFromResponse.getCreationTime())
                    && portfolioEntryFromDB.getMarketValueAtPurchasedTime().equals(portfolioEntryFromResponse.getMarketValueAtPurchasedTime())
                ) {
                    existingPortfolioCount++;
                }
            }
        }
        assert existingPortfolioCount == portfoliosFromDB.size();

    }

    @Test
    public void testView() throws Exception {
        Long samplePortfolioId = new Long(1);
        Optional<PortfolioEntry> portfolioFromDB = portfolioService.findById(samplePortfolioId);

        assert portfolioFromDB.isPresent();

        ResponseEntity<PortfolioEntry> response = this.makeRequest("/view/" + samplePortfolioId, PortfolioEntry.class);

        PortfolioEntry portfolioEntryFromResponse = response.getBody();

        assert portfolioEntryFromResponse != null;

        assertPortfolioValuesForNull(portfolioEntryFromResponse);

        PortfolioEntry portfolioEntryLegacy = portfolioFromDB.get();

        assert portfolioEntryLegacy.getId().equals(portfolioEntryFromResponse.getId());
        assert portfolioEntryLegacy.getCryptoCurrencyName().equals(portfolioEntryFromResponse.getCryptoCurrencyName());
        assert portfolioEntryLegacy.getAmountPurchased().equals(portfolioEntryFromResponse.getAmountPurchased());
        assert portfolioEntryLegacy.getWalletLocation().equals(portfolioEntryFromResponse.getWalletLocation());
        assert portfolioEntryLegacy.getUpdatedTime().equals(portfolioEntryFromResponse.getUpdatedTime());
        assert portfolioEntryLegacy.getCreationTime().equals(portfolioEntryFromResponse.getCreationTime());
        assert portfolioEntryLegacy.getMarketValueAtPurchasedTime().equals(portfolioEntryFromResponse.getMarketValueAtPurchasedTime());

    }

    @Test
    public void testAdd() throws Exception {
        Double amountToPersist = 2.5;
        String walletLocationToPersist = "New Wallet Location To Persist";
        String currencyNameToPersist = "btc";

        PortfolioEntry testPortfolioEntry = new PortfolioEntry(currencyNameToPersist, amountToPersist, walletLocationToPersist);

        ResponseEntity<PortfolioEntry> response = this.makeRequest("/add", testPortfolioEntry.toString(), PortfolioEntry.class);

        PortfolioEntry portfolioEntryFromResponse = response.getBody();

        assert portfolioEntryFromResponse != null;

        assertPortfolioValuesForNull(portfolioEntryFromResponse);

        assert testPortfolioEntry.getCryptoCurrencyName().equals(portfolioEntryFromResponse.getCryptoCurrencyName());
        assert testPortfolioEntry.getAmountPurchased().equals(portfolioEntryFromResponse.getAmountPurchased());
        assert testPortfolioEntry.getWalletLocation().equals(portfolioEntryFromResponse.getWalletLocation());
        assert portfolioEntryFromResponse.getMarketValueAtPurchasedTime().equals(portfolioEntryFromResponse.getMarketValueAtCurrentTime());
        assert portfolioEntryFromResponse.getCurrentCurrencyPrice() * portfolioEntryFromResponse.getAmountPurchased()
                == portfolioEntryFromResponse.getMarketValueAtPurchasedTime();

        Optional<PortfolioEntry> portfolioAfterPersistToDB = portfolioService.findById(portfolioEntryFromResponse.getId());

        assert portfolioAfterPersistToDB.isPresent();

        PortfolioEntry portfolioEntryAfterPersist = portfolioAfterPersistToDB.get();

        assertPortfolioValuesForNullFor(portfolioEntryAfterPersist);

        assert portfolioEntryAfterPersist.getAmountPurchased().equals(amountToPersist);
        assert portfolioEntryAfterPersist.getWalletLocation().equals(walletLocationToPersist);
        assert portfolioEntryAfterPersist.getCryptoCurrencyName().equals(currencyNameToPersist);
        assert portfolioEntryAfterPersist.getCreationTime().equals(portfolioEntryAfterPersist.getUpdatedTime());

    }

    @Test
    public void testDelete() throws Exception {

        Optional<PortfolioEntry> anyPortfolio = this.portfolioService.findAny();

        assert anyPortfolio.isPresent();

        Long portfolioId = anyPortfolio.get().getId();

        ResponseEntity<ResponseMessage> response = this.makeRequest("/delete/" + portfolioId, ResponseMessage.class);

        ResponseMessage responseMessage = response.getBody();

        assertResponseMessageForTypes(responseMessage, ResponseMessageType.DELETED_SUCCESSFULLY);

        Optional<PortfolioEntry> samePortfolioFromDB = this.portfolioService.findById(portfolioId);

        assert !samePortfolioFromDB.isPresent();
    }

    @Test
    public void testUpdate() throws Exception {
        Double amountToUpdate = 3.0;
        String walletLocationToUpdate = "New Wallet Location";
        String currencyNameToUpdate = "btc";

        Optional<PortfolioEntry> anyPortfolio = this.portfolioService.findAny();

        assert anyPortfolio.isPresent();

        PortfolioEntry portfolioEntry = anyPortfolio.get();
        portfolioEntry.setAmountPurchased(amountToUpdate);
        portfolioEntry.setWalletLocation(walletLocationToUpdate);
        portfolioEntry.setCryptoCurrencyName(currencyNameToUpdate);

        LocalDateTime oldUpdatedTime = portfolioEntry.getUpdatedTime();
        Long portfolioId = portfolioEntry.getId();

        ResponseEntity<ResponseMessage> response = this.makeRequest("/update/" + portfolioId,
                portfolioEntry.toString(), ResponseMessage.class);

        ResponseMessage responseMessage = response.getBody();

        assertResponseMessageForTypes(responseMessage, ResponseMessageType.UPDATED_SUCCESSFULLY);

        Optional<PortfolioEntry> samePortfolioAfterUpdateFromDB = this.portfolioService.findById(portfolioId);

        assert samePortfolioAfterUpdateFromDB.isPresent();

        PortfolioEntry samePortfolioEntryAfterUpdate = samePortfolioAfterUpdateFromDB.get();

        assertPortfolioValuesForNullFor(samePortfolioEntryAfterUpdate);

        assert !samePortfolioEntryAfterUpdate.getUpdatedTime().equals(oldUpdatedTime);
        assert samePortfolioEntryAfterUpdate.getAmountPurchased().equals(amountToUpdate);
        assert samePortfolioEntryAfterUpdate.getWalletLocation().equals(walletLocationToUpdate);
        assert samePortfolioEntryAfterUpdate.getCryptoCurrencyName().equals(currencyNameToUpdate);
    }

    @Test
    public void testListSymbols() throws Exception {

        ResponseEntity<String[]> response = this.makeRequest("/list-symbols", String[].class);

        String[] symbols = response.getBody();

        assert symbols.length > 0;

        for (String symbol : symbols) {
            if(symbol.contains(":")){
                assert symbol.split(":").length == 2;
            }
            else{
                assert symbol.length() == 6;
            }
        }
    }

    @Test
    public void testCombinedMarketValue() throws Exception {

        ResponseEntity<ResponseMessage> response = this.makeRequest("/view-portfolio-market-value", ResponseMessage.class);
        ResponseMessage responseMessage = response.getBody();

        assertResponseMessageForTypes(responseMessage);

        Double.parseDouble(responseMessage.getMessage());
    }

    private void assertPortfolioValuesForNull(PortfolioEntry portfolioEntry){
        assert portfolioEntry.getMarketValueAtPurchasedTime() != null;
        assert portfolioEntry.getCryptoCurrencyName() != null;
        assert portfolioEntry.getWalletLocation() != null;
        assert portfolioEntry.getAmountPurchased() != null;
        assert portfolioEntry.getCurrentProfitLossRate() != null;
        assert portfolioEntry.getCurrentCurrencyPrice() != null;
        assert portfolioEntry.getUpdatedTime() != null;
        assert portfolioEntry.getMarketValueAtCurrentTime() != null;
        assert portfolioEntry.getCreationTime() != null;
        assert portfolioEntry.getId() != null;
    }

    private void assertPortfolioValuesForNullFor(PortfolioEntry portfolioEntry){
        assert portfolioEntry.getMarketValueAtPurchasedTime() != null;
        assert portfolioEntry.getCryptoCurrencyName() != null;
        assert portfolioEntry.getWalletLocation() != null;
        assert portfolioEntry.getAmountPurchased() != null;
        assert portfolioEntry.getUpdatedTime() != null;
        assert portfolioEntry.getCreationTime() != null;
        assert portfolioEntry.getId() != null;
    }

}
