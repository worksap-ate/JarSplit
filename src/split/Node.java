package split;

import java.util.HashSet;
import java.util.Set;


public class Node {
	String data;
	Set<Node> children;
	public Node(String data){
		this.data = data;
		this.children = new HashSet<Node>();
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
