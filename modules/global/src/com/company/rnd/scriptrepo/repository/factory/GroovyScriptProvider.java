package com.company.rnd.scriptrepo.repository.factory;

public class GroovyScriptProvider implements ScriptProvider {

    @Override
    public String getScript(String scriptPath) {
        return "println \"Hello world!\"; return \"Hello world!\"";
    }
}
