package com.endiluamba.exchangeservice.controller;

import com.endiluamba.exchangeservice.model.Exchange;
import com.endiluamba.exchangeservice.repository.ExchangeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Tag(name = "Exchange endpoint")
@RestController
@RequestMapping("exchange-service")
public class ExchangeController {

    private Logger logger = LoggerFactory.getLogger(ExchangeController.class);

    @Autowired
    private Environment environment;

    @Autowired
    private ExchangeRepository repository;

    @GetMapping(value = "/{amount}/{from}/{to}")
    @Operation(summary = "Get exchange from currency")
    public Exchange getExchange(
            @PathVariable("amount")BigDecimal amount,
            @PathVariable("from") String from,
            @PathVariable("to") String to
            ) {

        logger.info("getExchange is called with -> {}, {}, {}", amount, from, to);

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
