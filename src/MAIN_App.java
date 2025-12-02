public class MAIN_App {
    public static void main(String[] args) {
        System.out.println("Booting NetTracer IOS...");
        System.out.println("System Ready.");
        System.out.println();

        CLI_Shell shell = new CLI_Shell();
        shell.run();
    } 
}
