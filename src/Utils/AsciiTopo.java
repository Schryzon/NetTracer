package Utils;
import Topology.Graph;

/**
 * AsciiTopo — render topology ke kanvas ASCII.
 * Syarat: HANYA baca array publik di Graph (swName, linkA/B, linkCost, linkUp, swCount, linkCount).
 * Highlight mode:
 *  - NORMAL: semua link titik "."
 *  - MST: jalur MST ditandai '='
 *  - PATH: rute terpendek Sx->Sy ditandai '#'
 */
public class AsciiTopo {

    // ukuran kanvas
    static final int W = 80;
    static final int H = 24;

    static char[][] cv = new char[H][W];
    // posisi tiap switch
    static int[] nx = new int[Graph.MAX_SWITCH];
    static int[] ny = new int[Graph.MAX_SWITCH];

    // buffer bantu
    static int[] mstFlag = new int[Graph.MAX_LINK]; // 1 jika edge dipilih MST (lokal)
    static int[] pathEdge = new int[Graph.MAX_LINK]; // 1 jika edge terpakai di path (lokal)

    // ==== API ====
    public static void show_normal() {
        clear();
        layout_circle();
        draw_all_edges('.');
        draw_nodes();
        flush();
    }

    public static void show_mst() {
        clear();
        layout_circle();
        kruskal_mst_local();       // isi mstFlag[]
        draw_all_edges('.');       // semua link biasa
        draw_edges_flag(mstFlag, '='); // mst jadi '='
        draw_nodes();
        flush();
    }

    public static void show_path(String A, String B) {
        clear();
        layout_circle();
        dijkstra_path(A, B);       // set pathEdge[] sesuai rute
        draw_all_edges('.');       // semua link biasa
        draw_edges_flag(pathEdge, '#'); // path jadi '#'
        draw_nodes();
        flush();
    }

    // ==== Kanvas & util ====
    static void clear(){
        int y,x;
        for (y=0;y<H;y++){
            for (x=0;x<W;x++) cv[y][x] = ' ';
        }
    }
    static void put(int x,int y,char c){
        if (x>=0 && x<W && y>=0 && y<H) cv[y][x]=c;
    }
    static void text(int x,int y,String s){
        int i;
        for (i=0;i<s.length();i++) put(x+i,y,s.charAt(i));
    }
    static void flush(){
        int y,x;
        for (y=0;y<H;y++){
            String line="";
            for (x=0;x<W;x++) line += cv[y][x];
            System.out.println(line);
        }
    }

    // ==== Layout lingkaran ====
    static void layout_circle(){
        int n = Graph.swCount;
        double cx = W/2.0, cy = H/2.0;
        double r = Math.min(W, H)*0.38;           // radius
        int i;
        for (i=0;i<n;i++){
            double ang = (2.0*Math.PI*i)/Math.max(1,n);
            int x = (int)(cx + r*Math.cos(ang));
            int y = (int)(cy + r*Math.sin(ang));
            nx[i]=x; ny[i]=y;
        }
    }

    // ==== Gambar node & label ====
    static void draw_nodes(){
        int i;
        for (i=0;i<Graph.swCount;i++){
            put(nx[i], ny[i], 'O');
            // label (maks 5 char biar muat)
            String name = Graph.swName[i];
            String lab = (name.length()<=5)?name:name.substring(0,5);
            text(Math.max(0, nx[i]-lab.length()/2), Math.min(H-1, ny[i]+1), lab);
        }
    }

    // ==== Gambar semua edges ====
    static void draw_all_edges(char ch){
        int i;
        for (i=0;i<Graph.linkCount;i++){
            if (Graph.linkUp[i]==0) continue;
            int a = idxSwitchOfPort(Graph.linkA[i]);
            int b = idxSwitchOfPort(Graph.linkB[i]);
            if (a<0 || b<0) continue;
            draw_line(nx[a], ny[a], nx[b], ny[b], ch);
        }
    }
    static void draw_edges_flag(int[] flag, char ch){
        int i;
        for (i=0;i<Graph.linkCount;i++){
            if (Graph.linkUp[i]==0) continue;
            if (flag[i]==1){
                int a = idxSwitchOfPort(Graph.linkA[i]);
                int b = idxSwitchOfPort(Graph.linkB[i]);
                if (a<0 || b<0) continue;
                draw_line(nx[a], ny[a], nx[b], ny[b], ch);
            }
        }
    }

    // ==== Bresenham sederhana ====
    static void draw_line(int x0,int y0,int x1,int y1,char ch){
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = (x0 < x1) ? 1 : -1;
        int sy = (y0 < y1) ? 1 : -1;
        int err = dx - dy;

        while (true){
            put(x0,y0,ch);
            if (x0==x1 && y0==y1) break;
            int e2 = 2*err;
            if (e2 > -dy){ err -= dy; x0 += sx; }
            if (e2 <  dx){ err += dx; y0 += sy; }
        }
    }

