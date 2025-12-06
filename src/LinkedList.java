public class LinkedList {
    private LLNode head;

    public LinkedList() {
        head = null;
    }

    public void add(int data) {
        LLNode newNode = new LLNode(data);
        if (head == null) {
            head = newNode;
            return;
        }
        LLNode cur = head;
        while (cur.next != null) {
            cur = cur.next;
        }
        cur.next = newNode;
    }

    public void remove(int data) {
        if (head == null)
            return;

        if (head.data == data) {
            head = head.next;
            return;
        }

        LLNode cur = head;
        while (cur.next != null && cur.next.data != data) {
            cur = cur.next;
        }

        if (cur.next != null) {
            cur.next = cur.next.next;
        }
    }

    public boolean search(int data) {
        LLNode cur = head;
        while (cur != null) {
            if (cur.data == data)
                return true;
            cur = cur.next;
        }
        return false;
    }

    public void display() {
        LLNode cur = head;
        while (cur != null) {
            System.out.print(cur.data + " -> ");
            cur = cur.next;
        }
        System.out.println("null");
    }

    public LLNode getHead() {
        return head;
    }

    // merge sort
    public static LLNode mergeSort(LLNode head) {
        if (head == null || head.next == null)
            return head;

        LLNode mid = getMiddle(head);
        LLNode nextMid = mid.next;

        mid.next = null;

        LLNode left = mergeSort(head);
        LLNode right = mergeSort(nextMid);

        return sortedMerge(left, right);
    }

    static LLNode sortedMerge(LLNode a, LLNode b) {
        if (a == null)
            return b;
        if (b == null)
            return a;

        LLNode result;

        if (a.data <= b.data) {
            result = a;
            result.next = sortedMerge(a.next, b);
        } else {
            result = b;
            result.next = sortedMerge(a, b.next);
        }

        return result;
    }

    static LLNode getMiddle(LLNode head) {
        if (head == null)
            return head;

        LLNode slow = head, fast = head.next;

        while (fast != null) {
            fast = fast.next;
            if (fast != null) {
                slow = slow.next;
                fast = fast.next;
            }
        }
        return slow;
    }
}