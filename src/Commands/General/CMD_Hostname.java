package Commands.General;

import CLI.CLI_Command;
import CLI.CLI_Shell;

public class CMD_Hostname implements CLI_Command {
    public void execute(String[] args) {
        if (args == null || args.length < 1) {
            System.out.println("usage: change-hostname <NEW_HOSTNAME>");
            return;
        }
        String newHostname = args[0];
        CLI_Shell.setHostname(newHostname);
        System.out.println("% Hostname changed to " + newHostname);
    }
    public String getDescription() { return "Change CLI prompt hostname"; }
}
