import java.util.HashMap;
import java.util.Map;

public class CLI_Registry {
    private Map<String, CLI_Command> commandMap;

    public CLI_Registry() {
        commandMap = new HashMap<>();
        registerCommands();
    }

    private void registerCommands() {
        // Daftarin command di sini
        commandMap.put("version", new CMD_Version());
        commandMap.put("show", new CMD_Show());
    }

    public void dispatch(String inputLine) {
        if (inputLine == null || inputLine.trim().isEmpty()) return;

        String[] parts = inputLine.trim().split("\\s+");
        String keyword = parts[0].toLowerCase();

        // Cek command khusus exit
        if (keyword.equals("exit") || keyword.equals("quit")) {
            System.out.println("Bye!");
            System.exit(0);
        }

        // Cari command di map
        if (commandMap.containsKey(keyword)) {
            String[] args = new String[parts.length - 1];
            System.arraycopy(parts, 1, args, 0, args.length);
            
            // Eksekusi via Interface
            commandMap.get(keyword).execute(args);
        } else {
            System.out.println("Unknown command: " + keyword);
        }
    }
}