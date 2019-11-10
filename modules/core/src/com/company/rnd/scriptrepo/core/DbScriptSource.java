package com.company.rnd.scriptrepo.core;

import org.springframework.scripting.ScriptSource;
import org.springframework.util.Assert;

public class DbScriptSource implements ScriptSource {

	private String script;

	private boolean modified;

	private String className;


	/**
	 * Create a new DbScriptSource for the given script.
	 * @param script the script String
	 */
    public DbScriptSource(String script) {
        setScript(script);
    }

    /**
     * Create a new DbScriptSource for the given script.
     * @param script the script String
     * @param className the suggested class name for the script
     * (may be {@code null})
     */
    public DbScriptSource(String script, String className) {
        setScript(script);
        this.className = className;
    }

    /**
     * Set a fresh script String, overriding the previous script.
     * @param script the script String
     */
    public synchronized void setScript(String script) {
        Assert.hasText(script, "Script must not be empty");
        this.modified = !script.equals(this.script);
        this.script = script;
    }


    @Override
    public synchronized String getScriptAsString() {
        this.modified = false;
        return this.script;
    }

    @Override
    public synchronized boolean isModified() {
        return this.modified;
    }

    @Override
    public String suggestedClassName() {
        return this.className;
    }


    @Override
    public String toString() {
        return "DB script" + (this.className != null ? " [" + this.className + "]" : "");
    }


}
