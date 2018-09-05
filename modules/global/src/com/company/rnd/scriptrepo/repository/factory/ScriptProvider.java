package com.company.rnd.scriptrepo.repository.factory;

import java.lang.reflect.Method;

public interface ScriptProvider {

    String getScript(Method method);

}
