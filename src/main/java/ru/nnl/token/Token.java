package ru.nnl.token;

import ru.nnl.lexer.Lexeme;

public class Token {

    private final Lexeme lexeme;
    private final String value;

    public Token(Lexeme type, String value) {
        this.lexeme = type;
        this.value = value;
    }

    public Lexeme getLexeme() {
        return lexeme;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Token{" +
                "lexeme=" + lexeme +
                ", value='" + value + '\'' +
                '}';
    }
}
