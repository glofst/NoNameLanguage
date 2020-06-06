package ru.nnl.stackmachine;


import ru.nnl.exception.ExecuteException;
import ru.nnl.lexer.Lexeme;
import ru.nnl.token.Token;
import ru.nnl.typeTable.Method;
import ru.nnl.typeTable.TypeTable;
import ru.nnl.types.hashset.MyHashSet;
import ru.nnl.types.list.MyList;
import ru.nnl.varTable.VarTable;

import java.util.*;

public class StackMachine {

    private final List<Token> RPNTokenList;
    private final Stack<Token> machineStack;
    private final VarTable varTable;
    private final TypeTable typeTable;
    private int tokenIterator;

    public StackMachine(List<Token> RPNTokenList, VarTable varTable, TypeTable typeTable) {
        this.RPNTokenList = RPNTokenList;
        this.varTable = varTable;
        this.typeTable = typeTable;
        this.tokenIterator = 0;
        this.machineStack = new Stack<>();
    }

    public void execute() throws ExecuteException {
        for (; tokenIterator < RPNTokenList.size(); tokenIterator++) {
            Token currentToken = RPNTokenList.get(tokenIterator);
            Lexeme currentLexeme = currentToken.getLexeme();

//            System.out.println("Token: " + currentToken);
//            System.out.println("Stack: " + machineStack);
//            System.out.println("VarTable: " + varTable + "\n");

            if (currentLexeme == Lexeme.VAR || currentLexeme == Lexeme.DIGIT || currentLexeme == Lexeme.CONST_STRING || currentLexeme == Lexeme.TYPE) {
                machineStack.push(currentToken);
                continue;
            }

            switch (currentLexeme) {
                case ASSIGN_OP -> assign();
                case PLUS -> plus();
                case MINUS -> minus();
                case MULTIPLY -> multiply();
                case DIVIDE -> divide();
                case GREATER -> greater();
                case LESS -> less();
                case EQUALS -> equal();
                case GREATER_OR_EQUALS -> greaterOrEquals();
                case LESS_OR_EQUALS -> lessOrEquals();
                case METHOD -> method(currentToken.getValue());
                case INPUT_OP -> input();
                case OUTPUT_OP -> output();
                case FALSE_TRANSITION -> falseTransition();
                case UNCONDITIONAL_TRANSITION -> unconditionalTransition();
                default -> throw new ExecuteException("Unexpected token {" + currentLexeme + ": " + currentToken.getValue() + "}");
            }
        }
    }

    private void checkForVariableOrDigit(Token token) throws ExecuteException {
        if (token.getLexeme() != Lexeme.VAR && token.getLexeme() != Lexeme.DIGIT) {
            throw new ExecuteException("Expected variable or digit, but it is {" + token.getLexeme() + ": " + token.getValue() + "}");
        }
    }

    private void checkForVariable(Token token) throws ExecuteException {
        if (token.getLexeme() != Lexeme.VAR) {
            throw new ExecuteException("Expected variable, but it is {" + token.getLexeme() + ": " + token.getValue() + "}");
        }
    }

    private void assign() throws ExecuteException {
        Token rvalue = machineStack.pop();
        Token lvalue = machineStack.pop();

        String newType = null;
        Object newValue = null;

        checkForVariable(lvalue);

        if (rvalue.getLexeme() == Lexeme.VAR) {
            newType = varTable.getType(lvalue.getValue());
            newValue = varTable.getValue(lvalue.getValue());
        } else if (rvalue.getLexeme() == Lexeme.DIGIT) {
            newType = "int";
            newValue = Integer.parseInt(rvalue.getValue());
        } else if (rvalue.getLexeme() == Lexeme.CONST_STRING) {
            newType = "string";
            newValue = rvalue.getValue();
        } else if (rvalue.getLexeme() == Lexeme.TYPE) {
            newValue = switch (rvalue.getValue()) {
                case "int" -> 0;
                case "string" -> "";
                case "list" -> new MyList();
                case "hash_set" -> new MyHashSet();
                default -> throw new ExecuteException("Unexpected type: " + rvalue.getValue());
            };
            newType = rvalue.getValue();
        }
        varTable.add(lvalue.getValue(), newType, newValue);
    }

    private Method findMethod(String name, List<Method> methods) {
        Method result_method = null;
        for (Method method : methods) {
            if (method.getName().equals(name)) {
                result_method = method;
                break;
            }
        }

        return result_method;
    }

    private void method(String name) throws ExecuteException {
        Token token = machineStack.pop();
        checkForVariable(token);

        Method method = findMethod(name, typeTable.get(varTable.getType(token.getValue())));

        List<Object> arguments = new ArrayList<>();
        List<String> methodParametersTypes = method.getParametersTypes();
        Collections.reverse(methodParametersTypes);

        for (String paramType : methodParametersTypes) {
            Token argument = machineStack.pop();
            String argumentType;
            Object argumentValue;

            switch (argument.getLexeme()) {
                case VAR -> {
                    argumentType = varTable.getType(argument.getValue());
                    argumentValue = varTable.getValue(argument.getValue());
                }
                case DIGIT -> {
                    argumentType = "int";
                    argumentValue = Integer.parseInt(argument.getValue());
                }
                case CONST_STRING -> {
                    argumentType = "string";
                    argumentValue = argument.getValue();
                }
                default -> throw new ExecuteException("Unexpected type of argument in method: " + name);
            }

            if (!argumentType.equals(paramType)) {
                throw new ExecuteException("Method " + name + " of type " + varTable.getType(token.getValue()) +
                        " accepts argument of type " + paramType +
                        ", but got " + argumentType);
            }

            arguments.add(argumentValue);
        }

        Object res = method.call(varTable.getValue(token.getValue()), arguments);
        String returnType = method.getReturnType();
        switch (returnType) {
            case "int" -> machineStack.push(new Token(Lexeme.DIGIT, Integer.toString((Integer) res)));
            case "string" -> machineStack.push(new Token(Lexeme.CONST_STRING, (String) res));
        }
    }

