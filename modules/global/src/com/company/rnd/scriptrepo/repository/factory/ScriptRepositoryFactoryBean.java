package com.company.rnd.scriptrepo.repository.factory;

import com.company.rnd.scriptrepo.repository.Scripted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ScriptRepositoryFactoryBean {

    private static final Logger log = LoggerFactory.getLogger(ScriptRepositoryFactoryBean.class);

    @Inject
    ScriptRepositoryRegistry registry;


    @SuppressWarnings("unchecked")
    public <T> T createRepository(Class<T> repositoryClass) {
        log.trace("Creating proxy for {}", repositoryClass.getName());
        RepositoryMethodsHandler<T> handler = new RepositoryMethodsHandler<>(repositoryClass);
        T proxyInstance = (T) Proxy.newProxyInstance(repositoryClass.getClassLoader(),
                new Class<?>[]{repositoryClass}, handler);
        registry.register(repositoryClass);
        return proxyInstance;
    }


    static class RepositoryMethodsHandler<T> implements InvocationHandler, Serializable {

        private static final Logger log = LoggerFactory.getLogger(RepositoryMethodsHandler.class);

        private Class<T> repositoryClass;

        private Map<Method, ScriptInfo> methodScriptInfoMap = new ConcurrentHashMap<>();

        RepositoryMethodsHandler(Class<T> repositoryClass) {
            this.repositoryClass = repositoryClass;
            Method[] methods = repositoryClass.getMethods();
            try {
                for (Method method : methods) {
                    if (method.getAnnotation(Scripted.class) != null) {
                        Scripted annotationConfig = method.getAnnotation(Scripted.class);
                        ScriptProvider provider = annotationConfig.providerClass().newInstance();
                        ScriptExecutor executor = annotationConfig.executorClass().newInstance();
                        methodScriptInfoMap.put(method, new ScriptInfo(provider, executor));
                    }
                }
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException(String.format("Cannot create proxy for interface%s", repositoryClass));
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            log.debug("Class: {}, Proxy: {}, Method: {}, Args: {}", repositoryClass.getName(), proxy.getClass(), method.getName(), args);
            ScriptInfo scriptInfo = methodScriptInfoMap.get(method);
            ScriptProvider provider = scriptInfo.provider;
            ScriptExecutor executor = scriptInfo.executor;
            String[] paramNames = Arrays.stream(method.getParameters()).map(Parameter::getName).toArray(String[]::new);
            return executor.eval(provider.getScript(method), method.getName(), paramNames, args);
        }
    }

    static class ScriptInfo {
        final ScriptProvider provider;
        final ScriptExecutor executor;

        ScriptInfo(ScriptProvider provider, ScriptExecutor executor) {
            this.provider = provider;
            this.executor = executor;
        }
    }

}
