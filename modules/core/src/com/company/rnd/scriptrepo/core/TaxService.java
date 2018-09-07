package com.company.rnd.scriptrepo.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;

@Service
public class TaxService {

    private static final Logger log = LoggerFactory.getLogger(TaxService.class);

    @Inject
    private TaxCalculator taxCalculator;

    public BigDecimal calculateTaxAmount(BigDecimal sum){
        return taxCalculator.calculateTax(sum);
    }

}
