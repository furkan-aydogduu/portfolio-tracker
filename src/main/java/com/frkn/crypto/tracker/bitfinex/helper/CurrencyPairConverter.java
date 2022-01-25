package com.frkn.crypto.tracker.bitfinex.helper;

import com.frkn.crypto.tracker.bitfinex.model.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CurrencyPairConverter {

    public Pair findRelatedPairsToConvertTheCurrencyToAnotherCurrency(
            String currency, String targetCurrency, List<Pair> pairs
    ){
        Pair pairToBeCalculated = new Pair(currency, targetCurrency);

        if(currency.toLowerCase().equals(targetCurrency.toLowerCase())){
            return pairToBeCalculated;
        }

        int pairsSize = pairs.size();
        Boolean foundACalculationPath = false;
        for (int currentPairIndex = 0; currentPairIndex < pairsSize; currentPairIndex++) {
            Pair currentPair = pairs.get(currentPairIndex);
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

    private Boolean startTraversingFromTheRelatedPairToFindACalculationPath(Pair pairToBeCalculated, Pair relatedPair,
                                                                            Pair previousRelatedPair){
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
                        && pairToBeCalculated.ifRelatedToEachOther(relatedPairOfRelatedPair)) {
                    canCalculate = startTraversingFromTheRelatedPairToFindACalculationPath(pairToBeCalculated,
                            relatedPairOfRelatedPair, relatedPair);
                    if (canCalculate) {
                        break;
                    }
                }
            }
        }
        if (!canCalculate) {
            pairToBeCalculated.removePossibleConvertablePairs(relatedPair.getPossibleConvertablePairs());
        }

        return canCalculate;
    }

    public List<Pair> convertSymbolsToPairs(String[] symbols){
        List<Pair> pairs = new ArrayList<>();
        for (String symbol : symbols) {
            Pair pair = new Pair(symbol);
            pairs.add(pair);
        }
        pairs.forEach(pair -> {
            List<Pair> relatedPairs = pairs
                    .stream()
                    .filter(possibleRelatedPair -> pair.ifRelatedToEachOther(possibleRelatedPair))
                    .collect(Collectors.toList());
            pair.addPossibleConvertablePairs(relatedPairs);
        });
        return pairs;
    }

}
