package split;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Spliter {
	public void start(Map<String, Set<String>> dependency){
		Map<String, Node> nodes = new HashMap<String, Node>();
		for(String key : dependency.keySet()){
			nodes.put(key, new Node(key));
		}
		Set<String> noRoots = new HashSet<String>();
	
		for(Map.Entry<String, Set<String>> entry : dependency.entrySet()){
			Node node = nodes.get(entry.getKey());
			for(String child : entry.getValue()){
				Node childNode;
				if(nodes.containsKey(child)){
					childNode = nodes.get(child);
					noRoots.add(child);
				}else{
					childNode = new Node(child);
				}
				node.children.add(childNode);
			}			
		}
		for(Map.Entry<String, Node> e : nodes.entrySet()){
			if(!noRoots.contains(e.getKey())){
				// e.getValue().show(0);
				System.out.println("root:" + e.getKey());
			}
		}
	}

	public static String toString(Map<String, Set<String>> o){
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String, Set<String>> entry : o.entrySet()){
			sb.append(entry.getKey() + " : {");
			for(String e : entry.getValue()){
				sb.append(e + ", ");
			}
			sb.append("}\n");
		}
		return sb.toString();
	}
}
