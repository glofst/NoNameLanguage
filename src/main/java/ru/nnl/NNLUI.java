package ru.nnl;

import ru.nnl.exception.ExecuteException;
import ru.nnl.exception.LangParseException;
import ru.nnl.exception.TokenException;
import ru.nnl.lexer.Lexer;
import ru.nnl.parser.Parser;
import ru.nnl.rpnTranslator.RPNTranslator;
import ru.nnl.stackmachine.StackMachine;
import ru.nnl.token.Token;
import ru.nnl.typeTable.TypeTable;
import ru.nnl.varTable.VarTable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class NNLUI {

    public static void main(String[] args) throws LangParseException, ExecuteException, TokenException, IOException {
        System.out.println("Start ...");
        String rawInput = Files.readString(Paths.get("examples/example.nnl"));

        System.out.println("======================\nTokens\n======================");

        Lexer lexer = new Lexer(rawInput);
        for (Token token : lexer.getTokens()) {
            System.out.println(token);
        }

        Parser parser = new Parser(lexer.getTokens());
       // parser.lang();
        System.out.println("\n======================\nParsed Successfully\n======================\n");
        VarTable variableTable = new VarTable();
        TypeTable typeTable = new TypeTable();

        typeTable.addList();

        RPNTranslator translator = new RPNTranslator(lexer.getTokens(), variableTable);
        List<Token> rpn = translator.translate();
        System.out.println("\n======================\nRPN Translate\n======================\n");
        for (Token token : rpn) {
            System.out.print(token.getValue() + " ");
        }
        System.out.println("\nTransition Table: " + variableTable);

        StackMachine stackMachine = new StackMachine(rpn, variableTable, typeTable);

        System.out.println("\n======================\nProgram run\n======================\n");
        stackMachine.execute();

        System.out.println("\n======================\nTable of variables\n======================\n");
        System.out.println(variableTable);
    }

}
