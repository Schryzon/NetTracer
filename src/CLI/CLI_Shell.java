package CLI;
import java.util.Scanner;

public class CLI_Shell {
    public boolean isRunning;
    public CLI_Registry registry;
    public static String hostname = "ios";

    public static void setHostname(String newHostname) {
        if (newHostname != null && !newHostname.isEmpty()) {
            hostname = newHostname;
        }
    }

    public CLI_Shell() {
        this.registry = new CLI_Registry();
        CLI_Registry.init(); // Add all commands
        this.isRunning = true;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (isRunning) {
            String promptChar = "> ";
            System.out.print(hostname + promptChar);

            if (!scanner.hasNextLine()) break;
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                HistoryManager.logCommand(input);
            }
            
            try {
                registry.dispatch(input);   // registry sudah ter-init
                System.out.println();
            } catch (Exception e) {
                System.out.println("Command Error: " + e.getMessage());
            }
        }
        scanner.close();
    }
}
