package Topology;
/*  Graph — switches disimpan di array paralel (nama + priority)
    - links disimpan di array paralel (portA, portB, cost, up)
    - indeks link per switch disimpan dalam LinkedList (int)
*/

public class Graph {

    public static final int MAX_SWITCH = 256;
    public static final int MAX_LINK   = 4096;

    public static int      swCount = 0;
    public static String[] swName  = new String[MAX_SWITCH];
    public static int[]    swPrio  = new int[MAX_SWITCH];        // default 32768
    public static LinkedList[] swLinkIdx = new LinkedList[MAX_SWITCH];

    // LINK TABLE
    public static int      linkCount = 0;
    public static String[] linkA  = new String[MAX_LINK];         // "S1:1"
    public static String[] linkB  = new String[MAX_LINK];         // "S2:3"
    public static int[]    linkCost = new int[MAX_LINK];
    public static int[]    linkUp   = new int[MAX_LINK];          // 1 up, 0 down

    public static void init() {
        swCount = 0; linkCount = 0;
        int i;
        for (i = 0; i < MAX_SWITCH; i++) {
            swName[i] = null;
            swPrio[i] = 32768;
            swLinkIdx[i] = null;
        }
        for (i = 0; i < MAX_LINK; i++) {
            linkA[i] = null; linkB[i] = null;
            linkCost[i] = 0; linkUp[i] = 0;
        }
    }

    public static void addSwitch(String sw) {
        int idx = idxSwitch(sw);
        if (idx >= 0) return;
        if (swCount >= MAX_SWITCH) {
            println("% switch table full");
            return;
        }
        swName[swCount] = sw;
        swPrio[swCount] = 32768;
        swLinkIdx[swCount] = new LinkedList(); // daftar indeks link yg menempel ke switch ini
        swCount++;
    }

    public static boolean hasSwitch(String sw) {
        return idxSwitch(sw) >= 0;
    }

    public static int getPriority(String sw) {
        int i = idxSwitch(sw);
        if (i < 0) return 32768;
        return swPrio[i];
    }

    public static void setPriority(String sw, int prio) {
        int i = idxSwitch(sw);
        if (i < 0) {
            addSwitch(sw);
            i = idxSwitch(sw);
        }
        swPrio[i] = prio;
    }

    public static int idxSwitch(String sw) {
        int i;
        for (i = 0; i < swCount; i++) {
            if (swName[i] != null && swName[i].equals(sw)) return i;
        }
        return -1;
    }

    // "S1:1" -> "S1"
    public static String getSwitchFromPort(String port) {
        int k = indexOf(port, ':');
        if (k < 0) return port;
        return port.substring(0, k);
    }

    public static boolean isPortFormat(String s) {
        return indexOf(s, ':') >= 0;
    }

    // ====== LINK MGMT ======
    public static void addLink(String port1, String port2, int cost) {
        if (!isPortFormat(port1) || !isPortFormat(port2)) {
            println("% bad port format, expected S:if");
            return;
        }
        String sw1 = getSwitchFromPort(port1);
        String sw2 = getSwitchFromPort(port2);
        addSwitch(sw1);
        addSwitch(sw2);

        if (linkCount >= MAX_LINK) {
            println("% link table full");
            return;
        }
        // avoid duplicate (unordered)
        int dup = findLinkIdx(port1, port2);
        if (dup >= 0) {
            println("% link exists");
            return;
        }

        linkA[linkCount] = port1;
        linkB[linkCount] = port2;
        linkCost[linkCount] = cost;
        linkUp[linkCount] = 1;

        // catat indeks link di daftar milik masing-masing switch
        int i1 = idxSwitch(sw1);
        int i2 = idxSwitch(sw2);
        if (swLinkIdx[i1] == null) swLinkIdx[i1] = new LinkedList();
        if (swLinkIdx[i2] == null) swLinkIdx[i2] = new LinkedList();
        swLinkIdx[i1].add(linkCount);
        swLinkIdx[i2].add(linkCount);

        linkCount++;
    }

    public static boolean removeLink(String port1, String port2) {
        int i = findLinkIdx(port1, port2);
        if (i < 0) return false;

        // hapus referensi indeks link i dari kedua switch terkait
        String sw1 = getSwitchFromPort(linkA[i]);
        String sw2 = getSwitchFromPort(linkB[i]);
        int s1 = idxSwitch(sw1);
        int s2 = idxSwitch(sw2);
        if (s1 >= 0 && swLinkIdx[s1] != null) swLinkIdx[s1].remove(i);
        if (s2 >= 0 && swLinkIdx[s2] != null) swLinkIdx[s2].remove(i);

        // compaction sederhana: pindahkan elemen terakhir ke i
        int last = linkCount - 1;
        if (i != last) {
            // sebelum overwrite, update daftar linked list switch yg menampung "last"
            String lastSwA = getSwitchFromPort(linkA[last]);
            String lastSwB = getSwitchFromPort(linkB[last]);
            int ls1 = idxSwitch(lastSwA);
            int ls2 = idxSwitch(lastSwB);
            // ganti semua node "last" -> "i" di kedua linked list
            if (ls1 >= 0 && swLinkIdx[ls1] != null) replaceAll(swLinkIdx[ls1], last, i);
            if (ls2 >= 0 && swLinkIdx[ls2] != null) replaceAll(swLinkIdx[ls2], last, i);

            linkA[i] = linkA[last];
            linkB[i] = linkB[last];
            linkCost[i] = linkCost[last];
            linkUp[i]   = linkUp[last];
        }
        // clear tail
        linkA[last] = null; linkB[last] = null;
        linkCost[last] = 0; linkUp[last] = 0;
        linkCount--;
        return true;
    }

