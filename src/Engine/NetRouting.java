package Engine;

import Topology.Graph;

public class NetRouting {


    /** Return switch index by name, or -1 if not found. */
    static int switchIndex(String name) {
        for (int i = 0; i < Graph.swCount; i++) {
            if (Graph.swName[i] != null && Graph.swName[i].equals(name)) return i;
        }
        return -1;
    }

    /** True if link at index e is administratively/physically up. */
    static boolean linkActive(int e) {
        return Graph.linkUp[e] == 1;
    }

    /** Get switch index at endpoint A of link e. */
    static int endpointA(int e) {
        String sw = Graph.getSwitchFromPort(Graph.linkA[e]);
        return switchIndex(sw);
    }

    /** Get switch index at endpoint B of link e. */
    static int endpointB(int e) {
        String sw = Graph.getSwitchFromPort(Graph.linkB[e]);
        return switchIndex(sw);
    }

    /**
     * BFS path from A to B in switch-space, returned as CSV: "S1,S2,S3".
     * Empty string means no path.
     */
    public static String bfsPathCsv(String A, String B) {
        int nSwitches = Graph.swCount;
        int src = switchIndex(A), dst = switchIndex(B);
        if (src < 0 || dst < 0) return "";
        if (src == dst) return A;

        int[] visited = new int[nSwitches];
        int[] parent  = new int[nSwitches];
        int[] queue   = new int[nSwitches];
        int head = 0, tail = 0;

        for (int i = 0; i < nSwitches; i++) { visited[i] = 0; parent[i] = -1; }

        queue[tail++] = src;
        visited[src] = 1;

        // BFS over active links
        while (head < tail) {
            int u = queue[head++];
            if (u == dst) break;

            for (int e = 0; e < Graph.linkCount; e++) {
                if (!linkActive(e)) continue;
                int a = endpointA(e), b = endpointB(e);
                if (a == u && visited[b] == 0) { visited[b] = 1; parent[b] = a; queue[tail++] = b; }
                if (b == u && visited[a] == 0) { visited[a] = 1; parent[a] = b; queue[tail++] = a; }
            }
        }

        if (visited[dst] == 0) return "";

        // Reconstruct dst -> src into rev[], then emit CSV src..dst
        int[] rev = new int[nSwitches];
        int rlen = 0, cur = dst;
        while (cur != -1 && cur != src) { rev[rlen++] = cur; cur = parent[cur]; }
        rev[rlen++] = src;

        String csv = "";
        for (int i = rlen - 1; i >= 0; i--) {
            csv += Graph.swName[rev[i]];
            if (i != 0) csv += ",";
        }
        return csv;
    }

    /** Return item at index from a CSV like "A,B,C"; empty if out of range. */
    public static String csvAt(String csv, int idx) {
        int n = csv.length();
        int field = 0, start = 0;
        for (int i = 0; i <= n; i++) {
            if (i == n || csv.charAt(i) == ',') {
                if (field == idx) return csv.substring(start, i);
                field++;
                start = i + 1;
            }
        }
        return "";
    }

    /** Count nodes in CSV like "A,B,C". */
    public static int csvCount(String csv) {
        if (csv == null || csv.length() == 0) return 0;
        int count = 1;
        for (int i = 0; i < csv.length(); i++) if (csv.charAt(i) == ',') count++;
        return count;
    }
}
