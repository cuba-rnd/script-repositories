package com.company.rnd.scriptrepo.repository.factory;

public interface ScriptExecutor {

    Object eval(String script, Object[] params);

}
