package com.frkn.crypto.tracker.bitfinex.helper;

import com.frkn.crypto.tracker.bitfinex.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CurrencyPairConverter {


    /**
     * Buggy implementation. Use public GraphCurrencyPath findRelatedPairsToConvertTheCurrencyToAnotherCurrency(String currency, String targetCurrency, Graph currencyGraph)
     * Left here for brain-storming and improve ideas on how to calculate cross coin prices
     * */
    public Pair findRelatedPairsToConvertTheCurrencyToAnotherCurrency(
            String currency, String targetCurrency, List<Pair> pairs
    ){
        Pair pairToBeCalculated = new Pair(currency, targetCurrency);

        if(currency.toLowerCase().equals(targetCurrency.toLowerCase())){
            return pairToBeCalculated;
        }

        int pairsSize = pairs.size();
        System.out.println(pairToBeCalculated.getDividendCurrency().getLabel() + " " + pairToBeCalculated.getDivisorCurrency().getLabel());
        Boolean foundACalculationPath = false;
        for (int currentPairIndex = 0; currentPairIndex < pairsSize; currentPairIndex++) {
            Pair currentPair = pairs.get(currentPairIndex);
            System.out.println(currentPair);
            if(pairToBeCalculated.ifRelatedToEachOther(currentPair)) {
                foundACalculationPath = startTraversingFromTheRelatedPairToFindACalculationPath(pairToBeCalculated,
                        currentPair, currentPair);
            }
            if(foundACalculationPath){
                break;
            }
            pairs.forEach(pair -> pair.setPassed(false));
        }

        return foundACalculationPath ? pairToBeCalculated : null;
    }

    public GraphCurrencyPath findRelatedCurrenciesToConvertTheCurrencyToAnotherCurrency(
            String currency, String targetCurrency, Graph currencyGraph
    ){
        Currency currencyAsCurrency = new Currency(currency);
        Currency targetCurrencyAsCurrency = new Currency(targetCurrency);
        GraphCurrency graphFromCurrency = currencyGraph.getGraphNodes().get(currencyAsCurrency);
        GraphCurrency graphTargetCurrency = currencyGraph.getGraphNodes().get(targetCurrencyAsCurrency);

        GraphCurrencyPath gcp = new GraphCurrencyPath(graphFromCurrency,graphTargetCurrency);

        if(graphFromCurrency == null || graphTargetCurrency == null){
            return gcp;
        }

        startTraversingFromTheRelatedGraphCurrencyToFindACalculationPath(gcp, graphFromCurrency, null);
        return gcp;
    }

    private void startTraversingFromTheRelatedGraphCurrencyToFindACalculationPath(GraphCurrencyPath path,
                                                                                  GraphCurrency relatedGraphCurrency, GraphCurrency previousRelatedGraphCurrency){

        if(!path.getPath().contains(relatedGraphCurrency)) {
            path.addGraphCurrencyToPath(relatedGraphCurrency);
        }

        if(path.canCalculatePrice()){
            return;
        }
        else {
            relatedGraphCurrency.getConnections().forEach(graphCurrency -> {
                if (!path.getPath().contains(graphCurrency) && !path.canCalculatePrice()) {
                    startTraversingFromTheRelatedGraphCurrencyToFindACalculationPath(path, graphCurrency, relatedGraphCurrency);
                }
            });

            if(!path.canCalculatePrice()) {
                path.removeGraphCurrencyFromPath(relatedGraphCurrency);
            }
        }


    }

    /**
     * @Deprecated see startTraversingFromTheRelatedGraphCurrencyToFindACalculationPath(GraphCurrencyPath path, GraphCurrency relatedGraphCurrency, GraphCurrency previousRelatedGraphCurrency){
     * */
    private Boolean startTraversingFromTheRelatedPairToFindACalculationPath(Pair pairToBeCalculated, Pair relatedPair,
                                                                            Pair previousRelatedPair){
        System.out.println("--" + relatedPair);
        if(!pairToBeCalculated.containsPair(relatedPair)){
            pairToBeCalculated.addPossibleConvertablePair(relatedPair);
        }

        if(pairToBeCalculated.canCalculatePrice()){
            return true;
        }

        Boolean canCalculate = false;
        if(!relatedPair.getPassed()) {
            relatedPair.setPassed(true);
            for (Pair relatedPairOfRelatedPair : relatedPair.getPossibleConvertablePairs()) {
                if (!relatedPairOfRelatedPair.equals(previousRelatedPair)
                        && !relatedPair.equals(relatedPairOfRelatedPair)
                        && !pairToBeCalculated.containsPair(relatedPairOfRelatedPair)
                        /*&& pairToBeCalculated.ifRelatedToEachOther(relatedPairOfRelatedPair)*/) {
                    canCalculate = startTraversingFromTheRelatedPairToFindACalculationPath(pairToBeCalculated,
                            relatedPairOfRelatedPair, relatedPair);
                    if (canCalculate) {
                        break;
                    }
                }
            }
        }
        if (!canCalculate) {
//            pairToBeCalculated.removePossibleConvertablePairs(relatedPair.getPossibleConvertablePairs());
            relatedPair.setPassed(false);
            pairToBeCalculated.removePossibleConvertablePair(relatedPair);
        }

        return canCalculate;
    }

    public List<Pair> convertSymbolsToPairs(String[] symbols){
        List<Pair> pairs = new ArrayList<>();
        System.out.println(symbols.length);

        /*for (int i = 0; i < symbols.length; i++) {
            Pair pair = new Pair(symbols[i]);
            pairs.add(pair);
            System.out.print(pair + ", ");
        }*/
        //System.out.println();
        for (String symbol : symbols) {
            Pair pair = new Pair(symbol);
            pairs.add(pair);
        }
        pairs.forEach(pair -> {
            List<Pair> relatedPairs = pairs
                    .stream()
                    .filter(possibleRelatedPair -> pair.ifRelatedToEachOther(possibleRelatedPair) && !pair.equals(possibleRelatedPair))
                    .collect(Collectors.toList());
            pair.addPossibleConvertablePairs(relatedPairs);
        });
        return pairs;
    }



}
