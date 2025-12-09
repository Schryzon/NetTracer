package Utils;
import Topology.Graph;

/**
 * AsciiTopo — ASCII topology renderer (no java.util).
 * - Kotak node: +-----+ / | NAME | / +-----+
 * - Link normal '.' , MST '=' , Path '#'
 * - Layout adaptif: radius dihitung dari N & lebar kotak.
 * - Connector: garis dimulai/berakhir di TEPI kotak (bukan dari tengah).
 */
public class AsciiTopo {

    // ===== Canvas (bisa diubah via setCanvas) =====
    static int W = 120;
    static int H = 40;

    // Node box metrics
    static final int PAD_X = 2;     // spasi kiri-kanan nama
    static final int BOX_H = 3;     // top, mid, bottom
    static final int MIN_BOX_W = 7; // minimal lebar kotak
    static final int MIN_GAP = 4;   // jarak minimal antar pusat kotak sepanjang busur

    // Data gambar
    static char[][] cv = new char[H][W];
    static int[] cx = new int[Graph.MAX_SWITCH];  // center x
    static int[] cy = new int[Graph.MAX_SWITCH];  // center y
    static int[] halfW = new int[Graph.MAX_SWITCH]; // setengah lebar kotak per node

    static int[] mstEdge  = new int[Graph.MAX_LINK];
    static int[] pathEdge = new int[Graph.MAX_LINK];

    // ===== Public API =====
    public static void setCanvas(int width, int height){
        if (width < 40) width = 40;
        if (height < 12) height = 12;
        W = width; H = height;
        cv = new char[H][W];
    }

    public static void show_normal() {
        clear(); layout_circle(); draw_all_edges('.'); draw_boxes(); flush();
    }
    public static void show_mst() {
        clear(); layout_circle(); compute_mst();
        draw_all_edges('.'); draw_flag_edges(mstEdge, '='); draw_boxes(); flush();
    }
    public static void show_path(String A, String B) {
        clear(); layout_circle(); compute_path(A,B);
        draw_all_edges('.'); draw_flag_edges(pathEdge, '#'); draw_boxes(); flush();
    }

    // ===== Canvas helpers =====
    static void clear(){
        for (int y=0;y<H;y++) for (int x=0;x<W;x++) cv[y][x]=' ';
    }
    static void put(int x,int y,char c){
        if (x>=0 && x<W && y>=0 && y<H) cv[y][x]=c;
    }
    static void text(int x,int y,String s){
        for (int i=0;i<s.length();i++) put(x+i,y,s.charAt(i));
    }
    static void flush(){
        for (int y=0;y<H;y++){
            String line="";
            for (int x=0;x<W;x++) line+=cv[y][x];
            System.out.println(line);
        }
    }

    // ===== Layout: circle with adaptive radius =====
    static void layout_circle(){
        int n = Graph.swCount;
        if (n==0) return;

        // hitung halfW (setengah lebar kotak) per node
        int maxHalf = 0, sumBoxW = 0;
        for (int i=0;i<n;i++){
            String name = (Graph.swName[i]==null) ? "" : Graph.swName[i];
            int boxW = name.length() + PAD_X*2;
            if (boxW < MIN_BOX_W) boxW = MIN_BOX_W;
            halfW[i] = boxW/2;
            sumBoxW += boxW;
            if (halfW[i] > maxHalf) maxHalf = halfW[i];
        }

        // sudut antar node
        double step = (2.0*Math.PI) / n;

        // radius awal: agar tidak mentok ke tepi canvas
        double cx0 = W/2.0, cy0 = H/2.0;
        double rBoundX = (W/2.0) - (maxHalf + 2);
        double rBoundY = (H/2.0) - (BOX_H); // tinggi kotak 3 baris
        double r = Math.min(rBoundX, rBoundY);

        // radius minimal agar busur antar node >= rata-rata lebar kotak + gap
        int avgBoxW = (sumBoxW / n);
        double minArc = avgBoxW + MIN_GAP;
        double rNeed = minArc / step; // arc = r * step  >= minArc
        if (r < rNeed) r = rNeed;
        if (r < 3) r = 3; // jaga-jaga

        for (int i=0;i<n;i++){
            double ang = i * step;
            cx[i] = (int)(cx0 + r*Math.cos(ang));
            cy[i] = (int)(cy0 + r*Math.sin(ang));
        }
    }

