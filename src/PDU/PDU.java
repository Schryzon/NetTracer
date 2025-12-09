package PDU;

// PDU string format: "ICMP|TYPE|SEQ|SRC|DST|TTL|TSENT|HOPIDX|PATHLEN|PATHCSV"
// TYPE: REQ (request) or RPL (reply)
public class PDU {

    public static String make(String type, int seq, String src, String dst,
                              int ttl, int tSent, int hopIdx, int pathLen, String pathCsv) {
        return "ICMP|" + type + "|" + seq + "|" + src + "|" + dst + "|" +
               ttl + "|" + tSent + "|" + hopIdx + "|" + pathLen + "|" + pathCsv;
    }

    // simple getters tanpa split regex (manual scan)
    public static String getField(String s, int fieldIndex) {
        // fieldIndex: 0=ICMP,1=TYPE,2=SEQ,3=SRC,4=DST,5=TTL,6=TSENT,7=HOPIDX,8=PATHLEN,9=PATHCSV
        int n = s.length();
        int idx = 0; int start = 0; int i = 0;
        for (i = 0; i <= n; i++) {
            if (i == n || s.charAt(i) == '|') {
                if (idx == fieldIndex) return s.substring(start, i);
                idx++;
                start = i + 1;
            }
        }
        return "";
    }

    public static String getType(String s){ return getField(s,1); }
    public static int    getSeq (String s){ return parseInt(getField(s,2)); }
    public static String getSrc (String s){ return getField(s,3); }
    public static String getDst (String s){ return getField(s,4); }
    public static int    getTTL (String s){ return parseInt(getField(s,5)); }
    public static int    getTSent(String s){ return parseInt(getField(s,6)); }
    public static int    getHop (String s){ return parseInt(getField(s,7)); }
    public static int    getPathLen(String s){ return parseInt(getField(s,8)); }
    public static String getPathCsv(String s){ return getField(s,9); }

    public static int parseInt(String x){
        try { return Integer.parseInt(x); } catch(Exception e){ return 0; }
    }
}
