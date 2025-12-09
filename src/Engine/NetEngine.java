package Engine;

import PDU.*;

/**
 * NetEngine: event-driven ping simulator.
 * - Priority (smaller = sooner) merepresentasikan waktu deliver (tick).
 * - Gunakan NetEngine.tick() untuk memajukan waktu dan memproses event due.
 */
public class NetEngine {

    public static int now = 0;                 // current tick
    private static final int VISUAL_DELAY_MS = 750;
    private static final int REPLY_TTL = 64;

    public static PriorityQueue q = new PriorityQueue(); // stores encoded PDU strings

    /** Advance one tick and process all due events. */
    public static void tick() {
        try { Thread.sleep(VISUAL_DELAY_MS); } catch (Exception ignored) {}
        now++;

        while (!q.isEmpty()) {
            int dueTick = q.peekPriority();
            if (dueTick > now) break;
            String pdu = q.dequeue();
            processPdu(pdu);
        }
    }

    /** Schedule an ICMP-like echo request along the BFS path from src to dst. */
    public static void sendPing(String src, String dst, int seq, int ttl) {
        String pathCsv = NetRouting.bfsPathCsv(src, dst);
        int pathLen = NetRouting.csvCount(pathCsv);
        if (pathLen == 0) {
            System.out.println("PING: no path from " + src + " to " + dst);
            return;
        }

        String req = PDU.make("REQ", seq, src, dst, ttl, now, 0, pathLen, pathCsv);
        q.enqueue(req, now + 1); // first hop on next tick

        System.out.println("PING " + dst + " from " + src +
                           ": seq=" + seq + " ttl=" + ttl + " path=" + pathCsv);
    }


    private static void processPdu(String pdu) {
        if (pdu == null) return;

        String type     = PDU.getType(pdu);     // REQ | RPL
        int    seq      = PDU.getSeq(pdu);
        String source   = PDU.getSrc(pdu);
        String dest     = PDU.getDst(pdu);
        int    ttl      = PDU.getTTL(pdu);
        int    sentAt   = PDU.getTSent(pdu);
        int    hopIndex = PDU.getHop(pdu);
        int    pathLen  = PDU.getPathLen(pdu);
        String pathCsv  = PDU.getPathCsv(pdu);

        if (ttl <= 0) {
            System.out.println("TTL expired for seq=" + seq);
            return;
        }

        if (type.equals("REQ")) {
            handleRequest(seq, source, dest, ttl, sentAt, hopIndex, pathLen, pathCsv);
        } else {
            handleReply(seq, source, dest, ttl, sentAt, hopIndex, pathLen, pathCsv);
        }
    }

    private static void handleRequest(
            int seq, String src, String dst, int ttl, int sentAt,
            int hopIndex, int pathLen, String pathCsv) {

        String currentNode = NetRouting.csvAt(pathCsv, hopIndex);
        int nextHop = hopIndex + 1;

        // Arrived (defensive: if nextHop passes end)
        if (nextHop >= pathLen || currentNode.equals(dst)) {
            arriveAtDestination(seq, src, dst, sentAt, pathCsv, pathLen);
            return;
        }

        String nextNode = NetRouting.csvAt(pathCsv, nextHop);

        // fast-arrival optimization when next node is the destination
        String forward = PDU.make("REQ", seq, src, dst, ttl - 1, sentAt, nextHop, pathLen, pathCsv);
        int deliverAt = nextNode.equals(dst) ? now : now + 1;
        q.enqueue(forward, deliverAt);
    }

    private static void handleReply(
            int seq, String src, String dst, int ttl, int sentAt,
            int hopIndex, int pathLen, String pathCsv) {

        String currentNode = NetRouting.csvAt(pathCsv, hopIndex);
        int prevHop = hopIndex - 1;

        // reached source
        if (prevHop < 0) {
            int rtt = now - sentAt;
            System.out.println("Reply from " + src + ": seq=" + seq + " rtt=" + rtt + " ticks");
            return;
        }

        String back = PDU.make("RPL", seq, src, dst, ttl - 1, sentAt, prevHop, pathLen, pathCsv);
        q.enqueue(back, now + 1);
    }

    private static void arriveAtDestination(
            int seq, String src, String dst, int sentAt, String pathCsv, int pathLen) {

        System.out.println("Echo request arrived at " + dst + " (seq=" + seq + "), sending reply...");
        String reply = PDU.make("RPL", seq, src, dst, REPLY_TTL, sentAt, pathLen - 1, pathLen, pathCsv);
        q.enqueue(reply, now + 1);
    }
}
