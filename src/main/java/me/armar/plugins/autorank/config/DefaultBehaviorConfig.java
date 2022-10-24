package me.armar.plugins.autorank.config;

import me.armar.plugins.autorank.Autorank;

public class DefaultBehaviorConfig extends AbstractConfig {
    public DefaultBehaviorConfig(Autorank instance) {
        this.setPlugin(instance);
        this.setFileName("DefaultBehavior.yml");
    }

    public boolean loadConfig() {
        boolean loaded = super.loadConfig();
        if (!loaded) {
            return false;
        } else {
            this.getConfig().options().header("This file allows you to change the default behavior of a path. \nFor example, if you change 'ALLOW_INFINITE_PATHING' to true, all paths will by default allow infinite pathing, unless specified otherwise by setting it to false for a path.\nIf you're unsure about the option, please consult the wiki about what they mean. \nMoreover, be careful with changing defaults: it can ruin your Autorank setup!");
            DefaultBehaviorOption[] var2 = DefaultBehaviorOption.values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                DefaultBehaviorOption option = var2[var4];
                this.getConfig().addDefault(option.toString(), option.getDefaultValue());
            }

            this.getConfig().options().copyDefaults(true);
            this.saveConfig();
            return true;
        }
    }

    public boolean getDefaultBooleanBehaviorOfOption(DefaultBehaviorOption option) throws IllegalArgumentException {
        if (!option.getClassType().equals(Boolean.class)) {
            throw new IllegalArgumentException("Option " + option + " is not of type boolean!");
        } else {
            return this.getConfig().getBoolean(option.toString());
        }
    }

    public int getDefaultIntegerBehaviorOfOption(DefaultBehaviorOption option) throws IllegalArgumentException {
        if (!option.getClassType().equals(Integer.class)) {
            throw new IllegalArgumentException("Option " + option + " is not of type integer!");
        } else {
            return this.getConfig().getInt(option.toString());
        }
    }

    public String getDefaultStringBehaviorOfOption(DefaultBehaviorOption option) throws IllegalArgumentException {
        if (!option.getClassType().equals(String.class)) {
            throw new IllegalArgumentException("Option " + option + " is not of type String!");
        } else {
            return this.getConfig().getString(option.toString());
        }
    }
}
