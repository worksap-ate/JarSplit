package split;

import java.util.*;
import kmeans.Kmeans;
import scc.*;

public class GraphMaker {
	private Map<String, Node> convertToNode(Map<String, Set<String>> dependency){
		Map<String, Node> nodes = new HashMap<String, Node>(dependency.size());
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

	
	private void addRelation(Map<String, Set<String>> dependency, Map<String, Node> nodes, Set<String> roots){
		for(Map.Entry<String, Set<String>> entry : dependency.entrySet()){
			Node rootNode = nodes.get(entry.getKey());
			for(String childName : entry.getValue()){
				rootNode.addChild(nodes.get(childName));
				roots.remove(childName);
			}
		}
	}

	private void addNonMySuperClass(Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs, Map<String, Node> nodes, Set<String> roots){
		for(Map.Entry<String, Set<String>> super2sub : super2subs.entrySet()){
			String superName = super2sub.getKey();
			Set<String> subNames = super2sub.getValue();
			Set<Node> subNodes = new HashSet<Node>();
			for(String subName : super2sub.getValue()){
				subNodes.add(nodes.get(subName));
			}
			
			for(Map.Entry<String, Set<String>> depends : dependency.entrySet()){
				String rootName = depends.getKey();
				Node rootNode = nodes.get(rootName);

				if(depends.getValue().contains(superName) && !subNames.contains(rootName)){
					for(Node subNode : subNodes){
						rootNode.addChild(subNode);
						roots.remove(subNode.getData());
					}
				}
			}
		}
	}

	private Set<Node> simplifyGraphByFirstChildren(Set<String> roots, Map<String, Node> nodes, int numRoots){
		Set<String> childNames = new HashSet<String>();
		for(String rootName : roots){
			Node rootNode = nodes.get(rootName);
			for(Node childNode : rootNode.getChildren()){
				String childName = childNode.getData();
				childNames.add(childName);
			}
		}
		List<String> childIndices = new ArrayList<String>(childNames);
		int dimension = childIndices.size();
		List<String> rootIndices = new ArrayList<String>(roots);
		int num = rootIndices.size();
		List<List<Double>> points = new ArrayList<List<Double>>(num);
		for(int i=0;i<num;i++){
			String rootName = rootIndices.get(i);
			List<Double> temp = new ArrayList<Double>(dimension);
			for(int j=0;j<dimension;j++){
				temp.add(0.0);
			}
			Node rootNode = nodes.get(rootName);
			for(Node childNode : rootNode.getChildren()){
				String childName = childNode.getData();
				int j = childIndices.indexOf(childName);
				temp.set(j, 1.0);
			}
			points.add(temp);
		}
		
		List<Integer> assigns = new Kmeans().start(points, numRoots);
		
		List<Node> ret = new ArrayList<Node>();
		for(int i=0; i<numRoots; i++){
			ret.add(new Node("DUMMY_ROOT_" + i));
		}
		for(int i=0;i<assigns.size();i++){
			int assign = assigns.get(i);
			ret.get(assign).addChild(nodes.get(rootIndices.get(i)));
		}
		return new HashSet<Node>(ret);	
	}
	
	private Set<String> getChildName(Node root, int depth){
		Set<String> ret = new HashSet<String>();
		if(depth==0){
			return ret;
		}
		for(Node child : root.getChildren()){
			ret.add(child.getData());
			ret.addAll(getChildName(child, depth-1));
		}
		return ret;
	}

	private Set<Node> simplifyGraphByNthChildren(Set<String> roots, Map<String, Node> nodes, int numRoots, int depth){
		Set<String> childNames = new HashSet<String>();
		for(String rootName : roots){
			Node rootNode = nodes.get(rootName);
			childNames.addAll(this.getChildName(rootNode, depth));
		}
		
		List<String> childIndices = new ArrayList<String>(childNames);
		int dimension = childIndices.size();
		System.out.println("dimension: " + dimension);
		List<String> rootIndices = new ArrayList<String>(roots);
		int num = rootIndices.size();
		List<List<Double>> points = new ArrayList<List<Double>>(num);
		for(int i=0;i<num;i++){
			String rootName = rootIndices.get(i);
			List<Double> temp = new ArrayList<Double>(dimension);
			for(int j=0;j<dimension;j++){
				temp.add(0.0);
			}
			Node rootNode = nodes.get(rootName);
			for(String childName : this.getChildName(rootNode, depth)){
				int j = childIndices.indexOf(childName);
				temp.set(j, 1.0);
			}
			points.add(temp);
		}
		
		List<Integer> assigns = new Kmeans().start(points, numRoots);

		List<Node> ret = new ArrayList<Node>();
		for(int i=0; i<numRoots; i++){
			ret.add(new Node("DUMMY_ROOT_" + i));
		}
		for(int i=0;i<assigns.size();i++){
			int assign = assigns.get(i);
			ret.get(assign).addChild(nodes.get(rootIndices.get(i)));
		}
		return new HashSet<Node>(ret);	
	}
	
	
	public Set<Node> makeGraph(Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs, int numRoots){
		Map<String, Node> nodes = convertToNode(dependency);
		Set<String> roots = new HashSet<String>(dependency.keySet());
		addRelation(dependency, nodes, roots);
		addNonMySuperClass(dependency, super2subs, nodes, roots);
		
		Set<Node> rootNodes = new HashSet<Node>();
		System.err.println("NUM OF ROOT NODE: " + roots.size());
		for(String root : roots){
			rootNodes.add(nodes.get(root));
		}
		
		Graph<String> g = new Graph<String>(nodes.size());
		for(Map.Entry<String, Node> e : nodes.entrySet()){
			String srcName = e.getKey();
			Node srcNode = e.getValue();
			for(Node child : srcNode.getChildren()){
				g.addEdge(srcName, child.getData());
			}
		}
		
		for(String root : roots){
			if(! nodes.containsKey(root)){
				System.err.println("ERROR ERROR");
			}
		}
	    // System.out.println(g.toStringIndex());
		Graph<Set<String>> simpleGraph = new Simplifier<String>().simplify(g, roots);
		// System.out.println(simpleGraph.toStringIndex());
		return rootNodes;
		// return this.simplifyGraphByFirstChildren(roots, nodes, numRoots);
		// return this.simplifyGraphByNthChildren(roots, nodes, numRoots, 3);
	}	
}
