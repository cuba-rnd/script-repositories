package com.company.rnd.scriptrepo.repository.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * Simple provider that gets script source from the same dir where Script
 * Repository interface is and file name equals to method name+'.groovy'.
 */
@Component
public class GroovyScriptProvider implements ScriptProvider {

    private static final Logger log = LoggerFactory.getLogger(GroovyScriptProvider.class);

    @Override
    public String getScript(Class<?> scriptRepositoryClass, String methodName) {
        String fileName = methodName + ".groovy";
        log.info("Getting groovy script: " + fileName);
        InputStream resourceAsStream = scriptRepositoryClass.getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}
