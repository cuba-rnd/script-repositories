package com.company.rnd.scriptrepo.core.test.database;

import com.haulmont.scripting.repository.ScriptParam;
import com.haulmont.scripting.repository.ScriptRepository;

import java.math.BigDecimal;

@ScriptRepository
public interface TestTaxCalculator {

    @DbGroovyScript
    BigDecimal calculateTax(@ScriptParam("amount") BigDecimal amount);

}
