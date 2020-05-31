package com.company.rnd.scriptrepo.core.test.database;

import com.company.rnd.scriptrepo.AppTestContainer;
import com.company.rnd.scriptrepo.core.test.files.FileRepositoryTest;
import com.company.rnd.scriptrepo.entity.PersistentScript;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scripting.ScriptSource;

import java.math.BigDecimal;

public class DatabaseRepositoryTest {

    @ClassRule
    public static AppTestContainer cont = AppTestContainer.Common.INSTANCE;

    private Metadata metadata;
    private Persistence persistence;
    private TestTaxService taxService;
    private PersistentScript script;

    @Before
    public void setUp() throws Exception {
        metadata = cont.metadata();
        persistence = cont.persistence();
        persistence.setSoftDeletion(false);
    }

    @After
    public void tearDown() throws Exception {
        persistence.runInTransaction(em -> em.remove(script));
    }

    @Test
    public void given_aScriptIsExistingInDb_when_theScriptRepositoryInterfaceIsExecuted_then_theDbScriptIsEvaluated() {

        // given:
        script = createDbScript(
                "TestTaxCalculator.calculateTax",
                "return amount*0.13"
        );

        // and:
        taxService = AppBeans.get(TestTaxService.class);

        // when:
        BigDecimal result = taxService.calculateTaxAmount(BigDecimal.TEN);

        // then:
        Assert.assertTrue(result.compareTo(BigDecimal.valueOf(1.4)) < 0);
    }

    private PersistentScript createDbScript(String scriptName, String scriptSourceText) {
        PersistentScript script = metadata.create(PersistentScript.class);

        script.setName(scriptName);
        script.setSourceText(scriptSourceText);

        persistence.runInTransaction(em -> em.persist(script));

        return script;
    }

}
