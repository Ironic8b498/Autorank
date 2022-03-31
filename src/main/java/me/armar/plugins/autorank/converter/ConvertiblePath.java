package me.armar.plugins.autorank.converter;

import java.util.HashMap;
import java.util.Map;

class ConvertiblePath {
    private String pathName;
    private String fromGroup;
    private final Map<String, String> requirements = new HashMap();
    private final Map<String, String> results = new HashMap();

    ConvertiblePath() {
    }

    public String getPathName() {
        return this.pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public void addRequirement(String key, String value) {
        this.requirements.put(key, value);
    }

    public void addResult(String key, String value) {
        this.results.put(key, value);
    }

    public Map<String, String> getRequirements() {
        return this.requirements;
    }

    public Map<String, String> getResults() {
        return this.results;
    }

    public String getFromGroup() {
        return this.fromGroup;
    }

    public void setFromGroup(String fromGroup) {
        this.fromGroup = fromGroup;
    }
}
