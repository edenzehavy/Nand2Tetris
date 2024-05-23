import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

enum Type {
    A_INSTURCTION, C_INSTURCTION, L_INSTRUCTION;
}

public class Parser {
    private HashMap<Integer, String> instructions;
    private int linesCounter;

    public Parser(File file) {
        this.instructions = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            this.linesCounter = 0;
            int i = 0;
            while (line != null) {
                if(line.equals("")){
                    line = reader.readLine();
                    continue;
                }
                if (!isWhiteSpace(line)) {
                    line = line.replaceAll(" ", "");
                    instructions.put(i, line);
                    line = reader.readLine();
                    i++;
                    continue;
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean isWhiteSpace(String line) {
        char c;
        if(line.indexOf('/') != -1) return true;
        for(int i = 0; i < line.length(); i++){
            c = line.charAt(i);
            if (c != ' '){
                return false;
            }
        }
        return true;
    }

    public boolean hasMoreLines() {
        return (linesCounter < instructions.size());
    }

    public void advance() {
        if (hasMoreLines()) {
            linesCounter++;
        }
    }

    public int getLinesCounter(){
        return linesCounter;
    }

    public void reset(){
        linesCounter = 0;
    }

    public Type instructionType() {
        char c = instructions.get(linesCounter).charAt(0);
        switch (c) {
            case '@':
                return Type.A_INSTURCTION;
            case '(':
                return Type.L_INSTRUCTION;
            default:
                return Type.C_INSTURCTION;
        }
    }

    public String symbol() {
        String instruction = instructions.get(linesCounter);
        if (instructionType() == Type.A_INSTURCTION) return instruction.substring(1);
        else if (instructionType() == Type.L_INSTRUCTION) return instruction.substring(1, instruction.length() - 1);
        throw new Error("No symbol in C-instruction");
    }

    public String dest() {
        String instruction = instructions.get(linesCounter);
        if(instruction.indexOf('=') != -1){
            return instruction.substring(0, instruction.indexOf("="));
        }
        return "";
    }

    public String comp() {
        String instruction = instructions.get(linesCounter);
        if (instructionType() == Type.C_INSTURCTION) {
            int index = instruction.indexOf(";");
            if (index == -1) return instruction.substring(instruction.indexOf("=") + 1);
            return instruction.substring(instruction.indexOf("=") + 1, index);
        }
        return "";
    }

    public String jump() {
        String instruction = instructions.get(linesCounter);
        if (instructionType() == Type.C_INSTURCTION) {
            int index = instruction.indexOf(";");
            if (index == -1) return null;
            return instruction.substring(index + 1);
        }
        return "";
    }


}
