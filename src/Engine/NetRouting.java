// NetRouting.java — BFS hops di atas Topology.Graph (tanpa util)
package Engine;

import Topology.Graph;

public class NetRouting {

    // cari indeks switch berdasarkan nama
    static int idxSw(String name){
        int i;
        for (i=0;i<Graph.swCount;i++){
            if (Graph.swName[i]!=null && Graph.swName[i].equals(name)) return i;
        }
        return -1;
    }

    // BFS: path switch-name dari A ke B. Return CSV: "S1,S2,S3"
    // Jika tidak ada path → return "".
    public static String bfsPathCsv(String A, String B){
        int n = Graph.swCount;
        int s = idxSw(A), t = idxSw(B);
        if (s<0 || t<0) return "";
        if (s==t) return A;

        int[] vis = new int[n];
        int[] par = new int[n];
        int[] q = new int[n];
        int qh=0, qt=0, i;

        for (i=0;i<n;i++){ vis[i]=0; par[i]=-1; }

        q[qt++] = s; vis[s]=1;

        while (qh<qt){
            int u = q[qh++];
            if (u==t) break;
            // scan neighbors dari link aktif
            for (i=0;i<Graph.linkCount;i++){
                if (Graph.linkUp[i]==0) continue;
                int a = idxSw(Graph.getSwitchFromPort(Graph.linkA[i]));
                int b = idxSw(Graph.getSwitchFromPort(Graph.linkB[i]));
                if (a==u && vis[b]==0){ vis[b]=1; par[b]=a; q[qt++]=b; }
                if (b==u && vis[a]==0){ vis[a]=1; par[a]=b; q[qt++]=a; }
            }
        }

        if (vis[t]==0) return "";

        // reconstruct t->s
        int[] rev = new int[n];
        int rc=0, cur=t;
        while (cur!=-1 && cur!=s){ rev[rc++]=cur; cur=par[cur]; }
        rev[rc++] = s;

        // build CSV s..t
        String out = "";
        for (i=rc-1;i>=0;i--){
            out += Graph.swName[rev[i]];
            if (i!=0) out += ",";
        }
        return out;
    }

    // Ambil elemen ke-idx dari CSV path; idx aman
    public static String csvAt(String csv, int idx){
        int n = csv.length();
        int k = 0; int start=0; int i;
        for (i=0;i<=n;i++){
            if (i==n || csv.charAt(i)==','){
                if (k==idx) return csv.substring(start,i);
                k++; start=i+1;
            }
        }
        return "";
    }

    // Hitung jumlah node di CSV
    public static int csvCount(String csv){
        if (csv==null || csv.length()==0) return 0;
        int c=1, i;
        for (i=0;i<csv.length();i++) if (csv.charAt(i)==',') c++;
        return c;
    }
}
