package Commands.General;

import CLI.CLI_Command;
import CLI.CLI_Registry;
import CLI.HistoryManager;

public class CMD_Redo implements CLI_Command{
    public void execute(String[] args) {
        HistoryManager.redo(new CLI_Registry());
    }

    public String getDescription() {
        return "Redo the undone action";
    }
}
