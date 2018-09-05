package com.company.rnd.scriptrepo.core;

import com.company.rnd.scriptrepo.AppTestContainer;
import com.company.rnd.scriptrepo.repository.factory.ScriptRepositoryFactoryBean;
import com.company.rnd.scriptrepo.repository.factory.ScriptRepositoryRegistry;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
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
    private ScriptRepositoryRegistry repoRegistry;

    @Before
    public void setUp() throws Exception {
        metadata = cont.metadata();
        persistence = cont.persistence();
        dataManager = AppBeans.get(DataManager.class);
        repoFactory = AppBeans.get(ScriptRepositoryFactoryBean.class);
        repoRegistry = AppBeans.get(ScriptRepositoryRegistry.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testLoadUser() {
        CustomerScriptRepository repo = repoFactory.createRepository(CustomerScriptRepository.class);
        String s = repo.renameCustomer(UUID.randomUUID(), RandomStringUtils.randomAlphabetic(8));
        assertNotNull(s);
        assertEquals("2", s.substring(0,1));
    }

    @Test
    public void testRegistryContainsClass() {
        repoFactory.createRepository(CustomerScriptRepository.class);
        Set<Class<?>> repos =  repoRegistry.getScriptRepositories();
        assertEquals(1, repos.size());
        assertTrue(repos.contains(CustomerScriptRepository.class));
    }
}