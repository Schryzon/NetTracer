public class Stack {
    private StackNode top;

    public Stack() {
        top = null;
    }

    public void push(String data) {
        StackNode newNode = new StackNode(data);
        newNode.next = top;
        top = newNode;
    }

    public String pop() {
        if (isEmpty())
            return null;
        String val = top.data;
        top = top.next;
        return val;
    }

    public String peek() {
        if (isEmpty())
            return null;
        return top.data;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public void display() {
        StackNode cur = top;
        while (cur != null) {
            System.out.println(cur.data);
            cur = cur.next;
        }
    }
}
