package me.armar.plugins.autorank.config;

enum DefaultBehaviorOption {
    AUTO_CHOOSE_PATH(Boolean.class, true),
    PRIORITY_PATH(Integer.class, 1),
    SHOW_PATH_BASED_ON_PREREQUISITES(Boolean.class, false),
    AUTO_COMPLETE_REQUIREMENT(Boolean.class, true),
    IS_OPTIONAL_REQUIREMENT(Boolean.class, false),
    ALLOW_INFINITE_PATHING(Boolean.class, false),
    ALLOW_PARTIAL_COMPLETION(Boolean.class, true),
    STORE_PROGRESS_ON_DEACTIVATION(Boolean.class, false);

    private final Class classType;
    private final Object defaultValue;

    DefaultBehaviorOption(Class classType, Object defaultValue) {
        this.classType = classType;
        this.defaultValue = defaultValue;
    }

    public Class getClassType() {
        return this.classType;
    }

    public Object getDefaultValue() {
        return this.defaultValue;
    }
}