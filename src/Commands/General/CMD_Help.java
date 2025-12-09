package Commands.General;

import CLI.CLI_Command;
import CLI.CLI_Registry;

public class CMD_Help implements CLI_Command {

    public void execute(String[] args) {
        int count = CLI_Registry.getCount();
        String[] names = CLI_Registry.getNames();
        CLI_Command[] execs = CLI_Registry.getExecs();

        System.out.println("Available commands:");

        for (int i = 0; i < count; i++) {
            String name = names[i];
            CLI_Command cmd = execs[i];

            if (name != null && cmd != null) {
                String desc = cmd.getDescription();
                if (desc == null) desc = "";
                System.out.println("  " + pad(name, 15) + " - " + desc);
            }
        }
    }

    public String pad(String s, int n) {
        int len = s.length();
        if (len >= n) return s;
        String out = s;
        for (int i = len; i < n; i++) out += " ";
        return out;
    }

    public String getDescription() {
        return "Show all available commands and descriptions";
    }
}
