package ru.nnl.typeTable;

import java.util.List;
import java.util.function.BiFunction;

public class Method {
    private final String name;
    private final List<String> parametersTypes;
    private final String returnType;
    private final BiFunction<Object, Object, Object> implementation;

    public Method(String name, List<String> parametersTypes, String returnType,
                  BiFunction<Object, Object, Object> implementation) {
        this.name = name;
        this.parametersTypes = parametersTypes;
        this.returnType = returnType;
        this.implementation = implementation;
    }

    public String getName() {
        return name;
    }

    public List<String> getParametersTypes() {
        return parametersTypes;
    }

    public String getReturnType() {
        return returnType;
    }

    public Object call(Object arg0, Object arg1) {
        return implementation.apply(arg0, arg1);
    }
}
