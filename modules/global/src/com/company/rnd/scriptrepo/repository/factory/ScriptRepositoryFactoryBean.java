package com.company.rnd.scriptrepo.repository.factory;

import com.company.rnd.scriptrepo.repository.GroovyScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component
public class ScriptRepositoryFactoryBean {

    private static final Logger log = LoggerFactory.getLogger(ScriptRepositoryFactoryBean.class);

    @SuppressWarnings("unchecked")
    public <T> T createRepository(Class<T> repositoryClass) {
        log.trace("Creating proxy for {}", repositoryClass.getName());
        return (T)Proxy.newProxyInstance(repositoryClass.getClassLoader(),
                new Class<?>[]{repositoryClass}, new RepositoryMethodsHandler<T>(repositoryClass)) ;
    }


    static class RepositoryMethodsHandler<T> implements InvocationHandler, Serializable {

        private static final Logger log = LoggerFactory.getLogger(RepositoryMethodsHandler.class);

        private Class<T> repositoryClass;

        RepositoryMethodsHandler(Class<T> repositoryClass) {
            this.repositoryClass = repositoryClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            log.debug("Class: {}, Proxy: {}, Method: {}, Args: {}", repositoryClass.getName(), proxy.getClass(), method.getName(), args);

            Method interfaceMethod = repositoryClass.getMethod(method.getName(), method.getParameterTypes());

            GroovyScript annotationConfig = interfaceMethod.getAnnotation(GroovyScript.class);

            ScriptProvider provider = annotationConfig.providerClass().newInstance();

            ScriptExecutor evaluator = annotationConfig.executorClass().newInstance();

            return evaluator.eval(provider.getScript(annotationConfig.scriptUrl()), args);
        }
    }

}
