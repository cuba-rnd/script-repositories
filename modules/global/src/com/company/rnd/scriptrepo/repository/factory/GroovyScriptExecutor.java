package com.company.rnd.scriptrepo.repository.factory;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.sys.AbstractScripting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class GroovyScriptExecutor implements ScriptExecutor {

    private static final Logger log = LoggerFactory.getLogger(GroovyScriptExecutor.class);

    @Override
    public Object eval(String script, Object[] params) {
        AbstractScripting scripting = AppBeans.get(AbstractScripting.class);
        return scripting.evaluateGroovy(script, new HashMap<>());
    }
}
