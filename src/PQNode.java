public class PQNode {
    public String data;
    public int priority;
    public PQNode next;

    public PQNode(String data, int priority) {
        this.data = data;
        this.priority = priority;
        this.next = null;
    }
}
