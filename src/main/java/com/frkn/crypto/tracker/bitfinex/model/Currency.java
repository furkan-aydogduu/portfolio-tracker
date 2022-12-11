package com.frkn.crypto.tracker.bitfinex.model;

import java.util.Objects;

public class Currency {

    private String label;
    public Currency() {

    }

    public Currency(String label) {
        this.label = label;
    }

    public Currency(Currency currency) {
        this.label = currency.getLabel();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return Objects.equals(label, currency.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
