package com.daniel.onlineshop.webservices.currencyconverter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Converter {

    @SerializedName("rates")
    @Expose
    private RatesDetails rates;

    @SerializedName("base")
    @Expose
    private String base;

    @SerializedName("date")
    @Expose
    private String date;

    public RatesDetails getRates() {
        return rates;
    }

    public void setRates(RatesDetails rates) {
        this.rates = rates;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}