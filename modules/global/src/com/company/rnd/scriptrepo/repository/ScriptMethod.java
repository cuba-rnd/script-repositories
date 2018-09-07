package com.company.rnd.scriptrepo.repository;

import com.company.rnd.scriptrepo.repository.factory.GroovyScriptExecutor;
import com.company.rnd.scriptrepo.repository.factory.GroovyScriptProvider;
import com.company.rnd.scriptrepo.repository.factory.ScriptExecutor;
import com.company.rnd.scriptrepo.repository.factory.ScriptProvider;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ScriptMethod {

    Class<? extends ScriptProvider> providerClass() default GroovyScriptProvider.class;

    Class<? extends ScriptExecutor> executorClass() default GroovyScriptExecutor.class;

    String description() default "";

}
