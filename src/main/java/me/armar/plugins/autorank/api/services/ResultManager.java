package me.armar.plugins.autorank.api.services;

import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;

import java.util.List;
import java.util.Optional;

public interface ResultManager {
    boolean registerResult(String var1, Class<? extends AbstractResult> var2);

    boolean unRegisterResult(String var1);

    List<Class<? extends AbstractResult>> getRegisteredResults();

    Optional<Class<? extends AbstractResult>> getResult(String var1);
}
