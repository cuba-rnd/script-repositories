package com.company.rnd.scriptrepo.repository.factory;

import com.company.rnd.scriptrepo.repository.ScriptMethod;
import com.company.rnd.scriptrepo.repository.ScriptParam;
import com.company.rnd.scriptrepo.repository.ScriptRepository;
import com.haulmont.cuba.core.sys.AppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Component(ScriptRepositoryFactoryBean.NAME)
public class ScriptRepositoryFactoryBean implements BeanDefinitionRegistryPostProcessor {

    public static final String NAME = "scriptRepositoryFactory";

    private static final Logger log = LoggerFactory.getLogger(ScriptRepositoryFactoryBean.class);

    private List<String> basePackages = Collections.emptyList();

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        ClassPathScanningCandidateComponentProvider provider
                = new ScriptRepositoryCandidateProvider();
        for (String packageName : basePackages) {
            Set<BeanDefinition> candidateComponents = provider.findCandidateComponents(packageName);
            try {
                for (BeanDefinition definition : candidateComponents) {
                    definition.setFactoryBeanName(NAME);
                    definition.setFactoryMethodName("createRepository");
                    definition.getConstructorArgumentValues().addGenericArgumentValue(Class.forName(definition.getBeanClassName()));
                    registry.registerBeanDefinition(definition.getBeanClassName(), definition);
                }
            } catch (ClassNotFoundException e) {
                throw new BeanCreationException(e.getMessage(), e);
            }
        }
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    @SuppressWarnings("unchecked")
    <T> T createRepository(Class<T> repositoryClass) {
        if (!repositoryClass.isAnnotationPresent(ScriptRepository.class)) {
            throw new IllegalArgumentException("Script repositories must be annotated with @ScriptRepository.");
        }

        log.debug("Creating proxy for {}", repositoryClass.getName());
        RepositoryMethodsHandler<T> handler = new RepositoryMethodsHandler<>();
        return (T) Proxy.newProxyInstance(repositoryClass.getClassLoader(),
                new Class<?>[]{repositoryClass}, handler);
    }

    public List<String> getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(List<String> basePackages) {
        this.basePackages = basePackages;
    }

    static class ScriptRepositoryCandidateProvider extends ClassPathScanningCandidateComponentProvider {

        public ScriptRepositoryCandidateProvider() {
            super(false);
            addIncludeFilter(new AnnotationTypeFilter(ScriptRepository.class));
        }

        @Override
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            AnnotationMetadata metadata = beanDefinition.getMetadata();
            return metadata.isInterface() && metadata.isIndependent();
        }
    }

    static class RepositoryMethodsHandler<T> implements InvocationHandler, Serializable {

        private final Object defaultDelegate = new Object();

        private static final Logger log = LoggerFactory.getLogger(RepositoryMethodsHandler.class);

        private Map<Method, ScriptInfo> methodScriptInfoMap = new ConcurrentHashMap<>();

        private ScriptInfo createMethodInfo(Method method) {
            ScriptMethod annotationConfig = method.getAnnotation(ScriptMethod.class);
            ScriptProvider provider = AppContext.getApplicationContext().getBean(annotationConfig.providerClass());
            ScriptExecutor executor = AppContext.getApplicationContext().getBean(annotationConfig.executorClass());
            return new ScriptInfo(provider, executor);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (!method.isAnnotationPresent(ScriptMethod.class)) {
                return method.invoke(defaultDelegate, args);
            }
            log.debug("Class: {}, Proxy: {}, Method: {}, Args: {}", method.getDeclaringClass().getName(), proxy.getClass(), method.getName(), args);
            ScriptInfo scriptInfo = methodScriptInfoMap.computeIfAbsent(method, m -> createMethodInfo(method));
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
