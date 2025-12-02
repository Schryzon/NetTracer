public class CLI_Registry {
    
    public CLI_Registry() {
    
    }

    public void dispatch(String inputLine) {
        if (inputLine == null || inputLine.trim().isEmpty()) return;

        String[] parts = inputLine.trim().split("\\s+");
        String keyword = parts[0].toLowerCase();

        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);

        switch (keyword) {
            case "exit":
            case "quit":
                System.out.println("Shutting down...");
                System.exit(0);
                break;
                
            case "help":
                System.out.println("Available commands: show, help, exit");
                break;
                
            case "show":
                handleShow(args);
                break;
                
            case "ver":
            case "version":
                System.out.println("NetTracer IOS Version 1.0 (Day 1 Build)");
                break;

            default:
                System.out.println("Error: Unknown command '" + keyword + "'");
        }
    }

    private void handleShow(String[] args) {
        if (args.length == 0) {
            System.out.println("Incomplete command. Try 'show version' or 'show topology'");
            return;
        }
        
        if (args[0].equals("version")) {
            System.out.println("NetTracer IOS 1.0");
        } else if (args[0].equals("topology")) {
            System.out.println("[Stub] Topology Graph not yet implemented (Waiting for Dev C)");
        } else {
            System.out.println("Unknown show option: " + args[0]);
        }
    }
}