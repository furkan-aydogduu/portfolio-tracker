package com.frkn.crypto.tracker.bitfinex.model;

import com.frkn.crypto.tracker.model.PortfolioEntry;

import java.util.*;
import java.util.stream.Collectors;

public class Pair {

    private String legacyPairString;
    private Currency dividendCurrency;
    private Currency divisorCurrency;
    private Double pairPrice;

    private LinkedHashSet<Pair> possibleConvertablePairs;
    private Boolean passed;

    public Pair(String pairAsString) {
        this.pairPrice = 0.0;
        this.legacyPairString = pairAsString;
        this.parseSymbol(pairAsString);
        this.possibleConvertablePairs = new LinkedHashSet<>();
        this.passed = false;
    }

    public Pair(String dividendCurrency, String divisorCurrency) {
        this.pairPrice = 0.0;
        this.dividendCurrency = new Currency(dividendCurrency.toLowerCase());
        this.divisorCurrency = new Currency(divisorCurrency.toLowerCase());
        this.possibleConvertablePairs = new LinkedHashSet<>();
    }

    public Pair(Pair pair) {
        this.pairPrice = pair.getPairPrice();
        this.dividendCurrency = new Currency(pair.getDividendCurrency());
        this.divisorCurrency = new Currency(pair.getDivisorCurrency());
        this.possibleConvertablePairs = new LinkedHashSet<>();
    }

    public Currency getDividendCurrency() {
        return dividendCurrency;
    }

    public void setDividendCurrency(Currency dividendCurrency) {
        this.dividendCurrency = dividendCurrency;
    }

    public Currency getDivisorCurrency() {
        return divisorCurrency;
    }

    public void setDivisorCurrency(Currency divisorCurrency) {
        this.divisorCurrency = divisorCurrency;
    }

    public LinkedHashSet<Pair> getPossibleConvertablePairs() {
        return possibleConvertablePairs;
    }

    public void setPossibleConvertablePairs(LinkedHashSet<Pair> possibleConvertablePairs) {
        this.possibleConvertablePairs = possibleConvertablePairs;
    }

    public void addPossibleConvertablePair(Pair possibleConvertablePair){
        this.possibleConvertablePairs.add(possibleConvertablePair);
    }

    public void addPossibleConvertablePairs(List<Pair> possibleConvertablePairs){
        this.possibleConvertablePairs.addAll(possibleConvertablePairs);
    }

    public void addPossibleConvertablePairs(LinkedHashSet<Pair> possibleConvertablePairs){
        this.possibleConvertablePairs.addAll(possibleConvertablePairs);
    }

    public void removePossibleConvertablePair(Pair possibleConvertablePair){
        this.possibleConvertablePairs.remove(possibleConvertablePair);
    }

    public void removePossibleConvertablePairs(LinkedHashSet<Pair> possibleConvertablePairs){
        this.possibleConvertablePairs.removeAll(possibleConvertablePairs);
    }

    public String getLegacyPairString() {
        return legacyPairString;
    }

    public void setLegacyPairString(String legacyPairString) {
        this.legacyPairString = legacyPairString;
    }

    public Double getPairPrice() {
        return pairPrice;
    }

    public void setPairPrice(Double pairPrice) {
        this.pairPrice = pairPrice;
    }

    public Boolean getPassed() {
        return passed;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
    }

    public void setChildrenUnPassed(){
        this.possibleConvertablePairs.forEach(
                pair -> pair.setPassed(false)
        );
    }

    private void parseSymbol(String pairAsString){
        if(pairAsString.contains(":")){
            String[] splittedPairs = pairAsString.split(":");
            this.dividendCurrency = new Currency(splittedPairs[0].toLowerCase());
            this.divisorCurrency = new Currency(splittedPairs[1].toLowerCase());
        }
        else{
            this.dividendCurrency = new Currency(pairAsString.substring(0,3).toLowerCase());
            this.divisorCurrency = new Currency(pairAsString.substring(3,6).toLowerCase());
        }
    }

    public Boolean containsPair(Pair pair){
        return /*this.possibleConvertablePairs
                .stream()
                .anyMatch( relatedPair ->
                        (
                                relatedPair.getDividendCurrency().equals(pair.getDividendCurrency())
                                && relatedPair.getDivisorCurrency().equals(pair.getDividendCurrency())
                        )
                        ||
                        (
                                (
                                    this.dividendCurrency.equals(relatedPair.getDividendCurrency())
                                            && relatedPair.getDividendCurrency().equals(pair.getDividendCurrency())
                                )
                                ||
                                (
                                    this.dividendCurrency.equals(relatedPair.getDividendCurrency())
                                            && relatedPair.getDividendCurrency().equals(pair.getDivisorCurrency())
                                )
                                ||
                                (
                                    this.dividendCurrency.equals(relatedPair.getDivisorCurrency())
                                            && relatedPair.getDivisorCurrency().equals(pair.getDividendCurrency())
                                )
                                ||
                                (
                                    this.dividendCurrency.equals(relatedPair.getDivisorCurrency())
                                            && relatedPair.getDivisorCurrency().equals(pair.getDivisorCurrency())
                                )
                        )
                );*/

                this.possibleConvertablePairs
                        .stream()
                        .anyMatch( relatedPair ->
                                (
                                        relatedPair.getDividendCurrency().equals(pair.getDividendCurrency())
                                                && relatedPair.getDivisorCurrency().equals(pair.getDivisorCurrency())
                                )
                                ||
                                (
                                        relatedPair.getDividendCurrency().equals(pair.getDivisorCurrency())
                                                && relatedPair.getDivisorCurrency().equals(pair.getDividendCurrency())
                                )

                        );
    }

