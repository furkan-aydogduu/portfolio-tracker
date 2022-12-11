package com.frkn.crypto.tracker.bitfinex.model;

import java.util.LinkedHashSet;

public class GraphCurrencyPath {

    private LinkedHashSet<GraphCurrency> path;
    private GraphCurrency fromCurrency;
    private GraphCurrency targetCurrency;

    public GraphCurrencyPath(){
        this.path = new LinkedHashSet<>();
    }

    public GraphCurrencyPath(GraphCurrency fromCurrency, GraphCurrency targetCurrency){
        this.path = new LinkedHashSet<>();
        this.fromCurrency = fromCurrency;
        this.targetCurrency = targetCurrency;
    }

    public void addGraphCurrencyToPath(GraphCurrency graphCurrency){
        this.path.add(graphCurrency);
    }

    public void removeGraphCurrencyFromPath(GraphCurrency graphCurrency){
        this.path.remove(graphCurrency);
    }

    public LinkedHashSet<GraphCurrency> getPath() {
        return path;
    }

    public GraphCurrency getFromCurrency() {
        return fromCurrency;
    }

    public GraphCurrency getTargetCurrency() {
        return targetCurrency;
    }

    public Boolean canCalculatePrice(){
        if(this.path.contains(this.fromCurrency) && this.path.contains(this.targetCurrency)){
            return true;
        }
        return false;
    }
}
