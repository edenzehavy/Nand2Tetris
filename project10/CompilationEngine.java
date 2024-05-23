import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CompilationEngine {

    /**
     * This class represents a Compilation Engine for a Jack programming language compiler.
     */

    private BufferedWriter writer;
    private JackTokenizer tokenizer;
    private boolean isNewRoutine = true;

    /**
     * Constructs a CompilationEngine object with the given input and output.
     * @param input The input file containing Jack source code.
     * @param output The output file where the XML representation of the parsed code will be written.
     */
    public CompilationEngine(File input, File output){
        try{
            writer = new BufferedWriter(new FileWriter(output));
            tokenizer = new JackTokenizer(input);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Compiles a complete class.
     */
    public void compileClass(){
        try {
            tokenizer.advance();
            writer.write("<class>" + "\n");
            writer.write("<keyword> class </keyword>" + "\n");
            tokenizer.advance();
            writer.write("<identifier> " + tokenizer.identifier() + " </identifier>" + "\n");
            tokenizer.advance();
            writer.write("<symbol> { </symbol>" + "\n");
            compileClassVarDec();
            compileSubroutine();
            writer.write("<symbol> } </symbol>" + "\n");
            writer.write("</class>" + "\n");
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compiles a static variable declaration, or a field declaration.
     */
    public void compileClassVarDec() {
        tokenizer.advance();
        try {
            while (tokenizer.keyWord().matches("static|field")) {
                writer.write("<classVarDec>" + "\n" + "<keyword> " + tokenizer.keyWord() + " </keyword>" + "\n");
                tokenizer.advance();
                if ("IDENTIFIER".equals(tokenizer.tokenType())) {
                    writer.write("<identifier> " + tokenizer.identifier() + " </identifier>" + "\n");
                } else {
                    writer.write("<keyword> " + tokenizer.keyWord() + " </keyword>" + "\n");
                }
                tokenizer.advance();
                writer.write("<identifier> " + tokenizer.identifier() + " </identifier>" + "\n");
                tokenizer.advance();
                if (tokenizer.symbol() == ',') {
                    writer.write("<symbol> , </symbol>" + "\n");
                    tokenizer.advance();
                    writer.write("<identifier> " + tokenizer.identifier() + " </identifier>" + "\n");
                    tokenizer.advance();
                }

                writer.write("<symbol> ; </symbol>" + "\n" + "</classVarDec>" + "\n");
                tokenizer.advance();
            }

            if (tokenizer.keyWord().matches("function|method|constructor")) {
                if (tokenizer.pointer > 0) tokenizer.pointer--;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compiles a complete method, function, or constructor.
     */
    public void compileSubroutine() {
        boolean containsSubroutine = false;
        tokenizer.advance();
        try {
            if (tokenizer.symbol() == '}' && tokenizer.tokenType().equals("SYMBOL")) {
                return;
            }
            if ((isNewRoutine) && (tokenizer.keyWord().matches("function|method|constructor"))) {
                isNewRoutine = false;
                writer.write("<subroutineDec>" + "\n");
                containsSubroutine = true;
            }
            if (tokenizer.keyWord().matches("function|method|constructor")) {
                containsSubroutine = true;
                writer.write("<keyword> " + tokenizer.keyWord() + " </keyword>" + "\n");
                tokenizer.advance();
            }
            if (tokenizer.tokenType().equals("IDENTIFIER")) {
                writer.write("<identifier> " + tokenizer.identifier() + " </identifier>" + "\n");
                tokenizer.advance();
            }

            else if (tokenizer.tokenType().equals("KEYWORD")) {
                writer.write("<keyword> " + tokenizer.keyWord() + " </keyword>" + "\n");
                tokenizer.advance();
            }

            if (tokenizer.tokenType().equals("IDENTIFIER")) {
                writer.write("<identifier> " + tokenizer.identifier() + " </identifier>" + "\n");
                tokenizer.advance();
            }

            if (tokenizer.symbol() == '(') {
                writer.write("<symbol> ( </symbol>" + "\n");
                writer.write("<parameterList>" + "\n");
                compileParameterList();
                writer.write("</parameterList>" + "\n");
                writer.write("<symbol> ) </symbol>" + "\n");
            }
            compileSubroutineBody();

            if (containsSubroutine) {
                writer.write("</subroutineBody>" + "\n" + "</subroutineDec>" + "\n");
                isNewRoutine = true;
            }
            compileSubroutine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compiles a (possibly empty) parameter list. Does not handle the enclosing parentheses tokens ( and ).
     */
    public void compileParameterList() {
        tokenizer.advance();
        try {
            while (!(tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == ')')) {
                if (tokenizer.tokenType().equals("IDENTIFIER")) {
                    writer.write("<identifier> " + tokenizer.identifier() + " </identifier>" + "\n");
                    tokenizer.advance();
                } else if (tokenizer.tokenType().equals("KEYWORD")) {
                    writer.write("<keyword> " + tokenizer.keyWord() + " </keyword>" + "\n");
                    tokenizer.advance();
                } else if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == ',') {
                    writer.write("<symbol> , </symbol>" + "\n");
                    tokenizer.advance();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compiles a subroutine's body.
     */
    public void compileSubroutineBody () {
        tokenizer.advance();
        try {
            if (tokenizer.symbol() == '{') {
                writer.write("<subroutineBody>" + "\n" + "<symbol> { </symbol>" + "\n");
                tokenizer.advance();
            }
            while (tokenizer.keyWord().equals("var") && (tokenizer.tokenType().equals("KEYWORD"))) {
                writer.write("<varDec>" + "\n");
                if (tokenizer.pointer > 0) tokenizer.pointer--;
                compileVarDec();
                writer.write("</varDec>" + "\n");
            }
            writer.write("<statements>" + "\n");
            compileStatements();
            writer.write("</statements>" + "\n" + "<symbol> " + tokenizer.symbol() + " </symbol>" + "\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compiles a var declaration.
     */
    public void compileVarDec() {
        tokenizer.advance();
        try {
            if (tokenizer.keyWord().equals("var") && (tokenizer.tokenType().equals("KEYWORD"))) {
                writer.write("<keyword> " + tokenizer.keyWord() + " </keyword>" + "\n");
                tokenizer.advance();
            }
            if (tokenizer.tokenType().equals("IDENTIFIER")) {
                writer.write("<identifier> " + tokenizer.identifier() + " </identifier>" + "\n");
                tokenizer.advance();
            }
            else if (tokenizer.tokenType().equals("KEYWORD")) {
                writer.write("<keyword> " + tokenizer.keyWord() + " </keyword>" + "\n");
                tokenizer.advance();
            }
            if (tokenizer.tokenType().equals("IDENTIFIER")) {
                writer.write("<identifier> " + tokenizer.identifier() + " </identifier>" + "\n");
                tokenizer.advance();
            }
            if ((tokenizer.tokenType().equals("SYMBOL")) && (tokenizer.symbol() == ',')) {
                writer.write("<symbol> , </symbol>" + "\n");
                tokenizer.advance();
                writer.write(("<identifier> " + tokenizer.identifier() + " </identifier>" + "\n"));
                tokenizer.advance();
            }
            if ((tokenizer.tokenType().equals("SYMBOL")) && (tokenizer.symbol() == ';')) {
                writer.write("<symbol> ; </symbol>" + "\n");
                tokenizer.advance();

            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compiles a sequence of statements. Does not handle the enclosing curly brackets tokens { and }.
     */
    public void compileStatements() {
        try {
            if (tokenizer.symbol() == '}' && (tokenizer.tokenType().equals("SYMBOL"))) {
                return;
            }
            if(tokenizer.tokenType().equals(("KEYWORD"))){
                switch (tokenizer.keyWord()){
                    case "do": {
                        writer.write("<doStatement>" + "\n");
                        compileDo();
                        writer.write("</doStatement>" + "\n");
                        break;
                    }
                    case "let":{
                        writer.write("<letStatement>" + "\n");
                        compileLet();
                        writer.write("</letStatement>" + "\n");
                        break;
                    }
                    case "if":{
                        writer.write("<ifStatement>" + "\n");
                        compileIf();
                        writer.write("</ifStatement>" + "\n");
                        break;
                    }
                    case "while":{
                        writer.write("<whileStatement>" + "\n");
                        compileWhile();
                        writer.write("</whileStatement>" + "\n");
                        break;
                    }
                    case "return":{
                        writer.write("<returnStatement>" + "\n");
                        compileReturn();
                        writer.write("</returnStatement>" + "\n");
                        break;
                    }
                    default:
                        break;
                }
            }
            tokenizer.advance();
            compileStatements();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compiles a let statement.
     */
    public void compileLet() {
        try {
            writer.write("<keyword> " + tokenizer.keyWord() + " </keyword>" + "\n");
            tokenizer.advance();
            writer.write("<identifier> " + tokenizer.identifier() + " </identifier>" + "\n");
            tokenizer.advance();
            if ((tokenizer.tokenType().equals("SYMBOL")) && (tokenizer.symbol() == '[')) {
                writer.write("<symbol> [ </symbol>" + "\n");
                compileExpression();
                tokenizer.advance();
                if ((tokenizer.tokenType().equals("SYMBOL")) && ((tokenizer.symbol() == ']'))) {
                    writer.write("<symbol> ] </symbol>" + "\n");
                }
                tokenizer.advance();
            }
            writer.write("<symbol> " + tokenizer.symbol() + " </symbol>" + "\n");
            compileExpression();
            writer.write("<symbol> " + tokenizer.symbol() + " </symbol>" + "\n");
            tokenizer.advance();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compiles an if statement, possibly with a trailing else clause.
     */
    public void compileIf() {
        try {
            writer.write("<keyword> if </keyword>" + "\n");
            tokenizer.advance();
            writer.write("<symbol> ( </symbol>" + "\n");
            compileExpression();
            writer.write("<symbol> ) </symbol>" + "\n");
            tokenizer.advance();
            writer.write("<symbol> { </symbol>" + "\n");
            tokenizer.advance();
            writer.write("<statements>" + "\n");
            compileStatements();
            writer.write("</statements>" + "\n" + "<symbol> } </symbol>" + "\n");
            tokenizer.advance();
            if (tokenizer.tokenType().equals("KEYWORD") && tokenizer.keyWord().equals("else")) {
                writer.write("<keyword> else </keyword>" + "\n");
                tokenizer.advance();
                writer.write("<symbol> { </symbol>" + "\n");
                tokenizer.advance();
                writer.write("<statements>" + "\n");
                compileStatements();
                writer.write("</statements>" + "\n" + "<symbol> } </symbol>" + "\n");
            }
            else {
                if(tokenizer.pointer > 0) tokenizer.pointer--;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compiles a while statement.
     */
    public void compileWhile() {
        try {
            writer.write("<keyword> " + tokenizer.keyWord() + " </keyword>" + "\n");
            tokenizer.advance();
            writer.write("<symbol> " + tokenizer.symbol() + " </symbol>" + "\n");
            compileExpression();
            tokenizer.advance();
            writer.write("<symbol> " + tokenizer.symbol() + " </symbol>" + "\n");
            tokenizer.advance();
            writer.write("<symbol> " + tokenizer.symbol() + " </symbol>" + "\n" + "<statements>" + "\n");
            compileStatements();
            writer.write("</statements>" + "\n" + "<symbol> " + tokenizer.symbol() + " </symbol>" + "\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compiles a do statement.
     */
    public void compileDo() {
        try {
            if (tokenizer.keyWord().equals("do")) writer.write("<keyword> do </keyword>" + "\n");
            compileCall();
            tokenizer.advance();
            writer.write("<symbol> " + tokenizer.symbol() + " </symbol>" + "\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compiles a return statement.
     */
    public void compileReturn() {
        try {
            writer.write("<keyword> return </keyword>" + "\n");
            tokenizer.advance();
            if ((!tokenizer.tokenType().equals("SYMBOL") || tokenizer.symbol() != ';')) {
                if(tokenizer.pointer > 0) tokenizer.pointer--;
                compileExpression();
            }
            if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == ';') {
                writer.write("<symbol> ; </symbol>" + "\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compiles an expression.
     */
    public void compileExpression() {
        try {
            writer.write("<expression>\n");
            compileTerm();
            while (true) {
                tokenizer.advance();
                if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.isOperation()) {
                    if (tokenizer.symbol() == '<') {
                        writer.write("<symbol> &lt; </symbol>" + "\n");
                    }
                    else if (tokenizer.symbol() == '>') {
                        writer.write("<symbol> &gt; </symbol>" + "\n");
                    }
                    else if (tokenizer.symbol() == '&') {
                        writer.write("<symbol> &amp; </symbol>" + "\n");
                    }
                    else {
                        writer.write("<symbol> " + tokenizer.symbol() + " </symbol>" + "\n");
                    }
                    compileTerm();
                }
                else {
                    if (tokenizer.pointer > 0) tokenizer.pointer--;
                    break;
                }
            }
            writer.write("</expression>" + "\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compiles a term. If the current token is an identifier, the routine must resolve it into a variable, and array entry
     * or a subroutine call. A single lookahead token, which may be [, ( or ., suffices to distinguish between the possibilities.
     * Any other token is not part of this term and should not be advanced over.
     */
    public void compileTerm() {
        try {
            writer.write("<term>" + "\n");
            tokenizer.advance();
            if (tokenizer.tokenType().equals("IDENTIFIER")) {
                String prevIdentifier = tokenizer.identifier();
                tokenizer.advance();
                if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == '[') {
                    writer.write("<identifier> " + prevIdentifier + " </identifier>" + "\n" + "<symbol> [ </symbol>" + "\n");
                    compileExpression();
                    tokenizer.advance();
                    writer.write("<symbol> ] </symbol>" + "\n");
                }
                else if (tokenizer.tokenType().equals("SYMBOL") && (tokenizer.symbol() == '(' || tokenizer.symbol() == '.')) {
                    if(tokenizer.pointer > 0) tokenizer.pointer--;
                    if(tokenizer.pointer > 0) tokenizer.pointer--;
                    compileCall();
                }
                else {
                    writer.write("<identifier> " + prevIdentifier + " </identifier>" + "\n");
                    if(tokenizer.pointer > 0) tokenizer.pointer--;
                }
            }
            else {
                if (tokenizer.tokenType().equals("INT_CONST")) {
                    writer.write("<integerConstant> " + tokenizer.intVal() + " </integerConstant>" + "\n");
                }
                else if (tokenizer.tokenType().equals("STRING_CONST")) {
                    writer.write("<stringConstant> " + tokenizer.stringVal() + " </stringConstant>" + "\n");
                }
                else if (tokenizer.tokenType().equals("KEYWORD") && tokenizer.keyWord().matches("this|null|false|true")) {
                    writer.write("<keyword> " + tokenizer.keyWord() + " </keyword>" + "\n");
                }
                else if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == '(') {
                    writer.write("<symbol> " + tokenizer.symbol() + " </symbol>" + "\n");
                    compileExpression();
                    tokenizer.advance();
                    writer.write("<symbol> " + tokenizer.symbol() + " </symbol>" + "\n");
                }
                else if (tokenizer.tokenType().equals("SYMBOL") && (tokenizer.symbol() == '-' || tokenizer.symbol() == '~')) {
                    writer.write("<symbol> " + tokenizer.symbol() + " </symbol>" + "\n");
                    compileTerm();
                }
            }
            writer.write("</term>" + "\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compiles a (possibly empty) comma-seperated list of expressions. Returns the number of expressions in the list.
     */
    public void compileExpressionList() {
        tokenizer.advance();
        if (tokenizer.symbol() == ')' && tokenizer.tokenType().equals("SYMBOL")) {
            if(tokenizer.pointer > 0) tokenizer.pointer--;
        }
        else {
            if(tokenizer.pointer > 0) tokenizer.pointer--;
            compileExpression();
        }
        while (true) {
            tokenizer.advance();
            if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == ',') {
                try {
                    writer.write("<symbol> , </symbol>" + "\n");
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                compileExpression();
            }
            else {
                if(tokenizer.pointer > 0) tokenizer.pointer--;
                break;
            }
        }
    }

    /**
     * Compiles a subroutine call.
     */
    private void compileCall() {
        tokenizer.advance();
        try {
            writer.write("<identifier> " + tokenizer.identifier() + " </identifier>" + "\n");
            tokenizer.advance();
            if(tokenizer.tokenType().equals("SYMBOL")) {
                if (tokenizer.symbol() == '.') {
                    writer.write("<symbol> " + tokenizer.symbol() + " </symbol>" + "\n");
                    tokenizer.advance();
                    writer.write("<identifier> " + tokenizer.identifier() + " </identifier>" + "\n");
                    tokenizer.advance();
                    writer.write("<symbol> " + tokenizer.symbol() + " </symbol>" + "\n" + "<expressionList>" + "\n");
                    compileExpressionList();
                    writer.write("</expressionList>" + "\n");
                    tokenizer.advance();
                    writer.write("<symbol> " + tokenizer.symbol() + " </symbol>" + "\n");
                } else if (tokenizer.symbol() == '(') {
                    writer.write("<symbol> " + tokenizer.symbol() + " </symbol>" + "\n" + "<expressionList>" + "\n");
                    compileExpressionList();
                    writer.write("</expressionList>" + "\n");
                    tokenizer.advance();
                    writer.write("<symbol> " + tokenizer.symbol() + " </symbol>" + "\n");
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
