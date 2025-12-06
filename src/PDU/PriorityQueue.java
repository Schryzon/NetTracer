package PDU;
public class PriorityQueue {
    private PQNode front;

    public PriorityQueue() {
        front = null;
    }

    // enqueue berdasarkan priority jadi lebih kecil nilai prioritynya lebih di
    // prioritaskan
    public void enqueue(String data, int priority) {
        PQNode newNode = new PQNode(data, priority);

        if (front == null || priority < front.priority) {
            newNode.next = front;
            front = newNode;
            return;
        }

        PQNode cur = front;
        while (cur.next != null && cur.next.priority <= priority) {
            cur = cur.next;
        }

        newNode.next = cur.next;
        cur.next = newNode;
    }

    public String dequeue() {
        if (isEmpty())
            return null;
        String val = front.data;
        front = front.next;
        return val;
    }

    public int peekPriority() {
        if (front == null) return Integer.MAX_VALUE;
        return front.priority;
    }

    public String peekData() {
        if (front == null) return null;
        return front.data;
    }


    public boolean isEmpty() {
        return front == null;
    }

    public void display() {
        PQNode cur = front;
        while (cur != null) {
            System.out.println(cur.data + " (prio=" + cur.priority + ")");
            cur = cur.next;
        }
    }
}
