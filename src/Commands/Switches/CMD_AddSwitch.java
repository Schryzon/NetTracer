package Commands.Switches;

import CLI.CLI_Command;
import Topology.Graph;

public class CMD_AddSwitch implements CLI_Command {
    public void execute(String[] args) {
        if (args == null || args.length < 1) {
            System.out.println("usage: add-switch <NAME>");
            return;
        }
        String name = args[0];
        Graph.addSwitch(name);
        System.out.println("% added switch " + name);
    }
    public String getDescription() { return "Add a switch node"; }
}
