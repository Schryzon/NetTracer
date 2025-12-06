package Commands.Switches;

import CLI.CLI_Command;
import Topology.Graph;

public class CMD_AddLink implements CLI_Command {
    public void execute(String[] args) {
        if (args == null || args.length < 3) {
            System.out.println("usage: add-link <S1:PORT> <S2:PORT> <COST>");
            return;
        }
        String p1 = args[0];
        String p2 = args[1];
        int cost;
        try { cost = Integer.parseInt(args[2]); }
        catch (Exception e){ System.out.println("% bad cost"); return; }

        Graph.addLink(p1, p2, cost);
        System.out.println("% link " + p1 + " <-> " + p2 + " cost=" + cost);
    }
    public String getDescription() { return "Add a link between two ports"; }
}
