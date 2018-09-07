package com.company.rnd.scriptrepo.repository.factory;

public interface ScriptProvider {

    String getScript(Class<?> scriptRepositoryClass, String methodName);

}
