import java.util.*;

public class Graph {
    // Struktur data topologi — modular, no generics (gunakan raw types)
    public static List switches;      // List<String>
    public static List links;         // List<Edge>
    public static Map priorities;     // Map<String, Integer> (switch -> priority)

    static {
        switches = new ArrayList();
        links = new ArrayList();
        priorities = new HashMap();
    }

    // ---- Switch Management ----
    public static void addSwitch(String sw) {
        if (!switches.contains(sw)) {
            switches.add(sw);
            priorities.put(sw, new Integer(32768)); // default STP priority
        }
    }

    public static boolean hasSwitch(String sw) {
        return switches.contains(sw);
    }

    // Ekstrak switch dari "S1:1" -> "S1"
    public static String getSwitchFromPort(String port) {
        int i = port.indexOf(':');
        if (i == -1) return port;
        return port.substring(0, i);
    }

    public static boolean isPortFormat(String s) {
        return s.indexOf(':') != -1;
    }

    // ---- Link Management ----
    public static void addLink(String port1, String port2, int cost) {
        // Otomatis tambahkan switch jika belum ada
        String sw1 = getSwitchFromPort(port1);
        String sw2 = getSwitchFromPort(port2);
        addSwitch(sw1);
        addSwitch(sw2);

        // Pastikan port valid (ada ':')
        if (!isPortFormat(port1) || !isPortFormat(port2)) {
            return; // atau error
        }

        links.add(new Edge(port1, port2, cost));
    }

    public static boolean removeLink(String port1, String port2) {
        for (int i = 0; i < links.size(); i++) {
            Edge e = (Edge) links.get(i);
            if (e.connects(port1, port2)) {
                links.remove(i);
                return true;
            }
        }
        return false;
    }

    public static void failLink(String port1, String port2) {
        for (int i = 0; i < links.size(); i++) {
            Edge e = (Edge) links.get(i);
            if (e.connects(port1, port2)) {
                e.up = false;
                return;
            }
        }
    }

    public static void recoverLink(String port1, String port2) {
        for (int i = 0; i < links.size(); i++) {
            Edge e = (Edge) links.get(i);
            if (e.connects(port1, port2)) {
                e.up = true;
                return;
            }
        }
    }

    // ---- Utility untuk CLI ----
    public static List getLinks() {
        return links;
    }

    public static List getSwitches() {
        return switches;
    }

    public static int getPriority(String sw) {
        Integer p = (Integer) priorities.get(sw);
        return p == null ? 32768 : p.intValue();
    }

    public static void setPriority(String sw, int prio) {
        priorities.put(sw, new Integer(prio));
    }

    // Kumpulkan semua port yang digunakan (untuk show topology)
    public static String[] getPortsOf(String sw) {
        List ports = new ArrayList();
        for (int i = 0; i < links.size(); i++) {
            Edge e = (Edge) links.get(i);
            if (e.portA.startsWith(sw + ":")) {
                ports.add(e.portA);
            }
            if (e.portB.startsWith(sw + ":")) {
                ports.add(e.portB);
            }
        }
        // Remove duplikat
        Set unique = new HashSet();
        unique.addAll(ports);
        return (String[]) unique.toArray(new String[0]);
    }

    // Untuk `show topology`
    public static void displayTopology() {
        // Switches
        System.out.println("Switches:");
        for (int i = 0; i < switches.size(); i++) {
            String sw = (String) switches.get(i);
            String[] ports = getPortsOf(sw);
            System.out.print("  " + sw + " prio=" + getPriority(sw) + " ports=[");
            for (int j = 0; j < ports.length; j++) {
                if (j > 0) System.out.print(", ");
                // Asumsi semua FWD untuk demo (nanti STP engine atur state)
                System.out.print(ports[j].substring(sw.length() + 1) + "(FWD)");
            }
            System.out.println("]");
        }

        // Links
        System.out.println("Links:");
        for (int i = 0; i < links.size(); i++) {
            Edge e = (Edge) links.get(i);
            System.out.println("  " + e.portA + " <-> " + e.portB +
                              " cost=" + e.cost + " up=" + (e.up ? 1 : 0));
        }
    }
}