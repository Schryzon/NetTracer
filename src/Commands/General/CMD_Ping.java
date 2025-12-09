package Commands.General;

import CLI.CLI_Command;
import Engine.*;

public class CMD_Ping implements CLI_Command {
    public void execute(String[] args) {
        if (args == null || args.length < 2) {
            System.out.println("usage: ping <SRC_SWITCH> <DST_SWITCH> [count] [ttl]");
            return;
        }
        String src = args[0];
        String dst = args[1];
        int count = (args.length >= 3) ? parse(args[2]) : 4;
        int ttl   = (args.length >= 4) ? parse(args[3]) : 64;

        for (int i=0;i<count;i++){
            NetEngine.sendPing(src, dst, i+1, ttl);
            // jalankan beberapa tick di antara ping (interval 1 tick)
            NetEngine.tick(); // interval
        }

        // drain event sampai kosong (atau batasi ticks)
        int guard = 10000;
        while (!NetEngine.q.isEmpty() && guard-- > 0){
            NetEngine.tick();
        }
    }
    public int parse(String s){ try { return Integer.parseInt(s); } catch(Exception e){ return 0; } }
    public String getDescription() { return "Send ICMP-like echo from SRC to DST"; }
}
