package com.company.rnd.scriptrepo.repository.factory;

import com.company.rnd.scriptrepo.repository.ScriptRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class ScriptRepositoryRegistry {

    private Set<Class<?>> repositories = new HashSet<>();

    public void register(Class<?> scriptRepository){
        if (scriptRepository.getAnnotation(ScriptRepository.class) == null) {
            throw new IllegalArgumentException("Script repositories must be annotated with @ScriptRepository.");
        }
        repositories.add(scriptRepository);
    }

    public Set<Class<?>> getScriptRepositories() {
        return Collections.unmodifiableSet(repositories);
    }

}
