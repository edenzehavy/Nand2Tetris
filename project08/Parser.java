import java.io.*;
import java.util.HashMap;

enum Type {
    C_ARITHMETIC, C_PUSH, C_POP, C_LABEL, C_GOTO, C_RETURN, C_IF, C_FUNCTION, C_CALL
}

public class Parser {
    private HashMap<Integer, Type> cmdTypes;
    private HashMap<Integer, String> arithmeticCmds;
    private HashMap<Integer, String> commands;
    private int linesCounter;


    public Parser(File file) {
        this.arithmeticCmds = new HashMap<>();
        this.arithmeticCmds.put(1, "add");
        this.arithmeticCmds.put(2, "sub");
        this.arithmeticCmds.put(3, "neg");
        this.arithmeticCmds.put(4, "eq");
        this.arithmeticCmds.put(5, "gt");
        this.arithmeticCmds.put(6, "lt");
        this.arithmeticCmds.put(7, "and");
        this.arithmeticCmds.put(8, "or");
        this.arithmeticCmds.put(9, "not");

        this.cmdTypes = new HashMap<>();
        this.cmdTypes.put(1, Type.C_ARITHMETIC);
        this.cmdTypes.put(2, Type.C_PUSH);
        this.cmdTypes.put(3, Type.C_POP);
        this.cmdTypes.put(4, Type.C_LABEL);
        this.cmdTypes.put(5, Type.C_GOTO);
        this.cmdTypes.put(6, Type.C_IF);
        this.cmdTypes.put(7, Type.C_FUNCTION);
        this.cmdTypes.put(8, Type.C_RETURN);
        this.cmdTypes.put(9, Type.C_RETURN);

        this.commands = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            this.linesCounter = 0;
            int i = 0;
            while (line != null) {
                if (line.equals("")) {
                    line = reader.readLine();
                    continue;
                }
                commands.put(i, removeComments(line).trim());
                line = reader.readLine();
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String removeComments(String line) {
        int index = line.indexOf('/');
        if (index != -1) {
            return line.substring(0, index);
        }
        return line;
    }

    public String removeSpacesPrefix(String line) {
        while(line.charAt(0) == ' ') line = line.substring(1);
        return line;
    }

    public boolean hasMoreLines() {
        return (linesCounter < commands.size());
    }

    public void advance() {
        if (hasMoreLines()) {
            linesCounter++;
        }
    }

    public Type commandType() {
        String[] splitCommand = commands.get(linesCounter).split(" ");
        if (arithmeticCmds.containsValue(splitCommand[0])){
            return Type.C_ARITHMETIC;
        }
        switch (splitCommand[0]) {
            case "return":
                return Type.C_RETURN;
            case "push":
                return Type.C_PUSH;
            case "pop":
                return Type.C_POP;
            case "label":
                return Type.C_LABEL;
            case "if-goto":
                return Type.C_IF;
            case "goto":
                return Type.C_GOTO;
            case "function":
                return Type.C_FUNCTION;
            case "call":
                return Type.C_CALL;
        }
        return null;
    }

    public String args1() {
        String[] splitCommand = commands.get(linesCounter).split(" ");
        if (splitCommand.length == 1 && commandType() != Type.C_RETURN) return splitCommand[0];
        return splitCommand[1];
    }

    public int args2() {
        String[] splitCommand = commands.get(linesCounter).split(" ");
        if (commandType() == Type.C_PUSH || commandType() == Type.C_POP || commandType() == Type.C_CALL || commandType() == Type.C_FUNCTION) {
            return Integer.parseInt(splitCommand[2]);
        }
         return 0;
    }

}

