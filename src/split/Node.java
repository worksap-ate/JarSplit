package split;

import java.util.*;

public class Node {
	private String data;
	private List<Node> parents;
	private List<Node> children;

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
		this.parents = new ArrayList<Node>();
		this.children = new ArrayList<Node>();
	}
	
	public void addChild(Node child){
		this.children.add(child);
		if(!child.parents.contains(this)){
			child.parents.add(this);
		}
	}

	public List<Node> getChildren(){
		return this.children;
	}

	public String getData(){
		return this.data;
	}
	
	public List<Node> getParents(){
		return this.parents;
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
