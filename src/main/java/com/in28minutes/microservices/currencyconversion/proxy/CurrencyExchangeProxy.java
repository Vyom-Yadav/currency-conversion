package com.in28minutes.microservices.currencyconversion.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.in28minutes.microservices.currencyconversion.beans.CurrencyConversion;

// @FeignClient(name = "currency-exchange", url = "localhost:8000") // hardcoded url
@FeignClient(name = "currency-exchange") // This uses eureka to figure out url (the instance to use)
public interface CurrencyExchangeProxy {

    @GetMapping("currency-exchange/from/{cur1}/to/{cur2}")
    CurrencyConversion retrieveExchangeValue(@PathVariable String cur1,
        @PathVariable String cur2);
}
