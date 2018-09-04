package com.company.rnd.scriptrepo.service;

import com.company.rnd.scriptrepo.repository.GroovyScript;
import com.company.rnd.scriptrepo.repository.ScriptRepository;

import java.util.UUID;

@ScriptRepository
public interface CustomerScriptRepository {


    @GroovyScript(scriptUrl = "helloworld.groovy")
    Object RenameCustomer(UUID customerId, String newName);


}
