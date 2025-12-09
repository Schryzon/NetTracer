import CLI.CLI_Shell;

public class MAIN_App {
    static final String BANNER =
        "┏┓╻┏━╸╺┳╸╺┳╸┏━┓┏━┓┏━╸┏━╸┏━┓\n" +
        "┃┗┫┣╸  ┃  ┃ ┣┳┛┣━┫┃  ┣╸ ┣┳┛\n" +
        "╹ ╹┗━╸ ╹  ╹ ╹┗╸╹ ╹┗━╸┗━╸╹┗╸";

    static void loading(String msg, int dots, int delayMs) throws InterruptedException {
        System.out.print(msg);
        for (int i = 0; i < dots; i++) { Thread.sleep(delayMs); System.out.print("."); }
        System.out.println();
    }

    public static void main(String[] args) throws InterruptedException {
        loading("Booting NetTracer ", 6, 500);
        System.out.println("System Initialized!\n");
        Thread.sleep(250);

        System.out.println(BANNER);
        System.out.println("Cisco IOS & STP Simulation in Java");        
        System.out.println();
        System.out.println("Type \"help\" to display all available commands.");
        System.out.println();
        

        CLI_Shell shell = new CLI_Shell();
        shell.run();
    }
}
