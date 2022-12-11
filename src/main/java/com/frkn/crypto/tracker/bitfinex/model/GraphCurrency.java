package com.frkn.crypto.tracker.bitfinex.model;

import java.util.HashSet;
import java.util.Objects;

public class GraphCurrency {

    private Currency currency;
    private HashSet<GraphCurrency> connections;

    public GraphCurrency(Currency currency) {
        this.currency = currency;
        this.connections = new HashSet<>();
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void addConnection(GraphCurrency graphCurrency){
        if(!graphCurrency.equals(this)){
            this.connections.add(graphCurrency);
        }
    }

    public void setConnections(HashSet<GraphCurrency> connections){
        this.connections = connections;
    }

    public HashSet<GraphCurrency> getConnections(){
        return this.connections;
    }

    public boolean connectionExists(GraphCurrency graphCurrency){
        return this.connections.contains(graphCurrency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphCurrency currency = (GraphCurrency) o;
        return Objects.equals(getCurrency().getLabel(), currency.getCurrency().getLabel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCurrency().getLabel());
    }

    @Override
    public String toString() {
        return currency.toString();
    }
}
