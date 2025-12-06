package CLI;
import Commands.General.*;
import Commands.Switches.*;

public class CLI_Registry {

    // Maksimal 64 command
    private static final int MAX_CMD = 64;

    private static String[] cmd_name  = new String[MAX_CMD];
    private static CLI_Command[] cmd_exec = new CLI_Command[MAX_CMD];
    private static int cmd_count = 0;

    public static void init() {
        cmd_count = 0;
        register("help", new CMD_Help());
        register("show", new CMD_Show());
        register("ping", new CMD_Ping());
        register("add-switch",   new CMD_AddSwitch());
        register("set-priority", new CMD_SetPriority());
        register("add-link",     new CMD_AddLink());
        register("del-link",     new CMD_DelLink());
        register("fail-link",    new CMD_FailLink());
        register("recover-link", new CMD_RecoverLink());

        // tambah sesuai keperluan
    }

    private static void register(String name, CLI_Command exec) {
        if (cmd_count >= MAX_CMD) {
            System.out.println("% registry full");
            return;
        }
        cmd_name[cmd_count]  = name;
        cmd_exec[cmd_count]  = exec;
        cmd_count++;
    }

    private static int find_command(String key) {
        // linear search sederhana
        for (int i = 0; i < cmd_count; i++) {
            if (str_equal(cmd_name[i], key)) return i;
        }
        return -1;
    }

    /**
     * Dispatcher command.
     * Parsing manual dilakukan via scanning whitespace.
     */
    public void dispatch(String line) {
        if (line == null) return;
        line = trim(line);
        if (line.length() == 0) return;

        // parse keyword (kata pertama)
        String keyword = read_word(line, 0);

        if (str_equal(keyword, "exit") || str_equal(keyword, "quit")) {
            System.out.println("Bye!");
            System.exit(0);
        }

        int idx = find_command(keyword);
        if (idx < 0) {
            System.out.println("Unknown command: " + keyword);
            return;
        }

        // ambil argumen-argumen selanjutnya
        int p = skip_word(line, 0);
        String[] args = parse_args(line, p);

        // eksekusi
        cmd_exec[idx].execute(args);
    }

    private static boolean str_equal(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);
    }

    private static String trim(String s) {
        int i = 0, j = s.length() - 1;
        while (i <= j && (s.charAt(i) == ' ' || s.charAt(i) == '\t')) i++;
        while (j >= i && (s.charAt(j) == ' ' || s.charAt(j) == '\t')) j--;
        if (i > j) return "";
        return s.substring(i, j + 1);
    }

    // baca kata pertama
    private static String read_word(String s, int pos) {
        int n = s.length();
        // skip whitespace
        while (pos < n && is_ws(s.charAt(pos))) pos++;
        int start = pos;
        while (pos < n && !is_ws(s.charAt(pos))) pos++;
        return s.substring(start, pos);
    }

    // lompat kata pertama → return posisi setelah kata
    private static int skip_word(String s, int pos) {
        int n = s.length();
        // skip space
        while (pos < n && is_ws(s.charAt(pos))) pos++;
        // skip characters
        while (pos < n && !is_ws(s.charAt(pos))) pos++;
        // skip trailing spaces
        while (pos < n && is_ws(s.charAt(pos))) pos++;
        return pos;
    }

    // parse seluruh argumen setelah posisi tertentu
    private static String[] parse_args(String s, int pos) {
        int n = s.length();
        // Hitung jumlah argumen dulu
        int count = 0;
        int p = pos;

        while (p < n) {
            // skip ws
            while (p < n && is_ws(s.charAt(p))) p++;
            if (p >= n) break;

            // word
            int start = p;
            while (p < n && !is_ws(s.charAt(p))) p++;
            count++;
        }

        // isi array realtime
        String[] args = new String[count];
        p = pos;
        int idx = 0;

        while (p < n) {
            while (p < n && is_ws(s.charAt(p))) p++;
            if (p >= n) break;

            int start = p;
            while (p < n && !is_ws(s.charAt(p))) p++;

            args[idx++] = s.substring(start, p);
        }

        return args;
    }

    private static boolean is_ws(char c) {
        return (c == ' ' || c == '\t');
    }

    public static int getCount() {
        return cmd_count;
    }

    public static String[] getNames() {
        return cmd_name;
    }

    public static CLI_Command[] getExecs() {
        return cmd_exec;
    }

}
