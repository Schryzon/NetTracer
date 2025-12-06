import java.util.Scanner;

public class CLI_Shell {
    private boolean isRunning;
    private CLI_Registry registry;
    private Stack commandHistory;
    private int mode = 0;
    
    private String hostname = "Switch"; // nanti bisa nambah kek router dll

    public CLI_Shell() {
        this.registry = new CLI_Registry();
        this.isRunning = true;
        this.commandHistory = new Stack();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        
        while (isRunning) {
            String promptChar = (mode == 1) ? "(config)# " : "> ";
            System.out.print(hostname + promptChar);
            
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim();
                
                if (!input.trim().isEmpty()) {
                    commandHistory.push(input); // Push ke Stack
                }

                if (input.equals("configure terminal") || input.equals("conf t")) {
                    if (mode == 0) {
                        mode = 1;
                        System.out.println("Enter configuration commands, one per line. End with CNTL/Z.");
                        continue; 
                    }
                }

                // Keluar mode config
                if (input.equals("exit") || input.equals("end")) {
                    if (mode == 1) {
                        mode = 0; // Balik ke mode awal
                        continue; // Skip ke loop berikutnya
                    }
                }

                try {
                    registry.dispatch(input);
                } catch (Exception e) {
                    System.out.println("Command Error: " + e.getMessage());
                }
            } else {
                isRunning = false;
            }
        }
        scanner.close();
    }
}