package com.frkn.crypto.tracker.bitfinex.model;

import java.util.HashMap;
import java.util.List;

public class Graph {

    private HashMap<Currency, GraphCurrency> nodes;

    public Graph(){
        this.nodes = new HashMap<>();
    }

    public Graph(List<Pair> pairList){
        this.nodes = new HashMap<>();
        pairList.forEach( pair -> {
            convertPairToGraphCurrencyAndAddToGraph(pair);
        });
        buildGraph();
    }


    public void convertPairToGraphCurrencyAndAddToGraph(Pair pair){
        if(this.nodes.get(pair.getDividendCurrency()) == null){
            GraphCurrency dividendGraphCurrency = new GraphCurrency(pair.getDividendCurrency());

            for (Pair possibleConvertablePair : pair.getPossibleConvertablePairs()) {
                if(possibleConvertablePair.getDividendCurrency().equals(pair.getDividendCurrency()) || possibleConvertablePair.getDivisorCurrency().equals(pair.getDividendCurrency())) {
                    GraphCurrency _dividendGraphCurrency = new GraphCurrency(possibleConvertablePair.getDividendCurrency());
                    if (this.nodes.get(_dividendGraphCurrency) != null) {
                        _dividendGraphCurrency = this.nodes.get(_dividendGraphCurrency);
                    }
                    dividendGraphCurrency.addConnection(_dividendGraphCurrency);
                    GraphCurrency _divisorGraphCurrency = new GraphCurrency(possibleConvertablePair.getDivisorCurrency());
                    if (this.nodes.get(_divisorGraphCurrency) != null) {
                        _divisorGraphCurrency = this.nodes.get(_divisorGraphCurrency);
                    }
                    dividendGraphCurrency.addConnection(_divisorGraphCurrency);
                }
            }

            GraphCurrency _divisorGraphCurrency = new GraphCurrency(pair.getDivisorCurrency());
            if (this.nodes.get(_divisorGraphCurrency) != null) {
                _divisorGraphCurrency = this.nodes.get(_divisorGraphCurrency);
            }
            dividendGraphCurrency.addConnection(_divisorGraphCurrency);
            this.nodes.put(pair.getDividendCurrency(), dividendGraphCurrency);
        }

        if(this.nodes.get(pair.getDivisorCurrency()) == null){
            GraphCurrency divisorGraphCurrency = new GraphCurrency(pair.getDivisorCurrency());

            for (Pair possibleConvertablePair : pair.getPossibleConvertablePairs()) {
                if(possibleConvertablePair.getDivisorCurrency().equals(pair.getDivisorCurrency()) || possibleConvertablePair.getDividendCurrency().equals(pair.getDivisorCurrency())) {
                    GraphCurrency _dividendGraphCurrency = new GraphCurrency(possibleConvertablePair.getDividendCurrency());
                    if (this.nodes.get(_dividendGraphCurrency) != null) {
                        _dividendGraphCurrency = this.nodes.get(_dividendGraphCurrency);
                    }
                    divisorGraphCurrency.addConnection(_dividendGraphCurrency);
                    GraphCurrency _divisorGraphCurrency = new GraphCurrency(possibleConvertablePair.getDivisorCurrency());
                    if (this.nodes.get(_divisorGraphCurrency) != null) {
                        _divisorGraphCurrency = this.nodes.get(_divisorGraphCurrency);
                    }
                    divisorGraphCurrency.addConnection(_divisorGraphCurrency);
                }
            }
            GraphCurrency _dividendGraphCurrency = new GraphCurrency(pair.getDividendCurrency());
            if (this.nodes.get(_dividendGraphCurrency) != null) {
                _dividendGraphCurrency = this.nodes.get(_dividendGraphCurrency);
            }
            divisorGraphCurrency.addConnection(_dividendGraphCurrency);
            this.nodes.put(pair.getDivisorCurrency(), divisorGraphCurrency);
        }
    }

    private void buildGraph(){
        this.nodes.forEach((currency, graphCurrency) -> {
            graphCurrency.getConnections().forEach(graphCurrency1 -> {
                if(graphCurrency1.getConnections().isEmpty()){
                    graphCurrency1.setConnections(this.nodes.get(graphCurrency1.getCurrency()).getConnections());
                }
            });
        });
    }

    public HashMap<Currency, GraphCurrency> getGraphNodes(){
        return this.nodes;
    }
}
