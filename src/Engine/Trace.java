package Engine;

import java.io.FileWriter;
import java.io.IOException;

public class Trace {
    public void exportTrace(String filename, String trace) {
        try {
            FileWriter fw = new FileWriter(filename);
            fw.write(trace);
            fw.close();
        } catch (IOException e) {
            System.out.println("Error exporting trace: " + e.getMessage());
        }
    }
}

//redirection > dan >> 
class Redirection {
    public void writeToFile(String filename, String content, boolean append) {
        try {
            FileWriter fw = new FileWriter(filename, append);
            fw.write(content);
            fw.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void handleRedirection(String commandOutput, String redirectCommand){
        if (redirectCommand.startsWith(">")){
            String filename = redirectCommand.substring (1).trim();
            writeToFile(filename, commandOutput, false); // overwrite
        } else if (redirectCommand.startsWith(">>")) {
            String filename = redirectCommand.substring(2).trim();
            writeToFile(filename, commandOutput, true); // append
        }
    }
}