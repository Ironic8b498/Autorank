package me.armar.plugins.autorank.api.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.builders.ResultBuilder;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;

public class ResultService implements ResultManager {

    public ResultService(Autorank instance) {
    }

    public boolean registerResult(String identifier, Class<? extends AbstractResult> resultClass) {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(resultClass);
        ResultBuilder.registerResult(identifier, resultClass);
        return true;
    }

    public boolean unRegisterResult(String identifier) {
        Objects.requireNonNull(identifier);
        return ResultBuilder.unRegisterResult(identifier);
    }

    public List<Class<? extends AbstractResult>> getRegisteredResults() {
        return ResultBuilder.getRegisteredResults();
    }

    public Optional<Class<? extends AbstractResult>> getResult(String identifier) {
        return ResultBuilder.getRegisteredResult(identifier);
    }
}