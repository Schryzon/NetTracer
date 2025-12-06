package Commands.General;

import CLI.CLI_Command;
import Topology.*;
import Utils.AsciiTopo;     // kalau mau ascii topology

public class CMD_Show implements CLI_Command {

    public void execute(String[] args) {
        if (args == null || args.length == 0) {
            System.out.println("Incomplete command. Try 'show version'");
            return;
        }

        String sub = args[0];

        if (equals(sub, "version")) {
            System.out.println("NetTracer IOS 1.1");
            return;
        }

        if (equals(sub, "topology")) {
            Graph.displayTopology();   // langsung panggil modul Graph procedural
            return;
        }

        if (equals(sub, "ascii")) {
            if (args.length == 1) {
                AsciiTopo.show_normal();
                return;
            }
            if (args.length == 2 && equals(args[1], "mst")) {
                AsciiTopo.show_mst();
                return;
            }
            if (args.length == 4 && equals(args[1], "path")) {
                AsciiTopo.show_path(args[2], args[3]);
                return;
            }
            System.out.println("Usage:");
            System.out.println("  show ascii");
            System.out.println("  show ascii mst");
            System.out.println("  show ascii path <A> <B>");
            return;
        }

        if (equals(sub, "switches")) {
            show_switches();
            return;
        }

        if (equals(sub, "links")) {
            show_links();
            return;
        }

        if (equals(sub, "history")) {
            System.out.println("[history not implemented yet]");
            return;
        }

        System.out.println("Unknown show option: " + sub);
    }

    private void show_switches() {
        int n = Graph.getSwitchCount();
        System.out.println("Switches: " + n);
        for (int i = 0; i < n; i++) {
            String sw = Graph.getSwitchAt(i);
            System.out.println("  " + sw + " prio=" + Graph.swPrio[i]);
        }
    }

    private void show_links() {
        int m = Graph.getLinkCount();
        System.out.println("Links: " + m);
        for (int i = 0; i < m; i++) {
            String a = Graph.getLinkAAt(i);
            String b = Graph.getLinkBAt(i);
            int c = Graph.getLinkCostAt(i);
            int up = Graph.getLinkUpAt(i);
            System.out.println("  " + a + " <-> " + b + " cost=" + c + " up=" + up);
        }
    }

    // simple equality (no util)
    private boolean equals(String a, String b) {
        return a != null && a.equals(b);
    }

    public String getDescription() {
        return "Show system, switches, links, topology.";
    }
}
