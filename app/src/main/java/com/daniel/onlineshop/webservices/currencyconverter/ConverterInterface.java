package com.daniel.onlineshop.webservices.currencyconverter;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ConverterInterface {

    String baseURL = "https://api.exchangeratesapi.io/";
    String baseCurrencyName = "EUR";

    @GET("latest")
    Call<Converter> getRates(@Query("base") String base);

}