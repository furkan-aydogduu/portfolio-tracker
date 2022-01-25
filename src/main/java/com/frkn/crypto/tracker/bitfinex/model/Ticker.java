package com.frkn.crypto.tracker.bitfinex.model;

public class Ticker {

    private Double mid;
    private Double bid;
    private Double ask;
    private Double lastPrice;
    private Double low;
    private Double high;
    private Double volume;
    private Double timestamp;

    public Ticker() {
    }

    public Ticker(Double mid, Double bid, Double ask, Double lastPrice, Double low, Double high, Double volume, Double timestamp) {
        this.mid = mid;
        this.bid = bid;
        this.ask = ask;
        this.lastPrice = lastPrice;
        this.low = low;
        this.high = high;
        this.volume = volume;
        this.timestamp = timestamp;
    }

    public Double getMid() {
        return mid;
    }

    public void setMid(Double mid) {
        this.mid = mid;
    }

    public Double getBid() {
        return bid;
    }

    public void setBid(Double bid) {
        this.bid = bid;
    }

    public Double getAsk() {
        return ask;
    }

    public void setAsk(Double ask) {
        this.ask = ask;
    }

    public Double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(Double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Double timestamp) {
        this.timestamp = timestamp;
    }
}
