package com.company.rnd.scriptrepo.repository.factory;

public interface ScriptProvider {

    String getScript(Class<?> methodDeclaringClass, String methodName);

}
