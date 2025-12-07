package CLI;

public class HistoryManager {
    private static Stack cmdHistory = new Stack();
    private static Stack undoStack = new Stack();
    private static Stack redoStack = new Stack();
    private static boolean isPerformingUndoRedo = false;

    public static void showLog() {
        System.out.println("Command History: ");
        cmdHistory.display();
    }

    public static void logCommand(String input) {
        cmdHistory.push(input);
    }

    public static void registerAction(String originalCmd, String inverseCmd) {
        if(isPerformingUndoRedo) return;

        undoStack.push(inverseCmd);
        undoStack.push(inverseCmd + "#" + originalCmd);
        redoStack.clear();
    }

    public static void undo(CLI_Registry registry) {
        if(undoStack.isEmpty()) {
            System.out.println("% Nothing to undo");
            return;
        }

        String packet = undoStack.pop();
        String[] parts = packet.split("#");
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
        String[] parts = packet.split("#");
        String original = parts[1];

        System.out.println("REDO> " + original);

        isPerformingUndoRedo = true;
        registry.dispatch(original);
        isPerformingUndoRedo = false;

        undoStack.push(packet);
    }
}
