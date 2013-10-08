package split;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import scc.*;
import java.io.*;

public class CopyOfGraphMaker {

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

	
	private void addRelation(Graph<Node> graph, Set<String> roots, Map<String, Set<String>> dependency, Map<String, Node> nodes){
		for(Map.Entry<String, Set<String>> entry : dependency.entrySet()){
			Node node = nodes.get(entry.getKey());
			for(String key2 : entry.getValue()){
				graph.addEdge(node, nodes.get(key2));
				roots.remove(key2);
			}
		}
	}

	private void addNonMySuperClass(Graph<Node> graph, Set<String> roots, Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs, Map<String, Node> nodes){
		for(Map.Entry<String, Set<String>> entry : super2subs.entrySet()){
			String superName = entry.getKey();
			Set<String> subNames = entry.getValue();
			Set<Node> subNodes = new HashSet<Node>();
			for(String subName : entry.getValue()){
				subNodes.add(nodes.get(subName));
			}
			
			for(Map.Entry<String, Set<String>> depends : dependency.entrySet()){
				String key = depends.getKey();
				Node targetNode = nodes.get(key);

				if(depends.getValue().contains(superName) && !subNames.contains(key)){
					for(Node subNode : subNodes){
						graph.addEdge(targetNode, subNode);
						roots.remove(subNode.getData());
					}
					
				}
			}
			
		}
	}
	
	
	public Set<Node> makeGraph(Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs){
		Graph<Node> graph = new Graph<Node>();
		Set<String> roots = new HashSet<String>();
		Map<String, Node> nodes = convertToNode(dependency);
		addRelation(graph, roots, dependency, nodes);
		addNonMySuperClass(graph, roots, dependency, super2subs, nodes);
		
		// graph.show();
		
		// Set<Node> rootNodes = new HashSet<Node>();
		//for(String root : getRealRoots(dependency.keySet(), nodes)){
		//	rootNodes.add(nodes.get(root));
		//}
		// for(String root : roots){
		// 	rootNodes.add(nodes.get(root));
		// }
		
		System.out.println(graph.getVertexes().size());
		try {
			Graph<Set<Node>> sGraph = new Simplifier<Node>().simplify(graph);
			System.out.println(sGraph.getVertexes().size());
			sGraph.show(new PrintStream("SimpleGraph.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
//		SCC<Node> scc = new SCC<Node>();
//		Set<Set<Node>> sccs = scc.stronglyConnectedComponents(graph);
//		
//		try {
//			PrintStream pw = new PrintStream("result.txt");
//			for(Set<Node> c : sccs){
//				pw.println(c);
//			}
//			pw.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		// return rootNodes;
        
        System.out.println("finish");
        return new HashSet<Node>();
	}


}
