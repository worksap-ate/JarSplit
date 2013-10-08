package split;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Node {
	private String data;
	private List<Node> children;
	
	// public boolean visitting;
	// public Set<Node> dependency;
	// public Set<Node> todo_notify;

	public String toString(){
		return this.data;
	}
	public int hashCode(){
        return data.hashCode();
    }
	public boolean equals(Object o){
		return this.data.equals(((Node)o).data);
    } 
	
	public Node(String data){
		this.data = data;
		this.children = new ArrayList<Node>();
		
		// this.visitting = false;
		// this.dependency = new HashSet<Node>();
		// this.todo_notify = new HashSet<Node>();
	}
	
	public void addChild(Node child){
		this.children.add(child);
	}
	public List<Node> getChildren(){
		return this.children;
	}

	public String getData(){
		return this.data;
	}

	public void show(int depth){
		StringBuilder indent = new StringBuilder();
		for(int i=0; i<depth; i++){
			indent.append("    ");
		}

		System.out.println(indent + this.data);
		for(Node child : this.children){
			child.show(depth + 1);
		}
	}
}
