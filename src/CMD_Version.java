public class CMD_Version implements CLI_Command {
    public void execute(String[] args) {
        System.out.println("NetTracer IOS Version 1.1");
    }

    public String getDescription() {
        return "Show system version";
    }
}