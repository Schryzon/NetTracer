public class CMD_Show implements CLI_Command {
    public void execute(String[] args) {
        if (args.length == 0) {
            System.out.println("Incomplete command. Try 'show version'");
            return;
        }

        String subCommand = args[0];
        
        if (subCommand.equals("version")) {
            System.out.println("NetTracer IOS 1.1");
        } else if (subCommand.equals("topology")) {
            System.out.println("[Stub] Graph topology not yet ready (Waiting for Dev C)");
        } else if (subCommand.equals("history")) {
            // Nanti diisi logika menampilkan history
            System.out.println("[Stub] History display coming soon");
        } else {
            System.out.println("Unknown show option: " + subCommand);
        }
    }

    public String getDescription() {
        return "Show running system information";
    }
}