    // ===== Boxes =====
    static void draw_boxes(){
        for (int i=0;i<Graph.swCount;i++) draw_box(i);
    }
    static void draw_box(int i){
        String name = (Graph.swName[i]==null) ? "" : Graph.swName[i];
        int boxW = halfW[i]*2; if (boxW < MIN_BOX_W) boxW = MIN_BOX_W;

        int left = cx[i]-boxW/2;
        int top  = cy[i]-1;
        int right = left + boxW - 1;
        int bottom = top + 2;

        // top
        put(left, top, '+'); for (int x=left+1;x<right;x++) put(x,top,'-'); put(right, top, '+');
        // mid
        put(left, cy[i], '|');
        // left padding
        for (int p=0; p<PAD_X; p++) put(left+1+p, cy[i], ' ');
        // name
        text(left+PAD_X+1-1, cy[i], name);
        // right padding
        int nameEnd = left + PAD_X + name.length();
        for (int x=nameEnd+1; x<right; x++) put(x, cy[i], ' ');
        put(right, cy[i], '|');
        // bottom
        put(left, bottom, '+'); for (int x=left+1;x<right;x++) put(x,bottom,'-'); put(right, bottom, '+');
    }

    // ===== Edges =====
    static void draw_all_edges(char ch){
        for (int e=0;e<Graph.linkCount;e++){
            if (Graph.linkUp[e]==0) continue;
            int a = idxOfPort(Graph.linkA[e]);
            int b = idxOfPort(Graph.linkB[e]);
            if (a<0 || b<0) continue;

            // konektor: titik pada TEPI kotak, bukan pusat
            int[] sa = connectorOnBox(a, b);
            int[] sb = connectorOnBox(b, a);
            draw_line(sa[0], sa[1], sb[0], sb[1], ch);
        }
    }
    static void draw_flag_edges(int[] flag, char ch){
        for (int e=0;e<Graph.linkCount;e++){
            if (Graph.linkUp[e]==0 || flag[e]!=1) continue;
            int a = idxOfPort(Graph.linkA[e]);
            int b = idxOfPort(Graph.linkB[e]);
            if (a<0 || b<0) continue;
            int[] sa = connectorOnBox(a, b);
            int[] sb = connectorOnBox(b, a);
            draw_line(sa[0], sa[1], sb[0], sb[1], ch);
        }
    }

