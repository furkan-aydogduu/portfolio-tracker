package com.frkn.crypto.tracker.model;

import com.frkn.crypto.tracker.validation.PortfolioAddValidation;
import org.json.JSONObject;
import org.json.JSONPropertyName;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.text.DecimalFormat;
import java.time.LocalDateTime;

@Entity
@Table(name = "portfolio")
public class PortfolioEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO , generator = "portfolio_seq")
    @Column(name = "id")
    public Long id;

    @NotNull(message = "No currency name provided", groups = {PortfolioAddValidation.class})
    @Size(message = "Currency name must be not be longer than 250 characters", max = 250, groups = {PortfolioAddValidation.class})
    @Column(name = "crypto_currency_name")
    public String cryptoCurrencyName;

    @NotNull(message = "No purchased amount provided", groups = {PortfolioAddValidation.class})
    @Column(name = "amount_purchased")
    public Double amountPurchased;

    @NotNull(message = "No wallet location provided", groups = {PortfolioAddValidation.class})
    @Size(message = "Wallet location must not be longer than 250 characters", max = 250, groups = {PortfolioAddValidation.class})
    @Column(name = "wallet_location")
    public String walletLocation;

    @NotNull
    @Column(name = "market_value_at_purchased_time")
    public Double marketValueAtPurchasedTime;

    @NotNull
    @Column(name = "creation_time")
    public LocalDateTime creationTime;

    @NotNull
    @Column(name = "updated_time")
    public LocalDateTime updatedTime;

    @Transient
    public Double marketValueAtCurrentTime;

    @Transient
    public Double currentCurrencyPrice;

    @Transient
    public String currentProfitLossRate;

    public PortfolioEntry() {

    }

    public PortfolioEntry(Long id, String cryptoCurrencyName, Double amountPurchased, String walletLocation,
                          Double marketValueAtPurchasedTime, LocalDateTime creationTime, Double marketValueAtCurrentTime) {
        this.id = id;
        this.cryptoCurrencyName = cryptoCurrencyName;
        this.amountPurchased = amountPurchased;
        this.walletLocation = walletLocation;
        this.marketValueAtPurchasedTime = marketValueAtPurchasedTime;
        this.creationTime = creationTime;
        this.marketValueAtCurrentTime = marketValueAtCurrentTime;
    }

    public PortfolioEntry(String cryptoCurrencyName, Double amountPurchased, String walletLocation) {
        this.cryptoCurrencyName = cryptoCurrencyName;
        this.amountPurchased = amountPurchased;
        this.walletLocation = walletLocation;
    }

    @JSONPropertyName("id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JSONPropertyName("cryptoCurrencyName")
    public String getCryptoCurrencyName() {
        return cryptoCurrencyName;
    }

    public void setCryptoCurrencyName(String cryptoCurrencyName) {
        this.cryptoCurrencyName = cryptoCurrencyName;
    }

    @JSONPropertyName("amountPurchased")
    public Double getAmountPurchased() {
        return amountPurchased;
    }

    public void setAmountPurchased(Double amountPurchased) {
        this.amountPurchased = amountPurchased;
    }

    @JSONPropertyName("walletLocation")
    public String getWalletLocation() {
        return walletLocation;
    }

    public void setWalletLocation(String walletLocation) {
        this.walletLocation = walletLocation;
    }

    @JSONPropertyName("marketValueAtPurchasedTime")
    public Double getMarketValueAtPurchasedTime() {
        return marketValueAtPurchasedTime;
    }

    public void setMarketValueAtPurchasedTime(Double marketValueAtPurchasedTime) {
        this.marketValueAtPurchasedTime = marketValueAtPurchasedTime;
        setCurrentProfitLossRate();
    }

    @JSONPropertyName("creationTime")
    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    @JSONPropertyName("marketValueAtCurrentTime")
    public Double getMarketValueAtCurrentTime() {
        return marketValueAtCurrentTime;
    }

    public void setMarketValueAtCurrentTime(Double marketValueAtCurrentTime) {
        this.marketValueAtCurrentTime = marketValueAtCurrentTime;
        setCurrentProfitLossRate();
    }

    @JSONPropertyName("updatedTime")
    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    @JSONPropertyName("currentCurrencyPrice")
    public Double getCurrentCurrencyPrice() {
        return currentCurrencyPrice;
    }

    public void setCurrentCurrencyPrice(Double currentCurrencyPrice) {
        this.currentCurrencyPrice = currentCurrencyPrice;
    }

    @JSONPropertyName("currentProfitLossRate")
    public String getCurrentProfitLossRate() {
        return currentProfitLossRate;
    }

    public void setCurrentProfitLossRate() {
        if(this.marketValueAtCurrentTime != null && this.marketValueAtPurchasedTime != null) {

            double realDiff = this.marketValueAtCurrentTime - this.marketValueAtPurchasedTime;
            double diff = Math.abs(realDiff);
            double profitLossRate = (diff * 100) / this.marketValueAtPurchasedTime;
            double diffSignum = Math.signum(realDiff);
            profitLossRate *= diffSignum;
            String formattedRate = new DecimalFormat("########0.0###").format(profitLossRate);
            this.currentProfitLossRate = formattedRate + "%";
        }
    }

    public void updatePortfolioByAnother(PortfolioEntry portfolioEntry){
        if(portfolioEntry.getAmountPurchased() != null) {
            this.amountPurchased = portfolioEntry.getAmountPurchased();
        }

        if(portfolioEntry.getWalletLocation() != null) {
            this.walletLocation = portfolioEntry.getWalletLocation();
        }

        if(portfolioEntry.getCryptoCurrencyName() != null) {
            this.cryptoCurrencyName = portfolioEntry.getCryptoCurrencyName();
        }
    }

    @PrePersist
    public void setCreationDateTime(){
        this.creationTime = LocalDateTime.now();
        this.updatedTime = this.creationTime;
    }

    @PreUpdate
    public void setUpdatedDateTimeAtUpdate(){
        this.updatedTime = LocalDateTime.now();
    }


    @Override
    public String toString() {
        return toJsonObject().toString();
    }

    public JSONObject toJsonObject(){
        return new JSONObject(this);
    }
}