    public static void failLink(String port1, String port2) {
        int i = findLinkIdx(port1, port2);
        if (i >= 0) linkUp[i] = 0;
    }

    public static void recoverLink(String port1, String port2) {
        int i = findLinkIdx(port1, port2);
        if (i >= 0) linkUp[i] = 1;
    }

    public static int findLinkIdx(String p1, String p2) {
        int i;
        for (i = 0; i < linkCount; i++) {
            if (equalsUnordered(linkA[i], linkB[i], p1, p2)) return i;
        }
        return -1;
    }

    public static int getSwitchCount(){ return swCount; }
    public static String getSwitchAt(int i){ return swName[i]; }

    public static int getLinkCount(){ return linkCount; }
    public static String getLinkAAt(int i){ return linkA[i]; }
    public static String getLinkBAt(int i){ return linkB[i]; }
    public static int getLinkCostAt(int i){ return linkCost[i]; }
    public static int getLinkUpAt(int i){ return linkUp[i]; }

    // kumpulkan semua port milik switch: hasil array baru ukuran tepat
    public static String[] getPortsOf(String sw) {
        // hitung dulu
        int count = 0;
        int i;
        for (i = 0; i < linkCount; i++) {
            if (startsWith(linkA[i], sw) && charAt(linkA[i], sw.length()) == ':') count++;
            if (startsWith(linkB[i], sw) && charAt(linkB[i], sw.length()) == ':') count++;
        }
        // tampung sementara + unique
        String[] temp = new String[count];
        int t = 0;
        for (i = 0; i < linkCount; i++) {
            if (startsWith(linkA[i], sw) && charAt(linkA[i], sw.length()) == ':') {
                if (!containsStr(temp, t, linkA[i])) temp[t++] = linkA[i];
            }
            if (startsWith(linkB[i], sw) && charAt(linkB[i], sw.length()) == ':') {
                if (!containsStr(temp, t, linkB[i])) temp[t++] = linkB[i];
            }
        }
        // trim
        String[] out = new String[t];
        for (i = 0; i < t; i++) out[i] = temp[i];
        return out;
    }

    public static void displayTopology() {
        int i, j;
        println("Switches:");
        for (i = 0; i < swCount; i++) {
            String sw = swName[i];
            String[] ports = getPortsOf(sw);
            print("  " + sw + " prio=" + swPrio[i] + " ports=[");
            for (j = 0; j < ports.length; j++) {
                if (j > 0) print(", ");
                // tampilkan nomor setelah "SW:"
                int start = length(sw) + 1;
                String no = ports[j].substring(start);
                print(no + "(FWD)");
            }
            println("]");
        }
        println("Links:");
        for (i = 0; i < linkCount; i++) {
            println("  " + linkA[i] + " <-> " + linkB[i] +
                    " cost=" + linkCost[i] + " up=" + (linkUp[i] == 1 ? 1 : 0));
        }
    }

    private static void print(String s){ System.out.print(s); }
    private static void println(String s){ System.out.println(s); }
    private static int indexOf(String s, char c){
        int n = s.length();
        int i;
        for (i = 0; i < n; i++) if (s.charAt(i) == c) return i;
        return -1;
    }
    private static int length(String s){ return s.length(); }
    private static char charAt(String s, int i){ return s.charAt(i); }
    private static boolean startsWith(String s, String prefix){
        int n = s.length(), m = prefix.length();
        if (m > n) return false;
        int i;
        for (i = 0; i < m; i++) if (s.charAt(i) != prefix.charAt(i)) return false;
        return true;
    }
    private static boolean containsStr(String[] arr, int len, String val){
        int i;
        for (i = 0; i < len; i++){
            if (arr[i] != null && arr[i].equals(val)) return true;
        }
        return false;
    }
    private static boolean equalsUnordered(String a1, String b1, String a2, String b2){
        if (a1 == null || b1 == null || a2 == null || b2 == null) return false;
        if (a1.equals(a2) && b1.equals(b2)) return true;
        if (a1.equals(b2) && b1.equals(a2)) return true;
        return false;
    }

    // ganti semua nilai oldVal menjadi newVal pada LinkedList indeks link
    private static void replaceAll(LinkedList list, int oldVal, int newVal){
        LLNode cur = list.getHead();
        while (cur != null) {
            if (cur.data == oldVal) cur.data = newVal;
            cur = cur.next;
        }
    }
}
