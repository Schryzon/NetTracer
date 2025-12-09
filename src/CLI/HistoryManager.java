package CLI;

public class HistoryManager {
    public static Stack cmdHistory = new Stack();
    public static Stack undoStack = new Stack();
    public static Stack redoStack = new Stack();
    public static boolean isPerformingUndoRedo = false;

    public static void showLog() {
        System.out.println("Command History: ");
        cmdHistory.display();
    }

    public static void logCommand(String input) {
        cmdHistory.push(input);
    }

    public static void registerAction(String originalCmd, String inverseCmd) {
        if(isPerformingUndoRedo) return;
        // Simpan satu paket: inverse#original
        undoStack.push(inverseCmd + "#" + originalCmd);
        redoStack.clear(); // Setiap aksi baru, redoStack harus dikosongkan
    }

    public static void undo(CLI_Registry registry) {
        if(undoStack.isEmpty()) {
            System.out.println("% Nothing to undo");
            return;
        }
        String packet = undoStack.pop();
        String[] parts = packet.split("#", 2);
        String inverse = parts[0];
        String original = parts[1];
        System.out.println("UNDO> " + inverse);
        isPerformingUndoRedo = true;
        registry.dispatch(inverse);
        isPerformingUndoRedo = false;
        redoStack.push(packet);
    }

    public static void redo(CLI_Registry registry) {
        if(redoStack.isEmpty()) {
            System.out.println("% Nothing to redo");
            return;
        }
        String packet = redoStack.pop();
        String[] parts = packet.split("#", 2);
        String inverse = parts[0];
        String original = parts[1];
        System.out.println("REDO> " + original);
        isPerformingUndoRedo = true;
        registry.dispatch(original);
        isPerformingUndoRedo = false;
        undoStack.push(packet);
    }
}
