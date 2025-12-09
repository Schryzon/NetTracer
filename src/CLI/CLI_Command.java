package CLI;

// Base blueprint for CLI commands
public interface CLI_Command {
    void execute(String[] args);
    
    String getDescription();
}