    public Boolean ifRelatedToEachOther(Pair pair){
        return  (       this.getDivisorCurrency().equals(pair.getDividendCurrency())
                    || this.getDividendCurrency().equals(pair.getDivisorCurrency())
                    || this.getDividendCurrency().equals(pair.getDividendCurrency())
                    || this.getDivisorCurrency().equals(pair.getDivisorCurrency())
                )
                /*&&
                !(       this.getDividendCurrency().equals(pair.getDividendCurrency())
                        && this.getDivisorCurrency().equals(pair.getDivisorCurrency())
                )*/;
    }

    public Boolean isCurrencyItself(){
        return this.getDividendCurrency().equals(this.getDivisorCurrency());

    }

    public Boolean canCalculatePrice(){

        LinkedHashSet<Currency> combinedResult = this.possibleConvertablePairs.stream()
                .map(pair -> {
                    LinkedHashSet<Currency> _result = new LinkedHashSet<>();
                    _result.add(pair.getDividendCurrency());
                    _result.add(pair.getDivisorCurrency());
                    return _result;
                }).collect(LinkedHashSet::new, (currencies, currencies2) -> {
                    LinkedHashSet<Currency> combined = new LinkedHashSet<>();
                    combined.addAll(currencies);
                    combined.addAll(currencies2);
                    currencies.retainAll(currencies2);
                    combined.removeAll(currencies);
                    currencies.clear();
                    currencies.addAll(combined);
                }, LinkedHashSet::addAll);

        return (
                       combinedResult.size() == 2
                    && combinedResult.contains(this.dividendCurrency)
                    && combinedResult.contains(this.divisorCurrency)
                )
                ||
                combinedResult.size() == 0
                ;
    }

    public Double calculatePrice(PortfolioEntry portfolioEntry){
        Double amountPurchased = Double.valueOf(portfolioEntry.getAmountPurchased());

        //which that is the portfolio currency (btc, eth.. etc.)
        Currency fromCurrency = this.dividendCurrency;

        List<Pair> clonedPairs = this.makeCopyOfPossibleConvertablePairs();

        Double combinedPairPrice = 1.0;
        if(clonedPairs.size() > 0) {
            combinedPairPrice = clonedPairs.get(0).getPairPrice();
        }

        int pairSize = clonedPairs.size();
        Boolean isSourcePortfolioDivisor = false;
        for (int i = 0; i < pairSize - 1; i++){
            Pair pair = clonedPairs.get(i);
            Pair nextPair = clonedPairs.get(i + 1);

            if(pair.getDivisorCurrency().equals(fromCurrency) || nextPair.getDivisorCurrency().equals(fromCurrency)){
                isSourcePortfolioDivisor = true;
            }

            if(pair.getDividendCurrency().equals(nextPair.getDividendCurrency())
                    || pair.getDivisorCurrency().equals(nextPair.getDivisorCurrency())){

                if(nextPair.getDivisorCurrency().equals(fromCurrency) && isSourcePortfolioDivisor){
                    isSourcePortfolioDivisor = false;
                }
                Currency tmpCurrency = new Currency(nextPair.getDividendCurrency());
                nextPair.setDividendCurrency(nextPair.getDivisorCurrency());
                nextPair.setDivisorCurrency(tmpCurrency);
                nextPair.setPairPrice(1.0 / nextPair.getPairPrice());
                combinedPairPrice *= nextPair.getPairPrice();
            }
            else{
                combinedPairPrice *= nextPair.getPairPrice();
            }
        }

        if(isSourcePortfolioDivisor){
           combinedPairPrice = 1.0 / combinedPairPrice;
        }

        portfolioEntry.setCurrentCurrencyPrice(combinedPairPrice);
        clonedPairs.clear();
        return amountPurchased * combinedPairPrice;
    }

    public List<Pair> makeCopyOfPossibleConvertablePairs(){
        return this.possibleConvertablePairs.stream().map(pair -> new Pair(pair)).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return Objects.equals(dividendCurrency, pair.dividendCurrency) && Objects.equals(divisorCurrency, pair.divisorCurrency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dividendCurrency, divisorCurrency);
    }

    @Override
    public String toString() {
        if(getDividendCurrency().getLabel().length() > 3 || getDivisorCurrency().getLabel().length() > 3){
            return getDividendCurrency().getLabel() + ":" + getDivisorCurrency().getLabel();
        }
        return getDividendCurrency().getLabel() + "" + getDivisorCurrency().getLabel();
    }
}
