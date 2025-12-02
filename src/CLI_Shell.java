import java.util.Scanner;

public class CLI_Shell {
    private boolean isRunning;
    private CLI_Registry registry;
    
    private String hostname = "Switch"; // nanti bisa nambah kek router dll

    public CLI_Shell() {
        this.registry = new CLI_Registry();
        this.isRunning = true;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        
        while (isRunning) {
            System.out.print(hostname + "> ");
            
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                
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