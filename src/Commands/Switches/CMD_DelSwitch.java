package Commands.Switches;

import CLI.CLI_Command;
import Topology.Graph;

public class CMD_DelSwitch implements CLI_Command {
    public void execute(String[] args) {
        if (args == null || args.length < 1) {
            System.out.println("usage: del-switch <NAME>");
            return;
        }
        String name = args[0];
        boolean ok = removeSwitch(name);
        if (ok) {
            System.out.println("% deleted switch " + name);
        } else {
            System.out.println("% switch not found or failed to delete");
        }
    }
    public String getDescription() { return "Delete a switch node"; }

    // Hapus switch dari Graph
    private boolean removeSwitch(String sw) {
        int idx = Graph.idxSwitch(sw);
        if (idx < 0) return false;
        // Hapus semua link yang terhubung ke switch ini
        if (Graph.swLinkIdx[idx] != null) {
            Topology.LinkedList list = Graph.swLinkIdx[idx];
            Topology.LLNode cur = list.getHead();
            while (cur != null) {
                int linkIdx = cur.data;
                String portA = Graph.linkA[linkIdx];
                String portB = Graph.linkB[linkIdx];
                Graph.removeLink(portA, portB);
                cur = cur.next;
            }
        }
        // Geser array swName, swPrio, swLinkIdx
        for (int i = idx; i < Graph.swCount - 1; i++) {
            Graph.swName[i] = Graph.swName[i+1];
            Graph.swPrio[i] = Graph.swPrio[i+1];
            Graph.swLinkIdx[i] = Graph.swLinkIdx[i+1];
        }
        Graph.swName[Graph.swCount-1] = null;
        Graph.swPrio[Graph.swCount-1] = 32768;
        Graph.swLinkIdx[Graph.swCount-1] = null;
        Graph.swCount--;
        return true;
    }
}
