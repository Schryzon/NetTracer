package Commands.Switches;

import CLI.CLI_Command;
import Topology.Graph;
import CLI.HistoryManager;

public class CMD_RenameSwitch implements CLI_Command {
    public void execute(String[] args) {
        if (args == null || args.length < 2) {
            System.out.println("usage: rename-switch <OLD_NAME> <NEW_NAME>");
            return;
        }
        String oldName = args[0];
        String newName = args[1];
        boolean ok = renameSwitch(oldName, newName);
        if (ok) {
            System.out.println("% switch " + oldName + " renamed to " + newName);
            // Daftarkan aksi untuk undo/redo
            String original = "rename-switch " + oldName + " " + newName;
            String inverse = "rename-switch " + newName + " " + oldName;
            HistoryManager.registerAction(original, inverse);
        } else {
            System.out.println("% failed to rename switch");
        }
    }
    public String getDescription() { return "Rename a switch node"; }

    // Rename switch di Graph dan update semua link terkait
    private boolean renameSwitch(String oldName, String newName) {
        int idx = Graph.idxSwitch(oldName);
        if (idx < 0 || Graph.idxSwitch(newName) >= 0) return false;
        Graph.swName[idx] = newName;
        // Update semua link yang terhubung ke switch lama
        for (int i = 0; i < Graph.linkCount; i++) {
            if (Graph.linkA[i] != null && Graph.linkA[i].startsWith(oldName + ":")) {
                Graph.linkA[i] = newName + Graph.linkA[i].substring(oldName.length());
            }
            if (Graph.linkB[i] != null && Graph.linkB[i].startsWith(oldName + ":")) {
                Graph.linkB[i] = newName + Graph.linkB[i].substring(oldName.length());
            }
        }
        return true;
    }
}
