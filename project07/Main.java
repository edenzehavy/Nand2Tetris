import java.io.File;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Please enter a valid path");
            return;
        }
        String path = args[0];
        File file = new File(path);
        if (file.isFile() && file.getName().endsWith("vm")) {
            toASMFromFile(path);
        } else if (file.isDirectory()) {
            File[] listOfFiles = file.listFiles();
            if (listOfFiles != null) {
                String asmFileName = file.getName() + ".asm";
                String asmPath = file.getAbsolutePath() + File.separator + asmFileName;
                File asmFile = new File(asmPath);

                CodeWriter writer = new CodeWriter(asmFile);
                for (File f : listOfFiles) {
                    if (f.isFile() && f.getName().toLowerCase().endsWith("vm")) {
                        appendToASMFile(writer, f.getAbsolutePath());
                    }
                }
                writer.close();
            } else {
                System.out.println("Please enter a valid path");
            }
        }
    }

    public static void toASMFromFile(String filePath){
        File file = new File(filePath);
        if (!file.exists()) throw new Error("File not found");
        Parser parser = new Parser(file);

        String path = file.getAbsolutePath();
        String fullName = file.getName();
        int indexOfDot = fullName.indexOf(".");
        String name = fullName.substring(0, indexOfDot);
        String directory = file.getParent();
        String asmPath = directory + File.separator + name + ".asm";
        File asmFile = new File(asmPath);

        CodeWriter writer = new CodeWriter(asmFile);
        while(parser.hasMoreLines()) {
            Type cmdType = parser.commandType();
            if (cmdType == Type.C_ARITHMETIC) {
                writer.writeArithmetic(parser.args1());
            } else if (cmdType == Type.C_PUSH) {
                writer.writePushPop("push", parser.args1(), parser.args2());
            } else if (cmdType == Type.C_POP) {
                writer.writePushPop("pop", parser.args1(), parser.args2());
            }
            parser.advance();
        }
        writer.close();
    }
    public static void appendToASMFile(CodeWriter writer, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) throw new Error("File not found");
        Parser parser = new Parser(file);
        while (parser.hasMoreLines()) {
            Type cmdType = parser.commandType();
            if (cmdType == Type.C_ARITHMETIC) {
                writer.writeArithmetic(parser.args1());
            } else if (cmdType == Type.C_PUSH) {
                writer.writePushPop("push", parser.args1(), parser.args2());
            } else if (cmdType == Type.C_POP) {
                writer.writePushPop("pop", parser.args1(), parser.args2());
            }
            parser.advance();
        }
    }
}