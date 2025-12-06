package Engine;
// NetEngine.java — event loop ping berbasis PriorityQueue (String,data; int,priority)
import PDU.*;

public class NetEngine {

    // current time tick
    public static int now = 0;

    // antrean event global (gunakan PriorityQueue buatan kamu)
    public static PriorityQueue q = new PriorityQueue();

    // satu tick: proses semua event dengan priority == now
    public static void tick(){
        now++;

        // karena PQ kamu sudah punya peekPriority(), proses selama event due
        while (!q.isEmpty()){
            int pr = q.peekPriority();
            if (pr > now) break;
            String pdu = q.dequeue(); // due event
            handlePDU(pdu);
        }
    }

    // kirim ping request: jadwalkan PDU REQ di tick now
    public static void sendPing(String src, String dst, int seq, int ttl){
        String pathCsv = NetRouting.bfsPathCsv(src, dst);
        int pathLen = NetRouting.csvCount(pathCsv);
        if (pathLen == 0){
            System.out.println("PING: no path from " + src + " to " + dst);
            return;
        }
        // hopIdx 0 = berada di node src, akan dikirim ke hop berikutnya
        String req = PDU.make("REQ", seq, src, dst, ttl, now, 0, pathLen, pathCsv);
        // schedule immediate delivery (now+1) ke hop berikutnya
        q.enqueue(req, now + 1);
        System.out.println("PING " + dst + " from " + src + ": seq=" + seq + " ttl=" + ttl + " path=" + pathCsv);
    }

    // handler event: kirim maju 1 hop; jika sampai di dst → kirim reply; jika reply sampai src → print RTT
    static void handlePDU(String pdu){
        // safety
        if (pdu == null) return;
        String type = PDU.getType(pdu);
        int    seq  = PDU.getSeq(pdu);
        String src  = PDU.getSrc(pdu);
        String dst  = PDU.getDst(pdu);
        int    ttl  = PDU.getTTL(pdu);
        int    ts   = PDU.getTSent(pdu);
        int    hop  = PDU.getHop(pdu);
        int    plen = PDU.getPathLen(pdu);
        String path = PDU.getPathCsv(pdu);

        if (ttl <= 0){
            System.out.println("TTL expired for seq=" + seq);
            return;
        }

        if (type.equals("REQ")){
            // posisi saat ini = path[hop]
            String curNode = NetRouting.csvAt(path, hop);
            // next hop:
            int nextHopIdx = hop + 1;
            if (nextHopIdx >= plen){
                // sudah lewat batas (harusnya tidak terjadi), treat as arrived
                deliverRequestAtDst(seq, src, dst, ts, path, plen);
                return;
            }
            String nextNode = NetRouting.csvAt(path, nextHopIdx);

            // kalau next == dst → anggap tiba di dst pada tick ini (simulasi per hop)
            if (nextNode.equals(dst)){
                // jadwalkan deliver di dst
                String arrive = PDU.make("REQ", seq, src, dst, ttl-1, ts, nextHopIdx, plen, path);
                q.enqueue(arrive, now); // tiba di tick yang sama (atau now+1 kalau mau delay)
                // proses arrival ketika dequeue berikutnya
            } else {
                // kirim ke hop berikutnya
                String forward = PDU.make("REQ", seq, src, dst, ttl-1, ts, nextHopIdx, plen, path);
                q.enqueue(forward, now + 1);
            }

            // jika ini event “arrival di dst” (hop menunjuk dst)
            if (curNode.equals(dst)){
                deliverRequestAtDst(seq, src, dst, ts, path, plen);
            }

        } else { // RPL
            // reply berjalan mundur: path reversed secara indeks
            // Kita encode path yang sama, tapi hop berkurang
            String curNode = NetRouting.csvAt(path, hop);
            int prevHopIdx = hop - 1;

            if (prevHopIdx < 0){
                // sampai ke src
                int rtt = now - ts;
                System.out.println("Reply from " + src + ": seq=" + seq + " rtt=" + rtt + " ticks");
                return;
            }
            String back = PDU.make("RPL", seq, src, dst, ttl-1, ts, prevHopIdx, plen, path);
            q.enqueue(back, now + 1);
        }
    }

    static void deliverRequestAtDst(int seq, String src, String dst, int ts, String path, int plen){
        System.out.println("Echo request arrived at " + dst + " (seq=" + seq + "), sending reply...");
        // buat reply: hop = index node dst dalam path = plen-1
        String reply = PDU.make("RPL", seq, src, dst, 64, ts, plen - 1, plen, path);
        q.enqueue(reply, now + 1);
    }
}
