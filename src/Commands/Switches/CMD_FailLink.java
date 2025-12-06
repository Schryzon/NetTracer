package Commands.Switches;

import CLI.CLI_Command;
import Topology.Graph;

public class CMD_FailLink implements CLI_Command {
    public void execute(String[] args) {
        if (args == null || args.length < 2) {
            System.out.println("usage: fail-link <S1:PORT> <S2:PORT>");
            return;
        }
        Graph.failLink(args[0], args[1]);
        System.out.println("% link failed");
    }
    public String getDescription() { return "Set a link administratively down"; }
}