    private int tokenToInt(Token token) throws ExecuteException {
        checkForVariableOrDigit(token);

        int result;
        if (token.getLexeme() == Lexeme.VAR) {
            result = (Integer) varTable.getValue(token.getValue());
        } else {
            result = Integer.parseInt(token.getValue());
        }

        return result;
    }

    private void plus() throws ExecuteException {
        int rightValue = tokenToInt(machineStack.pop());
        int leftValue = tokenToInt(machineStack.pop());

        machineStack.push(new Token(Lexeme.DIGIT, Integer.toString(leftValue + rightValue)));
    }

    private void minus() throws ExecuteException {
        int rightValue = tokenToInt(machineStack.pop());
        int leftValue = tokenToInt(machineStack.pop());

        machineStack.push(new Token(Lexeme.DIGIT, Integer.toString(leftValue - rightValue)));
    }

    private void multiply() throws ExecuteException {
        int rightValue = tokenToInt(machineStack.pop());
        int leftValue = tokenToInt(machineStack.pop());

        machineStack.push(new Token(Lexeme.DIGIT, Integer.toString(leftValue * rightValue)));
    }

    private void divide() throws ExecuteException {
        int rightValue = tokenToInt(machineStack.pop());
        int leftValue = tokenToInt(machineStack.pop());

        machineStack.push(new Token(Lexeme.DIGIT, Integer.toString(leftValue / rightValue)));
    }

    private void greater() throws ExecuteException {
        int rightValue = tokenToInt(machineStack.pop());
        int leftValue = tokenToInt(machineStack.pop());

        machineStack.push(new Token(Lexeme.DIGIT, Integer.toString(leftValue > rightValue ? 1 : 0)));
    }

    private void less() throws ExecuteException {
        int rightValue = tokenToInt(machineStack.pop());
        int leftValue = tokenToInt(machineStack.pop());

        machineStack.push(new Token(Lexeme.DIGIT, Integer.toString(leftValue < rightValue ? 1 : 0)));
    }

    private void equal() throws ExecuteException {
        int rightValue = tokenToInt(machineStack.pop());
        int leftValue = tokenToInt(machineStack.pop());

        machineStack.push(new Token(Lexeme.DIGIT, Integer.toString(leftValue == rightValue ? 1 : 0)));
    }

    private void greaterOrEquals() throws ExecuteException {
        int rightValue = tokenToInt(machineStack.pop());
        int leftValue = tokenToInt(machineStack.pop());

        machineStack.push(new Token(Lexeme.DIGIT, Integer.toString(leftValue >= rightValue ? 1 : 0)));
    }

    private void lessOrEquals() throws ExecuteException {
        int rightValue = tokenToInt(machineStack.pop());
        int leftValue = tokenToInt(machineStack.pop());

        machineStack.push(new Token(Lexeme.DIGIT, Integer.toString(leftValue <= rightValue ? 1 : 0)));
    }

    private void input() throws ExecuteException {
        Token token = machineStack.pop();

        checkForVariable(token);
        Scanner scanner = new Scanner(System.in);
        String str = scanner.next();
        String type;
        if (varTable.contains(token.getValue())) {
            type = varTable.getType(token.getValue());
        } else {
            try {
                Integer.parseInt(str);
                type = "int";
            } catch (NumberFormatException exception) {
                type = "string";
            }
        }

        if (type.equals("int")) {
            varTable.add(token.getValue(), type, Integer.parseInt(str));
        } else if (type.equals("string")) {
            varTable.add(token.getValue(), type, str);
        }
    }

    private void output() throws ExecuteException {
        Token token = machineStack.pop();
        String str = "";
        switch (token.getLexeme()) {
            case VAR -> {
                String type = varTable.getType(token.getValue());
                Object value = varTable.getValue(token.getValue());
                switch (type) {
                    case "int" -> str = Integer.toString((Integer) value);
                    case "string" -> str = (String) value;
                    case "list", "hash_set" -> str = value.toString();
                }
            }
            case DIGIT -> str = token.getValue();
            case CONST_STRING -> {
                str = token.getValue();
                str = str.substring(1, str.length() - 1);
            }
            default -> throw new ExecuteException("Expected variable, digit or const string, but it is " +
                    token.getLexeme() + ": " +
                    token.getValue());
        }
        System.out.print(str);

        if ((tokenIterator + 1 >= RPNTokenList.size()) || ((tokenIterator + 1 < RPNTokenList.size()) && RPNTokenList.get(tokenIterator + 1).getLexeme() != Lexeme.OUTPUT_OP)) {
            System.out.println();
        }
    }

    private void falseTransition() throws ExecuteException {
        Token pointer = machineStack.pop();
        Token condition = machineStack.pop();

        checkForVariable(pointer);

        if (tokenToInt(condition) == 0) {
            tokenIterator = tokenToInt(pointer) - 1;
        }
    }

    private void unconditionalTransition() throws ExecuteException {
        Token pointer = machineStack.pop();

        checkForVariable(pointer);

        tokenIterator = tokenToInt(pointer) - 1;
    }

}
