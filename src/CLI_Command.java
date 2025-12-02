public interface CLI_Command {
    void execute(String[] args);
    
    String getDescription();
}