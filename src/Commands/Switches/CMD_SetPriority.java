package Commands.Switches;

import CLI.CLI_Command;
import Topology.Graph;

public class CMD_SetPriority implements CLI_Command {
    public void execute(String[] args) {
        if (args == null || args.length < 2) {
            System.out.println("usage: set-priority <SW> <PRIO>");
            return;
        }
        String sw = args[0];
        int pr;
        try { pr = Integer.parseInt(args[1]); }
        catch (Exception e){ System.out.println("% bad priority number"); return; }

        Graph.setPriority(sw, pr);
        System.out.println("% priority updated");
    }
    public String getDescription() { return "Set STP priority for a switch"; }
}
