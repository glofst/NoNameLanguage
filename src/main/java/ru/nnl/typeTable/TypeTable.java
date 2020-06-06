package ru.nnl.typeTable;

import ru.nnl.types.list.MyList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TypeTable extends HashMap<String, List<Method>> {
    public TypeTable() {
        super();
    }
    public void addList() {
        this.put("list", new ArrayList<Method>(Arrays.asList(new Method("add", new ArrayList<String>(){{add("int");}}, "", (arg0, arg1) -> {
                    MyList list = (MyList) arg0;
                    ArrayList<Integer> argsList = (ArrayList<Integer>) arg1;
                    list.add(argsList.get(0));
                    return null;
                }),
                new Method("insert", new ArrayList<String>(){{add("int"); add("int");}}, "", (arg0, arg1) -> {
                    MyList list = (MyList) arg0;
                    ArrayList<Integer> argsList = (ArrayList<Integer>) arg1;
                    int realArg0 = argsList.get(0);
                    int realArg1 = argsList.get(1);
                    list.insert(realArg0, realArg1);
                    return null;
                }),
                new Method("get", new ArrayList<String>(){{add("int");}}, "int", (arg0, arg1) -> {
                    MyList list = (MyList) arg0;
                    ArrayList<Integer> argsList = (ArrayList<Integer>) arg1;
                    return list.get(argsList.get(0));
                }),
                new Method("remove", new ArrayList<String>(){{add("int");}}, "", (arg0, arg1) -> {
                    MyList list = (MyList) arg0;
                    ArrayList<Integer> argsList = (ArrayList<Integer>) arg1;
                    list.remove(argsList.get(0));
                    return null;
                }),
                new Method("size", new ArrayList<String>(), "int", (arg0, arg1) -> {
                    MyList list = (MyList) arg0;
                    return list.size();
                }),
                new Method("isEmpty", new ArrayList<String>(), "int", (arg0, arg1) -> {
                    MyList list = (MyList) arg0;
                    return list.isEmpty() ? 1 : 0;
                }))));
    }
}
