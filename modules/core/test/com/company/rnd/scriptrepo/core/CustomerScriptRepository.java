package com.company.rnd.scriptrepo.core;

import com.company.rnd.scriptrepo.repository.ScriptRepository;
import com.company.rnd.scriptrepo.repository.Scripted;

import java.util.UUID;

@ScriptRepository
public interface CustomerScriptRepository {

    @Scripted
    String renameCustomer(UUID customerId, String newName);

}
