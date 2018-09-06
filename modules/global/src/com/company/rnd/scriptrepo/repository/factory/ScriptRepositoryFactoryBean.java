package com.company.rnd.scriptrepo.repository.factory;

import com.company.rnd.scriptrepo.repository.ScriptMethod;
import com.company.rnd.scriptrepo.repository.ScriptParam;
import com.company.rnd.scriptrepo.repository.ScriptRepository;
import com.haulmont.cuba.core.sys.AppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Component
public class ScriptRepositoryFactoryBean {

    private static final Logger log = LoggerFactory.getLogger(ScriptRepositoryFactoryBean.class);

    @SuppressWarnings("unchecked")
    public <T> T createRepository(Class<T> repositoryClass) {
        if (!repositoryClass.isAnnotationPresent(ScriptRepository.class)) {
            throw new IllegalArgumentException("Script repositories must be annotated with @ScriptRepository.");
        }

        log.trace("Creating proxy for {}", repositoryClass.getName());
        RepositoryMethodsHandler<T> handler = new RepositoryMethodsHandler<>(repositoryClass);
        T proxyInstance = (T) Proxy.newProxyInstance(repositoryClass.getClassLoader(),
                new Class<?>[]{repositoryClass}, handler);
        return proxyInstance;
    }

    static class RepositoryMethodsHandler<T> implements InvocationHandler, Serializable {

        private static final Logger log = LoggerFactory.getLogger(RepositoryMethodsHandler.class);

        private Map<Method, ScriptInfo> methodScriptInfoMap = new ConcurrentHashMap<>();

        RepositoryMethodsHandler(Class<T> repositoryClass) {
            Method[] methods = repositoryClass.getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(ScriptMethod.class)) {
                    ScriptMethod annotationConfig = method.getAnnotation(ScriptMethod.class);
                    ScriptProvider provider = AppContext.getApplicationContext().getBean(annotationConfig.providerClass());
                    ScriptExecutor executor = AppContext.getApplicationContext().getBean(annotationConfig.executorClass());
                    methodScriptInfoMap.put(method, new ScriptInfo(provider, executor));
                }
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            log.debug("Class: {}, Proxy: {}, Method: {}, Args: {}", method.getDeclaringClass().getName(), proxy.getClass(), method.getName(), args);
            ScriptInfo scriptInfo = methodScriptInfoMap.get(method);
            ScriptProvider provider = scriptInfo.provider;
            ScriptExecutor executor = scriptInfo.executor;
            String[] paramNames = Arrays.stream(method.getParameters())
                    .map(getParameterName())
                    .toArray(String[]::new);
            String script = provider.getScript(method.getDeclaringClass(), method.getName());
            return executor.eval(script, method.getName(), paramNames, args);
        }

        private Function<Parameter, String> getParameterName() {
            return p -> p.isAnnotationPresent(ScriptParam.class)
                    ? p.getAnnotation(ScriptParam.class).value()
                    : p.getName();
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
