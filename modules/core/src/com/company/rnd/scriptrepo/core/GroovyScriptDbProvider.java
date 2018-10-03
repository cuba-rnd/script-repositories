package com.company.rnd.scriptrepo.core;

import com.company.rnd.scriptrepo.entity.PersistentScript;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.scripting.repository.provider.ScriptProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.lang.reflect.Method;

@Component("groovyDbProvider")
public class GroovyScriptDbProvider implements ScriptProvider {

    private static final Logger log = LoggerFactory.getLogger(GroovyScriptDbProvider.class);


    @Inject
    private DataManager dataManager;

    private String getScriptTextbyName(String name) throws Exception {
        String result = dataManager.load(PersistentScript.class)
                .query("select s from scriptrepo$PersistentScript s where s.name = :name")
                .parameter("name", name).one().getSourceText();
        return result;
    }

    @Override
    public String getScript(Method method) {
        Class<?> scriptRepositoryClass = method.getDeclaringClass();
        String methodName = method.getName();
        String scriptName = scriptRepositoryClass.getSimpleName() + "." + methodName;
        String script = null;
        try {
            script = getScriptTextbyName(scriptName);
            log.trace("Scripted method name: {} text: {}", scriptName, script);
            return script;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot fetch data from the database", e);
        }
    }
}
