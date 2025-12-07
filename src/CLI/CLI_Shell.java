package CLI;
import java.util.Scanner;

public class CLI_Shell {
    private boolean isRunning;
    private CLI_Registry registry;
    private Stack commandHistory;
    private CLI_Mode mode = CLI_Mode.NORMAL;
    public enum CLI_Mode {
        NORMAL,
        CONFIG;
    }
    private String hostname = "ios";

    public CLI_Shell() {
        this.registry = new CLI_Registry();
        CLI_Registry.init(); // Add all commands
        this.isRunning = true;
        this.commandHistory = new Stack();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (isRunning) {
            String promptChar = (mode == CLI_Mode.CONFIG) ? "(config)# " : "> ";
            System.out.print(hostname + promptChar);

            if (!scanner.hasNextLine()) break;
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                HistoryManager.logCommand(input);
            }

            // masuk mode config
            if ((input.equals("configure terminal") || input.equals("conf t")) && mode == CLI_Mode.NORMAL) {
                mode = CLI_Mode.CONFIG;
                System.out.println("Enter configuration commands, one per line. End with CNTL/Z.");
                continue;
            }
            // keluar mode config
            if ((input.equals("exit") || input.equals("end")) && mode == CLI_Mode.CONFIG) {
                mode = CLI_Mode.NORMAL;
                continue;
            }

            try {
                registry.dispatch(input);   // registry sudah ter-init
            } catch (Exception e) {
                System.out.println("Command Error: " + e.getMessage());
            }
        }
        scanner.close();
    }
}
