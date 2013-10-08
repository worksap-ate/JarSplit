package split;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import scc.*;
import java.io.*;

public class CopyOfCopyOfGraphMaker {

	private Set<String> getRealRoots(Set<String> provisionalRoots, Map<String, Node> nodes){
		Set<String> ret = new HashSet<String>();
		for(String key : provisionalRoots){
			ret.add(key);
		}
		for(String rootName : provisionalRoots){
			Node rootNode = nodes.get(rootName);
			for(Node childNode : rootNode.getChildren()){
				String childName = childNode.getData();
				if(ret.contains(childName)){
					ret.remove(childName);
					System.out.println("remove provisional root " + childName);
				}
			}
		}
		return ret;
	}

	
	private Map<String, Node> convertToNode(Map<String, Set<String>> dependency){
		Map<String, Node> nodes = new HashMap<String, Node>();
		for(Map.Entry<String, Set<String>> entry : dependency.entrySet()){
			String key = entry.getKey();
			if(!nodes.containsKey(key)){
				nodes.put(key, new Node(key));
			}
			for(String key2 : entry.getValue()){
				if(!nodes.containsKey(key2)){
					nodes.put(key2, new Node(key2));
				}
			}
		}
		return nodes;
	}

	
	private void addRelation(Graph<String> graph, Set<String> roots, Map<String, Set<String>> dependency){
		for(Map.Entry<String, Set<String>> entry : dependency.entrySet()){
			for(String key2 : entry.getValue()){
				graph.addEdge(entry.getKey(), key2);
				roots.remove(key2);
			}
		}
	}

	private void addNonMySuperClass(Graph<String> graph, Set<String> roots, Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs){
		for(Map.Entry<String, Set<String>> entry : super2subs.entrySet()){
			String superName = entry.getKey();
			Set<String> subNames = entry.getValue();
			
			for(Map.Entry<String, Set<String>> depends : dependency.entrySet()){
				String key = depends.getKey();
				if(depends.getValue().contains(superName) && !subNames.contains(key)){
					for(String subName : subNames){
						graph.addEdge(key, subName);
						roots.remove(subName);
					}
					
				}
			}
			
		}
	}
	
	
	public Set<Node> makeGraph(Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs){
		Graph<String> graph = new Graph<String>();
		Set<String> roots = new HashSet<String>();
		roots.addAll(dependency.keySet());
		addRelation(graph, roots, dependency);
		addNonMySuperClass(graph, roots, dependency, super2subs);
		
		System.out.println("roots size: " + roots.size());
		
		System.out.println(graph.getVertexes().size());
		try {
			Graph<Set<String>> sGraph = new Simplifier<String>().simplify(graph, roots);
			System.out.println(sGraph.getVertexes().size());
			sGraph.show(new PrintStream("SimpleGraph.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
        System.out.println("finish");
        return new HashSet<Node>();
	}


}
