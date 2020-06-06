package ru.nnl.lexer;


import java.util.regex.Pattern;

public enum Lexeme {

    COMMA("\\,", -1),
    DOT("\\.", -1),
    TYPE("(void|int|string|list|hash_set)", 2),
    ASSIGN_OP("=", 2),
    DIGIT("(0|([1-9][0-9]*))", 0),
    PLUS("\\+", 3),
    MINUS("\\-", 3),
    MULTIPLY("\\*", 4),
    DIVIDE("\\/", 4),
    GREATER(">", 2),
    LESS("<", 2),
    EQUALS("==", 2),
    GREATER_OR_EQUALS(">=", 2),
    LESS_OR_EQUALS("<=", 2),
    CONST_STRING("\"[^\"]*\"", 0),
    WHILE_KW("while", 5),
    FOR_KW("for", 5),
    IF_KW("if", 5),
    INPUT_KW("input", 5),
    OUTPUT_KW("print", 5),
    RETURN_KW("return", 5),
    SEMICOLON(";", 10),
    OPEN_PARENTHESIS("\\(", 1),
    CLOSE_PARENTHESIS("\\)", 1),
    OPEN_BRACKET("\\{", 1),
    CLOSE_BRACKET("\\}", 1),
    METHOD("(add|insert|get|remove|size|isEmpty)", 5),
    VAR("[a-zA-Z]+", 0),
    FALSE_TRANSITION("", 10),
    UNCONDITIONAL_TRANSITION("", 10),
    INPUT_OP("", 10),
    OUTPUT_OP("", 2);

    private final Pattern pattern;
    private final int priority;

    Lexeme(String regexp, int priority) {
        this.pattern = Pattern.compile(regexp);
        this.priority = priority;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public int getPriority() {
        return priority;
    }
}