    // ==== helper ====
    static int idxSwitchOfPort(String port){
        // "S1:2" → "S1"
        int k = indexOf(port, ':');
        String sw = (k<0)?port:port.substring(0,k);
        return idxSwitch(sw);
    }
    static int idxSwitch(String sw){
        int i;
        for (i=0;i<Graph.swCount;i++){
            if (Graph.swName[i]!=null && Graph.swName[i].equals(sw)) return i;
        }
        return -1;
    }
    static int indexOf(String s,char c){
        int i,n=s.length();
        for (i=0;i<n;i++) if (s.charAt(i)==c) return i;
        return -1;
    }

    static void kruskal_mst_local(){
        int i;
        for (i=0;i<Graph.linkCount;i++) mstFlag[i]=0;

        // kumpulkan edges aktif
        int m=0;
        int[] e = new int[Graph.linkCount];
        for (i=0;i<Graph.linkCount;i++){
            if (Graph.linkUp[i]==1) e[m++]=i;
        }
        // sort e[0..m) by cost (merge sort)
        mergesort_edges(e, m);

        // union-find atas switch
        int n = Graph.swCount;
        int[] p = new int[n];
        int[] r = new int[n];
        for (i=0;i<n;i++){ p[i]=i; r[i]=0; }

        for (i=0;i<m;i++){
            int li = e[i];
            int a = idxSwitchOfPort(Graph.linkA[li]);
            int b = idxSwitchOfPort(Graph.linkB[li]);
            if (a<0 || b<0) continue;
            if (uf_union(p,r,a,b)){
                mstFlag[li]=1;
            }
        }
    }
    static int uf_find(int[] p,int x){ if (p[x]==x) return x; p[x]=uf_find(p,p[x]); return p[x]; }
    static boolean uf_union(int[] p,int[] r,int a,int b){
        int pa=uf_find(p,a), pb=uf_find(p,b);
        if (pa==pb) return false;
        if (r[pa]<r[pb]) p[pa]=pb;
        else if (r[pb]<r[pa]) p[pb]=pa;
        else { p[pb]=pa; r[pa]++; }
        return true;
    }
    static void mergesort_edges(int[] idx,int len){
        if (len<=1) return;
        int[] aux = new int[len];
        ms(idx,aux,0,len);
    }
    static void ms(int[] a,int[] aux,int lo,int hi){
        if (hi-lo<=1) return;
        int mid=(lo+hi)/2;
        ms(a,aux,lo,mid); ms(a,aux,mid,hi);
        int i; for (i=lo;i<hi;i++) aux[i]=a[i];
        int p=lo,q=mid,k=lo;
        while (p<mid && q<hi){
            int ap=aux[p], aq=aux[q];
            int cp=Graph.linkCost[ap], cq=Graph.linkCost[aq];
            if (cp<=cq) a[k++]=aux[p++]; else a[k++]=aux[q++];
        }
        while (p<mid) a[k++]=aux[p++];
        while (q<hi) a[k++]=aux[q++];
    }

    static void dijkstra_path(String A, String B){
        int s = idxSwitch(A);
        int t = idxSwitch(B);
        int n = Graph.swCount;
        int i;

        // reset flags
        for (i=0;i<Graph.linkCount;i++) pathEdge[i]=0;

        if (s<0 || t<0 || s==t) return;

        int INF = 1_000_000_000;
        int[] dist = new int[n];
        int[] prev = new int[n];
        int[] used = new int[n];
        for (i=0;i<n;i++){ dist[i]=INF; prev[i]=-1; used[i]=0; }
        dist[s]=0;

        // Dijkstra dengan pemilihan minimum linear
        for (int it=0; it<n; it++){
            int u=-1; int best=INF;
            for (i=0;i<n;i++) if (used[i]==0 && dist[i]<best){ best=dist[i]; u=i; }
            if (u==-1) break;
            used[u]=1;
            // relax semua neighbor via edges aktif
            int e;
            for (e=0;e<Graph.linkCount;e++){
                if (Graph.linkUp[e]==0) continue;
                int a = idxSwitchOfPort(Graph.linkA[e]);
                int b = idxSwitchOfPort(Graph.linkB[e]);
                if (a<0 || b<0) continue;
                if (a==u){
                    int nd = dist[u] + Graph.linkCost[e];
                    if (nd < dist[b]){ dist[b]=nd; prev[b]=u; }
                } else if (b==u){
                    int nd = dist[u] + Graph.linkCost[e];
                    if (nd < dist[a]){ dist[a]=nd; prev[a]=u; }
                }
            }
        }

        if (prev[t]==-1) return; // no path

        // tandai edges di jalur s->t
        int cur = t;
        while (cur != s){
            int par = prev[cur];
            // cari edge (par,cur) dan mark
            mark_edge_between(par, cur);
            cur = par;
        }
    }

    static void mark_edge_between(int u,int v){
        int i;
        for (i=0;i<Graph.linkCount;i++){
            if (Graph.linkUp[i]==0) continue;
            int a = idxSwitchOfPort(Graph.linkA[i]);
            int b = idxSwitchOfPort(Graph.linkB[i]);
            if ((a==u && b==v) || (a==v && b==u)){
                pathEdge[i]=1; return;
            }
        }
    }
}
