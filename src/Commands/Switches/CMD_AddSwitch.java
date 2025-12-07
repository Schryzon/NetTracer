package Commands.Switches;

import CLI.CLI_Command;
import Topology.Graph;
import CLI.HistoryManager;

public class CMD_AddSwitch implements CLI_Command {
    public void execute(String[] args) {
        if (args == null || args.length < 1) {
            System.out.println("usage: add-switch <NAME>");
            return;
        }
        String name = args[0];
        Graph.addSwitch(name);
        System.out.println("% added switch " + name);
        // Daftarkan aksi untuk undo/redo
        String original = "add-switch " + name;
        String inverse = "del-switch " + name;
        HistoryManager.registerAction(original, inverse);
    }
    public String getDescription() { return "Add a switch node"; }
}
