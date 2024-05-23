import java.io.File;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java JackAnalyzer <file or directory>");
            return;
        }

        File input = new File(args[0]);

        if (!input.exists()) {
            System.out.println("File or directory does not exist.");
            return;
        }

        if (input.isFile() && !input.getName().endsWith(".jack")) {
            System.out.println("The given file is not a jack file");
            return;
        }

        if (input.isFile()) {
            compileFile(input);
        } else {
            File[] files = input.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".jack")) {
                        compileFile(file);
                    }
                }
            }
        }
    }

    public static void compileFile(File file) {
        String outputPath = file.getAbsolutePath().replace(".jack", ".xml");
        File output = new File(outputPath);
        CompilationEngine compilationEngine = new CompilationEngine(file, output);
        compilationEngine.compileClass();
    }
}
