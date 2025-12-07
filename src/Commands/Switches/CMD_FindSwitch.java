package Commands.Switches;

import CLI.CLI_Command;
import Topology.Graph;

public class CMD_FindSwitch implements CLI_Command {
    public void execute(String[] args) {
        if (args == null || args.length < 1) {
            System.out.println("usage: find-switch <NAME>");
            return;
        }
        String name = args[0];
        if (Graph.hasSwitch(name)) {
            System.out.println("Switch '" + name + "' exists in the topology.");
        } else {
            System.out.println("Switch '" + name + "' does NOT exist in the topology.");
        }
    }
    public String getDescription() { return "Check if a switch exists in the topology"; }
}
