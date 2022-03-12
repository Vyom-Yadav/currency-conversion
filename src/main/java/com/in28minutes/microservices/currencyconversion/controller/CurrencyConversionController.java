package com.in28minutes.microservices.currencyconversion.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.in28minutes.microservices.currencyconversion.beans.CurrencyConversion;
import com.in28minutes.microservices.currencyconversion.proxy.CurrencyExchangeProxy;

@RestController
public class CurrencyConversionController {

    final CurrencyExchangeProxy currencyExchangeProxy;

    @Autowired // I like mentioning explicitly
    public CurrencyConversionController(
        CurrencyExchangeProxy currencyExchangeProxy) {
        this.currencyExchangeProxy = currencyExchangeProxy;
    }

    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversion(@PathVariable String from,
        @PathVariable String quantity, @PathVariable String to) {

        final Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);

        final ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().getForEntity(
            "http://localhost:8000/currency-exchange/from/{from}/to/{to}",
            CurrencyConversion.class, uriVariables);


        final CurrencyConversion currencyConversion = responseEntity.getBody();
        assert currencyConversion != null;

        final BigDecimal bigDecimalQuantity = BigDecimal.valueOf(Integer.parseInt(quantity));
        final BigDecimal conversionMultiple = currencyConversion.getConversionMultiple();
        return new CurrencyConversion(
            currencyConversion.getId(), from, to,
            bigDecimalQuantity,
            conversionMultiple,
            conversionMultiple.multiply(bigDecimalQuantity),
            currencyConversion.getEnvironment());
    }

    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversionFeign(@PathVariable String from,
        @PathVariable String quantity, @PathVariable String to) {

        final CurrencyConversion currencyConversion = currencyExchangeProxy
            .retrieveExchangeValue(from, to);

        final BigDecimal bigDecimalQuantity = BigDecimal.valueOf(Integer.parseInt(quantity));
        final BigDecimal conversionMultiple = currencyConversion.getConversionMultiple();
        return new CurrencyConversion(
            currencyConversion.getId(), from, to,
            bigDecimalQuantity,
            conversionMultiple,
            conversionMultiple.multiply(bigDecimalQuantity),
            currencyConversion.getEnvironment() + " feign");
    }

}
