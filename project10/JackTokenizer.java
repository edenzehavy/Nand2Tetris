import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class JackTokenizer {

    private static final ArrayList<String> keywords = new ArrayList<>();
    private static final ArrayList<Character> symbols = new ArrayList<>();
    private static final ArrayList<Character> operations = new ArrayList<>();
    private ArrayList<String> tokens = new ArrayList<>();
    private Scanner scanner;
    private String jackCode;
    private String tokenType;
    private String keywordType;
    private char symbolType;
    private String identifier;
    private String stringValue;
    private int intValue;
    public int pointer;
    private boolean isNewToken;

    /**
     * Initializes the static class variables.
     */
    static {
        keywords.add("class");
        keywords.add("constructor");
        keywords.add("function");
        keywords.add("method");
        keywords.add("field");
        keywords.add("static");
        keywords.add("var");
        keywords.add("int");
        keywords.add("char");
        keywords.add("boolean");
        keywords.add("void");
        keywords.add("true");
        keywords.add("false");
        keywords.add("null");
        keywords.add("this");
        keywords.add("do");
        keywords.add("if");
        keywords.add("else");
        keywords.add("while");
        keywords.add("return");
        keywords.add("let");
        operations.add('+');
        operations.add('-');
        operations.add('*');
        operations.add('/');
        operations.add('&');
        operations.add('|');
        operations.add('<');
        operations.add('>');
        operations.add('=');
        symbols.add('{');
        symbols.add('}');
        symbols.add('(');
        symbols.add(')');
        symbols.add('[');
        symbols.add(']');
        symbols.add('.');
        symbols.add(',');
        symbols.add(';');
        symbols.add('+');
        symbols.add('-');
        symbols.add('*');
        symbols.add('/');
        symbols.add('&');
        symbols.add('|');
        symbols.add('<');
        symbols.add('>');
        symbols.add('=');
        symbols.add('-');
        symbols.add('~');
    }

    /**
     * Constructs a JackTokenizer object with the given file.
     * @param file The input file containing Jack source code.
     */
    public JackTokenizer(File file) {
        try {
            scanner = new Scanner(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        jackCode = "";
        while (scanner.hasNextLine()) {
            String Line = scanner.nextLine();
            boolean hasComments = false;
            if (Line.contains("//") || Line.contains("/*") || Line.startsWith(" *")) {
                hasComments = true;
            }
            if (hasComments) {
                int offSet;
                if (Line.startsWith(" *")) {
                    offSet = Line.indexOf("*");
                } else if (Line.contains("/*")) {
                    offSet = Line.indexOf("/*");
                } else {
                    offSet = Line.indexOf("//");
                }
                Line = Line.substring(0, offSet).trim();
            }

            while (Line.equals("") || Line.trim().equals("")) {
                if (scanner.hasNextLine()) {
                    Line = scanner.nextLine();
                    boolean innerHasComments = false;
                    if (Line.contains("//") || Line.contains("/*") || Line.startsWith(" *")) {
                        innerHasComments = true;
                    }
                    if (innerHasComments) {
                        int offSet;
                        if (Line.startsWith(" *")) {
                            offSet = Line.indexOf("*");
                        } else if (Line.contains("/*")) {
                            offSet = Line.indexOf("/*");
                        } else {
                            offSet = Line.indexOf("//");
                        }
                        Line = Line.substring(0, offSet).trim();
                    }
                } else {
                    break;
                }
            }

            jackCode += Line.trim();
        }

        tokens = new ArrayList<>();
        while (!jackCode.isEmpty()) {
            while (jackCode.charAt(0) == ' ') {
                jackCode = jackCode.substring(1);
            }
            for (int i = 0; i < keywords.size(); i++) {
                if (jackCode.startsWith(keywords.get(i).toString())) {
                    String keyword = keywords.get(i).toString();
                    tokens.add(keyword);
                    jackCode = jackCode.substring(keyword.length());
                }
            }
            if (symbols.contains(jackCode.charAt(0))) {
                char symbol = jackCode.charAt(0);
                tokens.add(Character.toString(symbol));
                jackCode = jackCode.substring(1);
            } else if (jackCode.charAt(0) >= '0' && jackCode.charAt(0) <= '9') {
                String str = jackCode.substring(0, 1);
                jackCode = jackCode.substring(1);
                while (jackCode.charAt(0) >= '0' && jackCode.charAt(0) <= '9') {
                    str += "" + jackCode.charAt(0);
                    jackCode = jackCode.substring(1);
                }
                tokens.add(str);
            } else if (jackCode.charAt(0) == '\"') {
                jackCode = jackCode.substring(1);
                String str = "\"";
                while ((jackCode.charAt(0) != '\"')) {
                    str += "" + jackCode.charAt(0);
                    jackCode = jackCode.substring(1);
                }
                str = str + "\"";
                tokens.add(str);
                jackCode = jackCode.substring(1);
            } else if (Character.isLetter(jackCode.charAt(0)) || (jackCode.charAt(0) == '_')) {
                String currIdentifier = "" + jackCode.charAt(0);
                jackCode = jackCode.substring(1);
                while ((Character.isLetter(jackCode.charAt(0))) || (jackCode.charAt(0) == '_')) {
                    currIdentifier += "" + jackCode.charAt(0);
                    jackCode = jackCode.substring(1);
                }
                tokens.add(currIdentifier);
            }
            isNewToken = true;
            pointer = 0;
        }
    }

    /**
     * Checks if there are more tokens in the file.
     * @reutrn true if there are and false if there aren't.
     */
    public boolean hasMoreTokens() {
        return (pointer < tokens.size());
    }

    /**
     * Gets the next token from the input and makes it the current token.
     */
    public void advance() {
        if (hasMoreTokens()) {
            if (!isNewToken) {
                pointer++;
            } else if (isNewToken) {
                isNewToken = false;
            }
            String currToken = tokens.get(pointer);
            if (keywords.contains(currToken)) {
                tokenType = "KEYWORD";
                keywordType = currToken;
            } else if (symbols.contains(currToken.charAt(0))) {
                symbolType = currToken.charAt(0);
                tokenType = "SYMBOL";
            } else if (currToken.charAt(0) >= '0' && currToken.charAt(0) <= '9') {
                intValue = Integer.parseInt(currToken);
                tokenType = "INT_CONST";
            } else if (currToken.charAt(0) == '\"') {
                tokenType = "STRING_CONST";
                stringValue = currToken.substring(1, currToken.length() - 1);
            } else if ( (currToken.charAt(0) == '_') || (Character.isLetter(currToken.charAt(0)))) {
                tokenType = "IDENTIFIER";
                identifier = currToken;
            }
        }
    }

    /**
     * Returns the string value of the current token type.
     * @return The string value of the current token type.
     */
    public String tokenType() {
        return tokenType;
    }

    /**
     * Returns the keyword which is the current token.
     * @return The keyword which is the current token.
     */
    public String keyWord() {
        return keywordType;
    }

    /**
     * Returns the character which is the current token.
     * @return The character which is the current token.
     */
    public char symbol() {
        return symbolType;
    }

    /**
     * Returns the string which is the current token.
     * @return The string which is the current token.
     */
    public String identifier() {
        return identifier;
    }

    /**
     * Returns the integer value of the current token.
     * @return The integer value of the current token.
     */
    public int intVal() {
        return intValue;
    }

    /**
     * Returns the string value of the current token.
     * @return The string value of the current token.
     */
    public String stringVal() {
        return stringValue;
    }
    /**
     * Returns if the current symbolType is an operation.
     * @return a boolean value which indicates if the current symbolType is an operation or not.
     */
    public boolean isOperation() {
        return (operations.contains(symbolType));
    }

}