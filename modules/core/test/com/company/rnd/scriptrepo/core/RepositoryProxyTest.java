package com.company.rnd.scriptrepo.core;

import com.company.rnd.scriptrepo.AppTestContainer;
import com.company.rnd.scriptrepo.core.test.data.Customer;
import com.company.rnd.scriptrepo.core.test.data.CustomerScriptRepository;
import com.company.rnd.scriptrepo.repository.factory.ScriptRepositoryFactoryBean;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RepositoryProxyTest {

    private static final Logger log = LoggerFactory.getLogger(RepositoryProxyTest.class);

    @ClassRule
    public static AppTestContainer cont = AppTestContainer.Common.INSTANCE;

    private Metadata metadata;
    private Persistence persistence;
    private DataManager dataManager;
    private ScriptRepositoryFactoryBean repoFactory;

    @Before
    public void setUp() throws Exception {
        metadata = cont.metadata();
        persistence = cont.persistence();
        dataManager = AppBeans.get(DataManager.class);
        repoFactory = AppBeans.get(ScriptRepositoryFactoryBean.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testRunSimpleScript() {
        CustomerScriptRepository repo = repoFactory.createRepository(CustomerScriptRepository.class);
        UUID customerId = UUID.randomUUID();
        String newName = RandomStringUtils.randomAlphabetic(8);
        String s = repo.renameCustomer(customerId, newName);
        log.info(s);
        assertNotNull(s);
        assertTrue(s.contains(customerId.toString()));
        assertTrue(s.contains(newName));
    }

    @Test
    public void testCreateObject() throws ParseException {
        CustomerScriptRepository repo = repoFactory.createRepository(CustomerScriptRepository.class);
        String newName = RandomStringUtils.randomAlphabetic(8);
        Date birthDate = DateUtils.parseDate("1988-12-14", new String[]{"yyyy-MM-dd"});
        Customer c = repo.createCustomer(newName, birthDate);
        assertEquals(newName, c.getName());
        assertEquals(birthDate, c.getBirthDate());
        assertNotNull(c.getId());
    }

}