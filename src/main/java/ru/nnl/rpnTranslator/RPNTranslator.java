package ru.nnl.rpnTranslator;


import ru.nnl.lexer.Lexeme;
import ru.nnl.token.Token;
import ru.nnl.varTable.VarTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class RPNTranslator {
    private final List<Token> tokens;
    private final VarTable varTable;

    public RPNTranslator(List<Token> tokens, VarTable varTable) {
        this.tokens = tokens;
        this.varTable = varTable;
    }

    private void fromStackToList(Stack<Token> stack, List<Token> list) {
        while (!stack.empty()) {
            list.add(stack.pop());
        }
    }

    public List<Token> translate() {
        List<Token> rpnList = new ArrayList<>();
        Stack<Token> stack = new Stack<>();
        Stack<Lexeme> exprWithTransitions = new Stack<>();
        Stack<Integer> whileKwPositions = new Stack<>();
        Stack<Token> varsWithMethodCalled = new Stack<>();
        Stack<Token> methodCalled = new Stack<>();
        int transitionNumber = 0;

        for (Token curToken : tokens) {
            Lexeme curType = curToken.getLexeme();

            if (curType.getPriority() < 0) {
                continue;
            }

            if (curType == Lexeme.METHOD) {
                varsWithMethodCalled.push(rpnList.remove(rpnList.size() - 1));
                methodCalled.push(curToken);
                continue;
            }

            if (curType == Lexeme.INPUT_KW) {
                fromStackToList(stack, rpnList);
                stack.add(new Token(Lexeme.INPUT_OP, "input"));
                continue;
            }

            if (curType == Lexeme.OUTPUT_KW) {
                fromStackToList(stack, rpnList);
                stack.add(new Token(Lexeme.OUTPUT_OP, "print"));
                continue;
            }

            if (curType == Lexeme.VAR || curType == Lexeme.DIGIT ||
                    curType == Lexeme.CONST_STRING ||
                    curType == Lexeme.TYPE) {
                rpnList.add(curToken);
                continue;
            }

            if (curType == Lexeme.OPEN_PARENTHESIS) {
                stack.push(curToken);
                continue;
            }

            if (curType == Lexeme.SEMICOLON) {
                fromStackToList(stack, rpnList);
                continue;
            }

            if (curType == Lexeme.CLOSE_PARENTHESIS) {
                if (!methodCalled.empty()) {
                    rpnList.add(varsWithMethodCalled.pop());
                    rpnList.add(methodCalled.pop());
                }
                Token top = stack.pop();
                while (top.getLexeme() != Lexeme.OPEN_PARENTHESIS) {
                    rpnList.add(top);
                    top = stack.pop();
                }
                continue;
            }

            if (curType == Lexeme.OPEN_BRACKET) {
                if (!exprWithTransitions.empty()) {
                    rpnList.add(new Token(Lexeme.VAR, "_p" + ++transitionNumber));
                    rpnList.add(new Token(Lexeme.FALSE_TRANSITION, "!F"));
                }

                continue;
            }

            if (curType == Lexeme.CLOSE_BRACKET) {
                if (!exprWithTransitions.empty()) {
                    int falseTransitionPointer = rpnList.size();
                    int oldTransitionNumber = transitionNumber;

                    if (exprWithTransitions.lastElement() == Lexeme.WHILE_KW) {
                        falseTransitionPointer += 2; // To skip unconditional transition

                        String transVar = "_p" + ++transitionNumber;
                        rpnList.add(new Token(Lexeme.VAR, transVar));
                        rpnList.add(new Token(Lexeme.UNCONDITIONAL_TRANSITION, "!"));
                        varTable.add(transVar, "int", whileKwPositions.pop());
                    }
                    varTable.add("_p" + oldTransitionNumber, "int", falseTransitionPointer);
                    exprWithTransitions.pop();
                }

                continue;
            }

            if (curType == Lexeme.IF_KW) {
                exprWithTransitions.push(curType);
                continue;
            }

            if (curType == Lexeme.WHILE_KW) {
                exprWithTransitions.push(curType);
                whileKwPositions.push(rpnList.size());
                continue;
            }

            if (!stack.empty() && stack.peek().getLexeme().getPriority() >= curType.getPriority()) {
                Token top = stack.pop();
                while (!stack.empty() && top.getLexeme().getPriority() >= curType.getPriority()) {
                    rpnList.add(top);
                    top = stack.pop();
                }
                if (top != null) {
                    rpnList.add(top);
                }
            }
            stack.push(curToken);
        }

        fromStackToList(stack, rpnList);

        return rpnList;
    }

}