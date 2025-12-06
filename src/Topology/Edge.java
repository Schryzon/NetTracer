package Topology;
public class Edge {
    public String portA;   // "S1:1"
    public String portB;   // "S2:1"
    public int cost;
    public boolean up;     // true = operational

    public Edge(String a, String b, int cost) {
        this.portA = a;
        this.portB = b;
        this.cost = cost;
        this.up = true;
    }

    public boolean connects(String x, String y) {
        return (portA.equals(x) && portB.equals(y)) ||
               (portA.equals(y) && portB.equals(x));
    }
}