package Commands.Switches;

import CLI.CLI_Command;
import Topology.Graph;

public class CMD_DelLink implements CLI_Command {
    public void execute(String[] args) {
        if (args == null || args.length < 2) {
            System.out.println("usage: del-link <S1:PORT> <S2:PORT>");
            return;
        }
        String p1 = args[0], p2 = args[1];
        boolean ok = Graph.removeLink(p1, p2);
        if (ok) System.out.println("% removed link " + p1 + " <-> " + p2);
        else    System.out.println("% no such link");
    }
    public String getDescription() { return "Delete a link"; }
}
