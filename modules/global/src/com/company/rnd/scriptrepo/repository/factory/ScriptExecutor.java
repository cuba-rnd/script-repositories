package com.company.rnd.scriptrepo.repository.factory;

public interface ScriptExecutor {

    <T> T eval(String script, String methodName, String[] argNames, Object[] args);

}
