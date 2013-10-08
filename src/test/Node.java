package test;

public class Node {
    private int data;
    public Node(int data){
        this.data = data;
    }

    public boolean equals(Object obj){
        return this.data == ((Node)obj).data;
    }

    public int hashCode(){
        return this.data;
    }

    public String toString(){
        return "Node" + this.data;
    }
}
