public class CMD_Version implements CLI_Command {
    public void execute(String[] args) {
        System.out.println("NetTracer IOS Version 1.1 (Day 2 Build)");
        System.out.println("Created by Dev Team A-E");
    }

    public String getDescription() {
        return "Show system version";
    }
}