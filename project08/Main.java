import java.io.File;

public class Main {
    public static void main(String[] args) {
        File inputPath = new File(args[0]);

        if (inputPath.isFile()) {
            toASMFromFile(inputPath);
        } else if (inputPath.isDirectory()) {
            toASMFromDirectory(inputPath);
        } else {
            System.out.println("Error, Invalid input");
        }
    }

    private static void toASMFromFile(File inputFile) {
        if (inputFile.getName().endsWith(".vm")) {
            Parser parser = new Parser(inputFile);
            File asmFile = getOutputFile(inputFile);
            CodeWriter writer = new CodeWriter(asmFile);

            if (inputFile.getName().startsWith("Sys")) {
                writer.writeInit();
            }

            toASMFromFile(parser, writer);
            writer.close();
        } else {
            System.out.println("Please enter a path of a valid vm file");
        }
    }

    private static void toASMFromDirectory(File inputDirectory) {
        File[] files = inputDirectory.listFiles();

        if (files != null) {
            File asmFile = new File(inputDirectory.getAbsolutePath() + File.separator + inputDirectory.getName() + ".asm");
            CodeWriter writer = new CodeWriter(asmFile);

            for (File file : files) {
                if (isValidVMFile(file, "Sys")) {
                    writer.writeInit();
                    writer.setFileName(file.getName());
                    toASMFromFile(new Parser(file), writer);
                }
            }

            for (File file : files) {
                if (isValidVMFile(file, "")) {
                    writer.setFileName(file.getName());
                    toASMFromFile(new Parser(file), writer);
                }
            }

            writer.close();
        }
    }

    private static void toASMFromFile(Parser parser, CodeWriter writer) {
        while (parser.hasMoreLines()) {
            Type commandType = parser.commandType();
            if (commandType == Type.C_ARITHMETIC) {
                writer.writeArithmetic(parser.args1());
            } else if (commandType == Type.C_POP || commandType == Type.C_PUSH) {
                writer.writePushPop(commandType.name(), parser.args1(), parser.args2());
            } else if (commandType == Type.C_LABEL) {
                writer.writeLabel(parser.args1());
            } else if (commandType == Type.C_GOTO) {
                writer.writeGoto(parser.args1());
            } else if (commandType == Type.C_IF) {
                writer.writeIf(parser.args1());
            } else if (commandType == Type.C_RETURN) {
                writer.writeReturn();
            } else if (commandType == Type.C_FUNCTION) {
                writer.writeFunction(parser.args1(), parser.args2());
            } else if (commandType == Type.C_CALL) {
                writer.writeCall(parser.args1(), parser.args2());
            }
            parser.advance();
        }
    }

    private static boolean isValidVMFile(File file, String prefix) {
        return file.isFile() && file.getName().startsWith(prefix) && file.getName().endsWith(".vm");
    }

    private static File getOutputFile(File inputFile) {
        String outputFileName = inputFile.isDirectory() ?
                inputFile.getAbsolutePath() + File.separator + inputFile.getName() + ".asm" :
                inputFile.getAbsolutePath().replaceFirst("[.][^.]+$", ".asm");
        return new File(outputFileName);
    }
}
