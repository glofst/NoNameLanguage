package ru.nnl.lexer;

import ru.nnl.exception.TokenException;
import ru.nnl.token.Token;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Lexer {
    private final String rawInput;

    public Lexer(String rawInput) {
        this.rawInput = rawInput;
    }

    public List<Token> getTokens() throws TokenException {
        List<Token> tokens = new ArrayList<>();
        int lineCounter = 0;

        for (String line : getLines(rawInput)) {
            try {
                ++lineCounter;

                while (line.matches("(\\s*\\S+\\s*)+")) {
                    line = line.trim();

                    Lexeme suitableLexeme = Lexeme.values()[0];

                    String real_regex = "^(" + suitableLexeme.getPattern().pattern() + ")";
                    Matcher matcher = Pattern.compile(real_regex).matcher(line);
                    while (!matcher.find()) {
                        suitableLexeme = getNextLexeme(suitableLexeme);
                        real_regex = "^(" + suitableLexeme.getPattern().pattern() + ")";
                        matcher.usePattern(Pattern.compile(real_regex));
                    }
                    String value = matcher.group(0);
                    tokens.add(new Token(suitableLexeme, value));
                    line = matcher.replaceFirst("");
                }
            } catch (IndexOutOfBoundsException ex) {
                Scanner scanner = new Scanner(line);
                String unknownSymbol = scanner.next();
                scanner.close();

                throw new TokenException("Unknown symbol at line " + lineCounter + " : " + unknownSymbol);
            }
        }

        return tokens;
    }

    private Lexeme getNextLexeme(Lexeme lexemeType) throws IndexOutOfBoundsException {
        int curPos = lexemeType.ordinal();
        Lexeme[] lexemeTypes = Lexeme.values();

        if (curPos >= lexemeTypes.length) {
            throw new IndexOutOfBoundsException();
        }

        return lexemeTypes[curPos + 1];
    }

    private List<String> getLines(String str) {
        Scanner scanner = new Scanner(str);
        List<String> lines = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            lines.add(line);
        }

        scanner.close();
        return lines;
    }
}

//package ru.glofst.nnl.lexer;
//
//        import ru.glofst.nnl.token.Token;
//
//        import java.util.*;
//        import java.util.regex.Matcher;
//
//public class Lexer_old {
//
//    private final String rawInput;
//
//    public Lexer_old(String rawInput) {
//        this.rawInput = rawInput;
//    }
//
//    public String getRawInput() {
//        return rawInput;
//    }
//
//    public List<Token> getTokens() {
//        List<Token> tokens = new ArrayList<>(); // Список токенов, который подается на выход
//
//        for (String line : getLines(rawInput)) {
//            line = line.replaceAll("\\s+", ""); // Удалить все пробелы
//
//            int currentIndex = 0; // текущий индекс цикла
//            int beginIndex = 0; // начальный индекс собираемой лексемы
//
//            boolean okWaiting = true;
//
//            List<Lexeme> previous_lexemes = new ArrayList<>(); // Хранит все подходящие лексемы под текущий substr
//
//            // До конца входного файла
//            while (currentIndex < line.length()) {
//                String buffer = line.substring(beginIndex, currentIndex + 1); // отхватываем каждый раз +1 символ из начального файла
//                List<Lexeme> suitable_lexemes = new ArrayList<>(); // Список регулярок, которые подходят под текущий стринг
//                for (Lexeme lexeme : Lexeme.values()) {
//                    Matcher matcher = lexeme.getPattern().matcher(buffer);
//                    if (matcher.find()) {
//                        suitable_lexemes.add(lexeme);
//                    }
//                }
//
//                if (suitable_lexemes.size() != 0) {
//                    previous_lexemes = suitable_lexemes;
//                }
//                // Начало собирания лексемы
//                if (okWaiting) {
//                    // Если на вход подан начальный символ, неизвестный для регулярок
//                    if (suitable_lexemes.size() == 0) {
//                        throw new RuntimeException("Incorrect source");
//                    }
//
//                    okWaiting = false;
//                } else if (suitable_lexemes.size() == 0) {
//                    // Если залезли на след. символ так, что больше нет подходящих регулярок
//                    // Значит мы собрали полную лексему и нужно создать токен
//
//                    generateToken(tokens, previous_lexemes, buffer);
//
//                    // Начинаем искать новую лексему
//                    okWaiting = true;
//                    beginIndex = currentIndex;
//                    currentIndex -= 1;
//                }
//                if (currentIndex == line.length() - 1) {
//                    // Если это последний элемент в файле, то необходимо завершить последний токен
//                    generateToken(tokens, previous_lexemes, buffer + "1");
//                }
//                // Все еще есть пожходящий варианты под лексему, продолжаем поис
//
//                currentIndex += 1;
//
//            }
//        }
//
//        return tokens;
//    }
//
//    private void generateToken(List<Token> tokens, List<Lexeme> previous_lexemes, String buffer) {
//        // убедиться, что ваша лексема не перекрывается лексемой ключевого слова
//        // (то есть лексемой более высокого приоритета)
//        Lexeme lexeme = getHighestPriority(previous_lexemes);
//
//        // Формируем токен
//        Token token = new Token(lexeme, buffer.substring(0, buffer.length() - 1));
//        tokens.add(token);
//        //System.out.println("TOKEN:" + token);
//    }
//
//
//    private Lexeme getHighestPriority(List<Lexeme> lexemes) {
//        return Collections.max(lexemes, new Comparator<Lexeme>() {
//            @Override
//            public int compare(Lexeme first, Lexeme second) {
//                if (first.getPriority() > second.getPriority())
//                    return 1;
//                else if (first.getPriority() < second.getPriority())
//                    return -1;
//                return 0;
//            }
//        });
//    }
//
//    private List<String> getLines(String str) {
//        Scanner scanner = new Scanner(str);
//        List<String> lines = new ArrayList<String>();
//
//        while (scanner.hasNextLine()) {
//            String line = scanner.nextLine();
//            lines.add(line);
//        }
//
//        scanner.close();
//        return lines;
//    }
//}
