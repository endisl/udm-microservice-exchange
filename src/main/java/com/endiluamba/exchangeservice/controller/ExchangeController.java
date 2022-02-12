package com.endiluamba.exchangeservice.controller;

import com.endiluamba.exchangeservice.model.Exchange;
import com.endiluamba.exchangeservice.repository.ExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RestController
@RequestMapping("exchange-service")
public class ExchangeController {

    @Autowired
    private Environment environment;

    @Autowired
    private ExchangeRepository repository;

    @GetMapping(value = "/{amount}/{from}/{to}")
    public Exchange getExchange(
            @PathVariable("amount")BigDecimal amount,
            @PathVariable("from") String from,
            @PathVariable("to") String to
            ) {


        var exchange = repository.findByFromAndTo(from, to);
        if (exchange == null) throw new RuntimeException("Currency Unsupported");

        var port = environment.getProperty("local.server.port");

        BigDecimal conversionFactor = exchange.getConversionFactor();
        BigDecimal convertedValue = conversionFactor.multiply(amount);

        exchange.setEnvironment(port);
        exchange.setConvertedValue(convertedValue.setScale(2, RoundingMode.CEILING));

        return exchange;
    }

}
