package com.company.rnd.scriptrepo.repository.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.stream.Collectors;

public class GroovyScriptProvider implements ScriptProvider {

    private static final Logger log = LoggerFactory.getLogger(GroovyScriptProvider.class);

    @Override
    public String getScript(Method method) {
        String fileName = method.getName().toLowerCase() + ".groovy";
        log.info("Getting groovy script: " + fileName);
        Class<?> declaringClass = method.getDeclaringClass();
        InputStream resourceAsStream = declaringClass.getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}
