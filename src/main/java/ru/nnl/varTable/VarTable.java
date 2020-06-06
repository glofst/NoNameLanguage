package ru.nnl.varTable;

import java.util.HashMap;
import java.util.Map;

public class VarTable {

    private final HashMap<String, VarData> hashMap;

    public VarTable() {
        hashMap = new HashMap<>();
    }

    public void add(String var, String value) {
        hashMap.put(var, new VarData("int", value));
    }

    public void add(String var, String type, Object value) {
        hashMap.put(var, new VarData(type, value));
    }

    public boolean contains(String var) {
        return hashMap.containsKey(var);
    }

    public String getType(String var) {
        return hashMap.get(var).type;
    }

    public Object getValue(String var) {
        return hashMap.get(var).value;
    }

    public void setType(String var, String type) {
        hashMap.get(var).type = type;
    }

    public void setValue(String var, Object value) {
        hashMap.get(var).value = value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{");
        boolean first = true;

        for (Map.Entry<String, VarData> entry : hashMap.entrySet()) {
            if (!first) {
                builder.append(", ");
            }
            builder.append("[").append(entry.getKey()).append(" : ").append(entry.getValue()).append("]");
            first = false;
        }
        builder.append("}");

        return builder.toString();
    }

    private static class VarData {
        public String type;
        public Object value;

        public VarData(String type, Object value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return type + ", " + value;
        }
    }

}
/*
public class Table {

    // Строго в однопоточном режиме
    // Область видимости -> локальная таблица переменных
    Map<String, Map<String, Integer>> variables = new HashMap<>();

    void putVariable(String name, Integer value) {
        //TODO позволить методу работать с областями видимости
        //variables.put(name, value);
    }

    Integer getVariable(String name, String scope) {
        //TODO позволить методу работать с областями видимости
        //return variables.get(name);
        return null;
    }
}
*/