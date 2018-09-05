package com.company.rnd.scriptrepo.repository.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class GroovyScriptExecutor implements ScriptExecutor {

    private static final Logger log = LoggerFactory.getLogger(GroovyScriptExecutor.class);

    @Override
    @SuppressWarnings("unchecked")
    public <T> T eval(String script, String methodName, String[] paramsNames, Object[] args) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine groovy = manager.getEngineByName("groovy");
        if (paramsNames.length != args.length) {
            throw new IllegalArgumentException(String.format("Parameters and args must be the same length. Parameters: %d args: %d", paramsNames.length, args.length));
        }
        try {
            groovy.eval(script);
            return (T) ((Invocable) groovy).invokeFunction(methodName, args);
        } catch (ScriptException | NoSuchMethodException e) {
            throw new RuntimeException("Error executing script", e);
        }
    }
}
