import java.util.HashMap;

public class SymbolTable{
    private HashMap<String,Integer> table;

    public SymbolTable() {
        this.table = new HashMap<>();
        for (int i = 0; i < 16; i++) {
            this.table.put("R" + i, i);
        }
        this.table.put("SCREEN", 16384);
        this.table.put("KBD", 24576);
        this.table.put("SP", 0);
        this.table.put("LCL", 1);
        this.table.put("ARG", 2);
        this.table.put("THIS", 3);
        this.table.put("THAT", 4);
    }

    public void addEntry(String symbol, int address) {
        this.table.put(symbol, address);
    }

    public boolean contains(String symbol){
        return this.table.containsKey(symbol);
    }

    public int getAddress(String symbol){
        return this.table.get(symbol);
    }









}
