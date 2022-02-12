package com.endiluamba.exchangeservice.repository;

import com.endiluamba.exchangeservice.model.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRepository  extends JpaRepository<Exchange, Long> {

    Exchange findByFromAndTo(String from, String to);
}
