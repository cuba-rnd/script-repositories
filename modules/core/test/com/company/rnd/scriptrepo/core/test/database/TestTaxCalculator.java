package com.company.rnd.scriptrepo.core.test.database;

import com.company.rnd.scriptrepo.repository.ScriptMethod;
import com.company.rnd.scriptrepo.repository.ScriptParam;
import com.company.rnd.scriptrepo.repository.ScriptRepository;

import java.math.BigDecimal;

@ScriptRepository
public interface TestTaxCalculator {

    @ScriptMethod (providerBeanName ="groovyDbProvider")
    BigDecimal calculateTax(@ScriptParam("amount") BigDecimal amount);

}
