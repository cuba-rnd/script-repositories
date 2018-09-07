package com.company.rnd.scriptrepo.core.test.database;

import com.company.rnd.scriptrepo.AppTestContainer;
import com.company.rnd.scriptrepo.core.FileRepositoryTest;
import com.company.rnd.scriptrepo.core.TaxService;
import com.haulmont.cuba.core.global.AppBeans;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class DatabaseRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(FileRepositoryTest.class);

    @ClassRule
    public static AppTestContainer cont = AppTestContainer.Common.INSTANCE;

    private TaxService taxService;

    @Before
    public void setUp() throws Exception {
        taxService = AppBeans.get(TaxService.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testTaxCalculation () {
        Assert.assertTrue(taxService.calculateTaxAmount(BigDecimal.TEN).compareTo(BigDecimal.valueOf(1.4)) < 0);
    }

}
