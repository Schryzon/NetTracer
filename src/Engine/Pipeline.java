package Engine;

import java.util.Scanner;

public class Pipeline {
    public String runCommand (String cmd){
        if (cmd.equals ("show ports")){
            return " ";
        }
        return "unknown command: " + cmd;
    }

    public String runCommandWithInput(String cmd, String input) {
        if (cmd.startsWith("grep ")) {
            String keyword = cmd.substring(5);
            String result = "";
            Scanner sc = new Scanner(input);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.contains(keyword)) {
                    result += line + "\n";
                }
            }
            sc.close();
            return result;
        }
        return "Unknown command: " + cmd;
    }
}