    // Bresenham
    static void draw_line(int x0,int y0,int x1,int y1,char ch){
        int dx = abs(x1 - x0), dy = abs(y1 - y0);
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

    // ===== Connector: intersection ray(center->otherCenter) dengan perimeter kotak =====
    static int[] connectorOnBox(int i, int j){
        int bw = halfW[i]*2; if (bw < MIN_BOX_W) bw = MIN_BOX_W;
        int left = cx[i]-bw/2, right = left+bw-1;
        int top = cy[i]-1, bottom = top+2;

        int dx = cx[j]-cx[i];
        int dy = cy[j]-cy[i];
        if (dx==0 && dy==0) return new int[]{cx[i], cy[i]}; // degenerate

        // pilih sisi dominan agar titik keluar pas di tepi
        if (abs(dx) >= abs(dy)) {
            // horizontal-dominant → tembak kanan/kiri
            int x = (dx >= 0) ? right : left;
            // proporsional y
            // t = (x - cx[i]) / dx
            // y = cy[i] + t*dy
            double t = ((double)(x - cx[i])) / (double)dx;
            int y = cy[i] + (int)Math.round(t * dy);
            // clamp ke tepi vertikal
            if (y < top) y = top;
            if (y > bottom) y = bottom;
            return new int[]{x, y};
        } else {
            // vertical-dominant → tembak atas/bawah
            int y = (dy >= 0) ? bottom : top;
            double t = ((double)(y - cy[i])) / (double)dy;
            int x = cx[i] + (int)Math.round(t * dx);
            if (x < left) x = left;
            if (x > right) x = right;
            return new int[]{x, y};
        }
    }

    // ===== Helpers =====
    static int idxOfPort(String port){
        int k = indexOf(port, ':');
        String sw = (k<0)?port:port.substring(0,k);
        return idxOfSwitch(sw);
    }
    static int idxOfSwitch(String sw){
        for (int i=0;i<Graph.swCount;i++){
            if (Graph.swName[i]!=null && Graph.swName[i].equals(sw)) return i;
        }
        return -1;
    }
    static int indexOf(String s,char c){
        for (int i=0;i<s.length();i++) if (s.charAt(i)==c) return i;
        return -1;
    }
    static int abs(int v){ return (v<0)?-v:v; }

    // ===== Local MST (Kruskal) =====
    static void compute_mst(){
        for (int i=0;i<Graph.linkCount;i++) mstEdge[i]=0;

        int m=0; int[] idx=new int[Graph.linkCount];
        for (int i=0;i<Graph.linkCount;i++) if (Graph.linkUp[i]==1) idx[m++]=i;

        mergesort_by_cost(idx,m);

        int n=Graph.swCount;
        int[] p=new int[n], r=new int[n];
        for (int i=0;i<n;i++){ p[i]=i; r[i]=0; }

        for (int k=0;k<m;k++){
            int e=idx[k];
            int a=idxOfPort(Graph.linkA[e]);
            int b=idxOfPort(Graph.linkB[e]);
            if (a<0||b<0) continue;
            if (uf_union(p,r,a,b)) mstEdge[e]=1;
        }
    }
    static int uf_find(int[] p,int x){ if(p[x]==x) return x; p[x]=uf_find(p,p[x]); return p[x]; }
    static boolean uf_union(int[] p,int[] r,int a,int b){
        int pa=uf_find(p,a), pb=uf_find(p,b);
        if (pa==pb) return false;
        if (r[pa]<r[pb]) p[pa]=pb;
        else if (r[pb]<r[pa]) p[pb]=pa;
        else { p[pb]=pa; r[pa]++; }
        return true;
    }
    static void mergesort_by_cost(int[] a,int len){
        if (len<=1) return;
        int[] aux=new int[len]; ms(a,aux,0,len);
    }
    static void ms(int[] a,int[] aux,int lo,int hi){
        if (hi-lo<=1) return;
        int mid=(lo+hi)/2; ms(a,aux,lo,mid); ms(a,aux,mid,hi);
        for (int i=lo;i<hi;i++) aux[i]=a[i];
        int i=lo,j=mid,k=lo;
        while(i<mid && j<hi){
            int ei=aux[i], ej=aux[j];
            int ci=Graph.linkCost[ei], cj=Graph.linkCost[ej];
            if (ci<=cj) a[k++]=aux[i++]; else a[k++]=aux[j++];
        }
        while(i<mid) a[k++]=aux[i++];
        while(j<hi) a[k++]=aux[j++];
    }

    // ===== Local shortest path (Dijkstra O(n^2)) =====
    static void compute_path(String A,String B){
        for (int i=0;i<Graph.linkCount;i++) pathEdge[i]=0;

        int s=idxOfSwitch(A), t=idxOfSwitch(B);
        if (s<0||t<0||s==t) return;

        int n=Graph.swCount, INF=1_000_000_000;
        int[] dist=new int[n], prev=new int[n], used=new int[n];
        for (int i=0;i<n;i++){ dist[i]=INF; prev[i]=-1; used[i]=0; }
        dist[s]=0;

        for (int it=0; it<n; it++){
            int u=-1, best=INF;
            for (int i=0;i<n;i++) if (used[i]==0 && dist[i]<best){ best=dist[i]; u=i; }
            if (u==-1) break; used[u]=1;

            for (int e=0;e<Graph.linkCount;e++){
                if (Graph.linkUp[e]==0) continue;
                int a=idxOfPort(Graph.linkA[e]), b=idxOfPort(Graph.linkB[e]);
                if (a<0||b<0) continue;
                if (a==u){
                    int nd=dist[u]+Graph.linkCost[e];
                    if (nd<dist[b]){ dist[b]=nd; prev[b]=u; }
                } else if (b==u){
                    int nd=dist[u]+Graph.linkCost[e];
                    if (nd<dist[a]){ dist[a]=nd; prev[a]=u; }
                }
            }
        }

        if (prev[t]==-1) return;
        int cur=t;
        while(cur!=s){
            int par=prev[cur];
            mark_edge(par,cur);
            cur=par;
        }
    }
    static void mark_edge(int u,int v){
        for (int e=0;e<Graph.linkCount;e++){
            if (Graph.linkUp[e]==0) continue;
            int a=idxOfPort(Graph.linkA[e]), b=idxOfPort(Graph.linkB[e]);
            if ((a==u&&b==v)||(a==v&&b==u)){ pathEdge[e]=1; return; }
        }
    }
}
