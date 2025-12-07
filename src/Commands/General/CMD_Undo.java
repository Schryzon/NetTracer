package Commands.General;

import CLI.CLI_Command;
import CLI.CLI_Registry;
import CLI.HistoryManager;

public class CMD_Undo implements CLI_Command{
    public void execute(String[] args) {
        HistoryManager.undo(new CLI_Registry());
    }
    
    public String getDescription() {
        return "Undo last action";
    }
}
