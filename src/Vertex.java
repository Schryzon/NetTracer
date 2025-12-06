public class Vertex {
    public String id; // e.g., "S1:1"

    public Vertex(String id) {
        this.id = id;
    }

    public boolean equals(Vertex other) {
        return this.id.equals(other.id);
    }
}