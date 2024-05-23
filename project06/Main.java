import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String args[]) {
        if (args.length != 1) System.out.println("Please enter a valid path");
        String path = args[0];
        File file = new File(path);
        if(file.isFile() && file.getName().endsWith("asm")){
            assemble(path);
        }
        else if (file.isDirectory()){
            File[] listOfFiles = file.listFiles();
            if (listOfFiles != null){
                for (File f : listOfFiles){
                    if (f.isFile() && f.getName().toLowerCase().endsWith("asm")){
                        assemble(f.getAbsolutePath());
                    }
                }
            } else {
                System.out.println("Please enter a valid path");
            }
        }
    }

    public static void assemble(String filePath){
        File file = new File(filePath);
        if (!file.exists()) throw new Error("File not found");
        Parser parser = new Parser(file);
        SymbolTable st = new SymbolTable();
        while (parser.hasMoreLines()) {
            if (parser.instructionType() == Type.L_INSTRUCTION && !st.contains(parser.symbol())) {
                st.addEntry(parser.symbol(), parser.getLinesCounter());
            }
            parser.advance();
        }
        parser.reset();

        String path = file.getAbsolutePath();
        String fullName = file.getName();
        int indexOfDot = fullName.indexOf(".");
        String name = fullName.substring(0, indexOfDot);
        int nameIndex = file.getAbsolutePath().indexOf(name);
        String directory = path.substring(0, nameIndex);
        String hackPath = directory + name + ".hack";
        File hackFile = new File(hackPath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(hackFile))) {
            int stAddress = 16;
            while (parser.hasMoreLines()) {
                if (parser.instructionType() == Type.A_INSTURCTION) {
                    String symbol = parser.symbol();
                    String binary = "";
                    if (!isNumber(symbol)) {
                        if (!st.contains(symbol)) {
                            st.addEntry(symbol, stAddress++);
                        }
                        binary = convertToBinary(st.getAddress(symbol));
                        writer.write(0 + binary);
                        writer.newLine();
                    } else {
                        binary = convertToBinary(Integer.parseInt(symbol));
                        writer.write(0 + binary);
                        writer.newLine();
                    }
                }
                if (parser.instructionType() == Type.C_INSTURCTION) {
                    String binaryCmd = Code.comp(parser.comp()) + Code.dest(parser.dest()) + Code.jump(parser.jump());
                    writer.write(111 + binaryCmd);
                    writer.newLine();
                }
                parser.advance();
            }
        } catch (IOException e) {
            throw new Error("Couldn't write to file");
        }
    }

    public static String convertToBinary(int number) {
        return String.format("%15s", Integer.toBinaryString(number)).replace(' ', '0');
    }

    public static boolean isNumber(String input) {
        // Using regular expression to check if the string contains only numbers
        return input.matches("\\d+");
    }
}