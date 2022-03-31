package me.armar.plugins.autorank.statsmanager.query;

import me.armar.plugins.autorank.statsmanager.query.parameter.ParameterType;
import me.armar.plugins.autorank.statsmanager.query.parameter.StatisticParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StatisticQuery {
    private final List<StatisticParameter> parameters = new ArrayList();

    public StatisticQuery() {
    }

    public static StatisticQuery makeStatisticQuery(Object... strings) {
        StatisticQuery query = new StatisticQuery();

        for(int i = 0; i < strings.length; i += 2) {
            Object string = strings[i];
            if (string != null && strings[i + 1] != null) {
                String key = (String)string;
                String value = strings[i + 1].toString();
                query.addParameter(ParameterType.getParameterType(key), value);
            }
        }

        return query;
    }

    public void addParameter(StatisticParameter parameter) {
        this.parameters.add(parameter);
    }

    public void addParameter(ParameterType parameterType, String value) {
        this.parameters.add(StatisticParameter.createInstance(parameterType, value));
    }

    public Optional<StatisticParameter> getParameter(ParameterType parameterType) {
        return this.parameters.stream().filter((p) -> {
            return p.getParameterType().equals(parameterType);
        }).findFirst();
    }

    public void removeParameter(ParameterType parameterType) {
        this.parameters.removeIf((parameter) -> {
            return parameter.getParameterType() == parameterType;
        });
    }

    public boolean hasParameter(ParameterType type) {
        return this.getParameter(type).isPresent();
    }

    public Optional<String> getParameterValue(ParameterType type) {
        return this.getParameter(type).map(StatisticParameter::getValue);
    }
}
