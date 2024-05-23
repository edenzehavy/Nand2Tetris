import java.io.*;

public class CodeWriter {
    private BufferedWriter writer;
    private  int LABELCOUNT = 0;
    private  String fileName = "";


    public CodeWriter(File output){
        try {
            this.writer = new BufferedWriter(new FileWriter(output));
            this.LABELCOUNT = 0;
            this.fileName = output.getName();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void setFileName(String fileName){
        this.fileName = fileName.substring(0, fileName.lastIndexOf('.'));
    }

    public void writeInit(){
        try {
            writer.write("@256" + "\n" + "D=A" + "\n" + "@SP" + "\n" + "M=D" + "\n");
            writeCall("Sys.init", 0);
        }
        catch (IOException e){
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
                outputCmd += "D=M-D" + "\n" + "@FALSE" + LABELCOUNT + "\n" + "D;JLE" + "\n" + "@SP" + "\n" +
                        "A=M-1" + "\n" + "M=-1" + "\n" + "@CONTINUE" + LABELCOUNT + "\n" + "0;JMP" + "\n" + "(FALSE" + LABELCOUNT + ")" + "\n" + "@SP" + "\n" + "A=M-1" + "\n" +
                        "M=0" + "\n" + "(CONTINUE" + LABELCOUNT + ")" + "\n";
                LABELCOUNT++;
                break;
            }
            case "lt": {
                outputCmd += "D=M-D" + "\n" + "@FALSE" + LABELCOUNT + "\n" + "D;JGE" + "\n" + "@SP" + "\n" +
                        "A=M-1" + "\n" + "M=-1" + "\n" + "@CONTINUE" + LABELCOUNT + "\n" + "0;JMP" + "\n" + "(FALSE" + LABELCOUNT + ")" + "\n" + "@SP" + "\n" + "A=M-1" + "\n" +
                        "M=0" + "\n" + "(CONTINUE" + LABELCOUNT + ")" + "\n";
                LABELCOUNT++;
                break;
            }
            case "eq": {
                outputCmd += "D=M-D" + "\n" + "@FALSE" + LABELCOUNT + "\n" + "D;JNE" + "\n" + "@SP" + "\n" +
                        "A=M-1" + "\n" + "M=-1" + "\n" + "@CONTINUE" + LABELCOUNT + "\n" + "0;JMP" + "\n" + "(FALSE" + LABELCOUNT + ")" + "\n" + "@SP" + "\n" + "A=M-1" + "\n" +
                        "M=0" + "\n" + "(CONTINUE" + LABELCOUNT + ")" + "\n";
                LABELCOUNT++;
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
        if(command.equals("C_PUSH")) {
            switch (segment) {
                case "static": {
                    outputCmd += "@" + this.fileName + "." + index + "\n" + "D=M" + "\n" + "@SP" + "\n" + "A=M" + "\n" + "M=D" + "\n" + "@SP" + "\n" + "M=M+1" + "\n";
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
                    else if(index == 0){
                        outputCmd += pushPrefix("THIS", -1);
                    }
                    break;
                }
                case "temp": {
                    outputCmd += "@R" + (index + 5) + "\n" + "D=M" + "\n" + "@SP" + "\n" + "A=M" + "\n" + "M=D" + "\n" + "@SP" + "\n" + "M=M+1" + "\n";
                    break;
                }
                case "constant": {
                    outputCmd += "@" + index + "\n" + "D=A" + "\n" + "@SP" + "\n" + "A=M" + "\n" + "M=D" + "\n" + "@SP" + "\n" + "M=M+1" + "\n";
                    break;
                }
            }
        }
        else if(command.equals("C_POP")){
            switch (segment) {
                case "static": {
                    outputCmd += "@SP" + "\n" + "AM=M-1" + "\n" + "D=M" + "\n" + "@" + this.fileName + "." + index + "\n" + "M=D" + "\n";
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
                    else if(index == 0){
                        outputCmd += popPrefix("THIS", -1);
                    }
                    break;
                }
                case "temp": {
                    outputCmd += "@SP" + "\n" + "AM=M-1" + "\n" + "D=M" + "\n" + "@R" + (index + 5) + "\n" + "M=D" + "\n";
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

    public void writeLabel(String label){
        String toWrite = "(" + label + ")" + "\n";
        try{
            writer.write(toWrite);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void writeGoto(String label){
        String toWrite = "@" + label + "\n" + "0;JMP" + "\n";
        try{
            writer.write(toWrite);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void writeIf(String label){
        String toWrite = "@SP" + "\n" + "AM=M-1" + "\n" + "D=M" + "\n" + "@" + label + "\n" + "D;JNE" + "\n";
        try{
            writer.write(toWrite);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void writeFunction(String functionName, int nVars){
        String toWrite = "(" + functionName + ")" + "\n";
        try{
            writer.write(toWrite);
            for (int i = 0; i < nVars; i++){
                this.writer.write("@LCL" + "\n" + "D=M" + "\n" +"@" + i + "\n" + "A=D+A" + "\n" + "M=0" + "\n" + "@SP" + "\n" + "M=M+1" + "\n");
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void writeCall(String functionName, int nArgs){
        String toWrite = "@" + functionName + LABELCOUNT + "\n" + "D=A" + "\n" + "@SP" + "\n" + "A=M" + "\n" + "M=D" + "\n" + "@SP" + "\n" + "M=M+1" + "\n" + pushPrefix("LCL", -1) + pushPrefix("ARG", -1) +
                pushPrefix("THIS", -1) + pushPrefix("THAT", -1) + "@SP" + "\n" + "D=M" + "\n" + "@5" + "\n" + "D=D-A" + "\n" + "@" + nArgs + "\n" + "D=D-A" + "\n" + "@ARG" + "\n" + "M=D" + "\n" + "@SP" +
                "\n" + "D=M" + "\n" + "@LCL" + "\n" + "M=D" + "\n";
        try{
            writer.write(toWrite);
            writeGoto(functionName);
            writeLabel(functionName + LABELCOUNT);
            LABELCOUNT++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeReturn(){
        String toWrite = "@LCL" + "\n" + "D=M" + "\n" + "@R15" + "\n" + "M=D" + "\n" + "@R15" + "\n" + "D=M" + "\n" + "@5" + "\n" + "A=D-A" + "\n" + "D=M" + "\n" + "@R14" + "\n" + "M=D" + "\n" + "@SP" + "\n" + "AM=M-1" + "\n" + "D=M" + "\n" + "@ARG" + "\n" + "A=M" + "\n" + "M=D" + "\n" + "@ARG" + "\n" + "D=M+1" + "\n" +
                "@SP" + "\n" + "M=D" + "\n" + "@R15" + "\n" + "AM=M-1" + "\n" + "D=M" + "\n" + "@THAT" + "\n" + "M=D" + "\n" + "@R15" + "\n" + "AM=M-1" + "\n" + "D=M" + "\n" + "@THIS" + "\n" + "M=D" + "\n" + "@R15" + "\n" + "AM=M-1" + "\n" + "D=M" + "\n" + "@ARG" + "\n" + "M=D" + "\n" + "@R15" + "\n" + "AM=M-1"
                + "\n" + "D=M" + "\n" + "@LCL" + "\n" + "M=D" + "\n" + "@R14" + "\n" + "A=M" + "\n" + "0;JMP" + "\n";
        try{
            writer.write(toWrite);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
