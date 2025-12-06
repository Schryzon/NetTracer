package Commands.Switches;

import CLI.CLI_Command;
import Topology.Graph;

public class CMD_RecoverLink implements CLI_Command {
    public void execute(String[] args) {
        if (args == null || args.length < 2) {
            System.out.println("usage: recover-link <S1:PORT> <S2:PORT>");
            return;
        }
        Graph.recoverLink(args[0], args[1]);
        System.out.println("% link recovered");
    }
    public String getDescription() { return "Bring a failed link back up"; }
}
