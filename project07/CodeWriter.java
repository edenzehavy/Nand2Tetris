import java.io.*;

public class CodeWriter {
    private BufferedWriter writer;
    private int loopsIndex;


    public CodeWriter(File output){
        try {
            this.writer = new BufferedWriter(new FileWriter(output));
            this.loopsIndex = 0;
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void writeArithmetic(String command) {
        String outputCmd = "@SP" + "\n" + "AM=M-1" + "\n" + "D=M" + "\n" + "A=A-1" + "\n";
        switch (command) {
            case "add": {
                outputCmd += "M=M+D" + "\n";
                break;
            }
            case "sub": {
                outputCmd += "M=M-D" + "\n";
                break;
            }
            case "and": {
                outputCmd += "M=M&D" + "\n";
                break;
            }
            case "or": {
                outputCmd += "M=M|D" + "\n";
                break;
            }
            case "gt": {
                outputCmd += "D=M-D" + "\n" + "@FALSE" + loopsIndex + "\n" + "D;JLE" + "\n" + "@SP" + "\n" +
                        "A=M-1" + "\n" + "M=-1" + "\n" + "@CONTINUE" + loopsIndex + "\n" + "0;JMP" + "\n" + "(FALSE" + loopsIndex + ")" + "\n" + "@SP" + "\n" + "A=M-1" + "\n" +
                        "M=0" + "\n" + "(CONTINUE" + loopsIndex + ")" + "\n";
                loopsIndex++;
                break;
            }
            case "lt": {
                outputCmd += "D=M-D" + "\n" + "@FALSE" + loopsIndex + "\n" + "D;JGE" + "\n" + "@SP" + "\n" +
                        "A=M-1" + "\n" + "M=-1" + "\n" + "@CONTINUE" + loopsIndex + "\n" + "0;JMP" + "\n" + "(FALSE" + loopsIndex + ")" + "\n" + "@SP" + "\n" + "A=M-1" + "\n" +
                        "M=0" + "\n" + "(CONTINUE" + loopsIndex + ")" + "\n";
                loopsIndex++;
                break;
            }
            case "eq": {
                outputCmd += "D=M-D" + "\n" + "@FALSE" + loopsIndex + "\n" + "D;JNE" + "\n" + "@SP" + "\n" +
                        "A=M-1" + "\n" + "M=-1" + "\n" + "@CONTINUE" + loopsIndex + "\n" + "0;JMP" + "\n" + "(FALSE" + loopsIndex + ")" + "\n" + "@SP" + "\n" + "A=M-1" + "\n" +
                        "M=0" + "\n" + "(CONTINUE" + loopsIndex + ")" + "\n";
                loopsIndex++;
                break;
            }
            case "neg": {
                outputCmd = "D=0" + "\n" + "@SP" + "\n" + "A=M-1" + "\n" + "M=D-M" + "\n";
                break;
            }
            case "not": {
                outputCmd = "@SP" + "\n" + "A=M-1" + "\n" + "M=!M" + "\n";
                break;
            }
            default:
                break;
        }
        try {
            writer.write(outputCmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writePushPop(String command, String segment, int index){
        String outputCmd = "";
        if(command.equals("push")) {
            switch (segment) {
                case "static": {
                    int x = 16 + index;
                    String str = "" + x;
                    outputCmd += pushPrefix(str, -1);
                    break;
                }
                case "this": {
                    outputCmd += pushPrefix(segment.toUpperCase(), index);
                    break;
                }
                case "that": {
                    outputCmd += pushPrefix(segment.toUpperCase(), index);
                    break;
                }
                case "local": {
                    outputCmd += pushPrefix("LCL", index);
                    break;
                }
                case "argument": {
                    outputCmd += pushPrefix("ARG", index);
                    break;
                }
                case "pointer":{
                    if(index == 1) outputCmd += pushPrefix("THAT", -1);
                    else outputCmd += pushPrefix("THIS", -1);
                    break;
                }
                case "temp": {
                    outputCmd += pushPrefix("R5", index + 5);
                    break;
                }
                case "constant": {
                    outputCmd += "@" + index + "\n" + "D=A" + "\n" + "@SP" + "\n" + "A=M" + "\n" + "M=D" + "\n" + "@SP" + "\n" + "M=M+1" + "\n";
                    break;
                }
            }
        }
        else if(command.equals("pop")){
            switch (segment) {
                case "static": {
                    int x = 16 + index;
                    String str = "" + x;
                    outputCmd += popPrefix(str, -1);
                    break;
                }
                case "this": {
                    outputCmd += popPrefix(segment.toUpperCase(), index);
                    break;
                }
                case "that": {
                    outputCmd += popPrefix(segment.toUpperCase(), index);
                    break;
                }
                case "local": {
                    outputCmd += popPrefix("LCL", index);
                    break;
                }
                case "argument": {
                    outputCmd += popPrefix("ARG", index);
                    break;
                }
                case "pointer":{
                    if(index == 1) outputCmd += popPrefix("THAT", -1);
                    else outputCmd += popPrefix("THIS", -1);
                    break;
                }
                case "temp": {
                    outputCmd += popPrefix("R5", index + 5);
                    break;
                }
                case "constant": {
                    outputCmd += "@" + index + "\n" + "D=A" + "\n" + "@SP" + "\n" + "A=M" + "\n" + "M=D" + "\n" + "@SP" + "\n" + "M=M+1" + "\n";
                    break;
                }
            }
        }
        try {
            writer.write(outputCmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String pushPrefix(String segment, int index){
        String prefix = "";
        if(index == -1){
            prefix = "@" + segment + "\n" + "D=M" + "\n" + "@SP" + "\n" + "A=M" + "\n" + "M=D" + "\n" + "@SP" + "\n" + "M=M+1" + "\n";
        }
        else{
            prefix = "@" + segment + "\n" + "D=M" + "\n" + "@" + index + "\n" + "A=D+A" + "\n" + "D=M" + "\n" + "@SP" + "\n" + "A=M" + "\n" + "M=D" + "\n" + "@SP" + "\n" + "M=M+1" + "\n";
        }
        return prefix;
    }
    public String popPrefix(String segment, int index){
        String prefix = "";
        if(index == -1){
            prefix = "@" + segment + "\n" + "D=A" + "\n" + "@R13" + "\n" + "M=D" + "\n" + "@SP" + "\n" + "AM=M-1" + "\n" + "D=M" + "\n" + "@R13" + "\n" + "A=M" + "\n" + "M=D" + "\n";
        }
        else{
            prefix = "@" + segment + "\n" + "D=M" + "\n" + "@" + index + "\n" + "D=D+A" + "\n" + "@R13" + "\n" + "M=D" + "\n" + "@SP" + "\n" + "AM=M-1" + "\n" + "D=M" + "\n" + "@R13" + "\n" + "A=M" + "\n" + "M=D" + "\n";
        }
        return prefix;
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
