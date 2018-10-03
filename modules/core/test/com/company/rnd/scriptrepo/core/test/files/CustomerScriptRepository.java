package com.company.rnd.scriptrepo.core.test.files;

import com.haulmont.cuba.security.app.Authenticated;
import com.haulmont.scripting.repository.ScriptMethod;
import com.haulmont.scripting.repository.ScriptParam;
import com.haulmont.scripting.repository.ScriptRepository;

import java.util.Date;
import java.util.UUID;

@ScriptRepository
public interface CustomerScriptRepository {

    @ScriptMethod
    String renameCustomer(@ScriptParam("customerId") UUID customerId, @ScriptParam("newName") String newName);

    @Authenticated //You may need this annotation to get scripts that might be protected by row-level security
    @ScriptMethod
    Customer createCustomer(@ScriptParam("name") String name, @ScriptParam("birthDate") Date birthDate);

}
