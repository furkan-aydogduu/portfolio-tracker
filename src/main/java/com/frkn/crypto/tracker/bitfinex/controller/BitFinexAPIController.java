package com.frkn.crypto.tracker.bitfinex.controller;

import com.frkn.crypto.tracker.bitfinex.helper.CurrencyPairConverter;
import com.frkn.crypto.tracker.bitfinex.model.*;
import com.frkn.crypto.tracker.configuration.RestTemplateSecurer;
import com.frkn.crypto.tracker.model.PortfolioEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Iterator;
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
        //String[] symbols = new String[]{"htxust", "htxusd", "oneust", "aptust", "xcad:usd", "treebust", "btcusd", "oneusd", "boousd", "eurust", "aptusd"};
        List<Pair> pairs = currencyPairConverter.convertSymbolsToPairs(symbols);

        Graph currencyGraph = new Graph(pairs);

        /*currencyGraph.getGraphNodes().forEach((currency, graphCurrency) -> {
            System.out.println("-" + currency);
            graphCurrency.getConnections().forEach(graphCurrency1 -> {
                System.out.println("---" + graphCurrency1.getCurrency().getLabel());
                graphCurrency1.getConnections().forEach(graphCurrency2 -> {
                    System.out.println("-----" + graphCurrency2.getCurrency().getLabel());

                    graphCurrency2.getConnections().forEach(graphCurrency3 -> {
                        System.out.println("-------" + graphCurrency3.getCurrency().getLabel());
                    });
                });
            });
        });*/
        /*currencies.forEach(portfolio -> {
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
        });*/

        currencies.forEach(portfolio -> {
            GraphCurrencyPath currencyPath = currencyPairConverter
                    .findRelatedPairsToConvertTheCurrencyToAnotherCurrency(portfolio.getCryptoCurrencyName(), "eur", currencyGraph);

            if(currencyPath.canCalculatePrice()){
                Pair pairToConvertTheCurrencyToEuro = new Pair(portfolio.getCryptoCurrencyName(), "eur");

                Iterator<GraphCurrency> pathIterator = currencyPath.getPath().iterator();

                if(pathIterator.hasNext()) {
                    GraphCurrency currentGraphCurrency = pathIterator.next();

                    GraphCurrency nextGraphCurrency = null;
                    if(pathIterator.hasNext()) {
                        nextGraphCurrency = pathIterator.next();
                    }

                    do {
                        Pair tmpPair = new Pair(currentGraphCurrency.getCurrency().getLabel(), nextGraphCurrency.getCurrency().getLabel());
                        Pair tmpCrossedPair = new Pair(nextGraphCurrency.getCurrency().getLabel(), currentGraphCurrency.getCurrency().getLabel());

                        if (pairs.contains(tmpPair)) {
                            if (!tmpPair.isCurrencyItself()) {
                                String legacyPairString = tmpPair.toString();
                                Ticker ticker = getTicker(legacyPairString);
                                tmpPair.setPairPrice(ticker.getMid());
                            } else {
                                tmpPair.setPairPrice(1.0);
                            }
                            pairToConvertTheCurrencyToEuro.addPossibleConvertablePair(tmpPair);
                        } else if (pairs.contains(tmpCrossedPair)) {
                            if (!tmpCrossedPair.isCurrencyItself()) {
                                String legacyPairString = tmpCrossedPair.toString();
                                Ticker ticker = getTicker(legacyPairString);
                                tmpCrossedPair.setPairPrice(ticker.getMid());
                            } else {
                                tmpCrossedPair.setPairPrice(1.0);
                            }
                            pairToConvertTheCurrencyToEuro.addPossibleConvertablePair(tmpCrossedPair);
                        }

                        currentGraphCurrency = nextGraphCurrency;
                        if(pathIterator.hasNext()) {
                            nextGraphCurrency = pathIterator.next();
                        }

                        if(currentGraphCurrency.equals(nextGraphCurrency)){
                            break;
                        }
                    } while(true);
                }

                Double currentPortfolioMarketValue = pairToConvertTheCurrencyToEuro.calculatePrice(portfolio);
                portfolio.setMarketValueAtCurrentTime(currentPortfolioMarketValue);
                currencyPath.getPath().forEach(graphCurrency -> System.out.println("*" + graphCurrency));

                pairToConvertTheCurrencyToEuro.getPossibleConvertablePairs().forEach(pair -> System.out.println(pair + " : " + pair.getPairPrice()));
            }

            System.out.println("-----------------------");
        });
    }

}
