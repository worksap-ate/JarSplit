package split;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Spliter {
	public List<Set<String>> start(Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs){
		GraphMaker graphMaker = new GraphMaker();
		Set<Node> rootNodes = graphMaker.makeGraph(dependency, super2subs);
		
		
		// stat(rootNodes);
		
		return depthfirstsearchs(rootNodes);
		// count(rootNodes);
		// return new ArrayList<Set<String>>();
		
	}
	
	public List<Set<String>> _start(Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs){
		CopyOfCopyOfGraphMaker graphMaker = new CopyOfCopyOfGraphMaker();
		Set<Node> rootNodes = graphMaker.makeGraph(dependency, super2subs);
		
		
		// stat(rootNodes);
		
		// return depthfirstsearchs(rootNodes);
		// count(rootNodes);
		return new ArrayList<Set<String>>();
		
	}

	private void depthiter(Node rootNode, Set<Node> work){
		if(work.add(rootNode)){
			for(Node child : rootNode.getChildren()){
				depthiter(child, work);
			}
		}
	}

	private Set<Node> depthfirstsearch(Node rootNode){
		System.out.println("start " + rootNode.getData());
		Set<Node> work = new HashSet<Node>();
		depthiter(rootNode, work);
		System.out.println(work.size());
		return work;
	}
	
	private List<Set<String>> depthfirstsearchs(Set<Node> rootNodes){
		List<Set<String>> ret = new ArrayList<Set<String>>();
		for(Node rootNode : rootNodes){
			Set<String> temp = new HashSet<String>();
			ret.add(temp);
			for(Node node : depthfirstsearch(rootNode)){
				temp.add(node.getData());
			}
		}
		for(Set<String> s : ret){
			System.out.println("group: " + s);
		}
		return ret;
	}
	
	
	// --- below code may be useful for optimization.
	
	
	private double calcJaccard(Set<Node> X, Set<Node> Y){
		Set<Node> union = new HashSet<Node>();
		union.addAll(X);
		union.addAll(Y);
		Set<Node> intersection = new HashSet<Node>();
		intersection.addAll(X);
		intersection.retainAll(Y);
		return ((double)intersection.size()) / union.size();
	}

	private void stat(Set<Node> rootNodes){
		Map<Integer, Node> indexedRootNodes = new HashMap<Integer, Node>();
		Map<Integer, Set<Node>> indexedChildNodesOfRoot = new HashMap<Integer, Set<Node>>();
		int idx = 0;
		for(Node rootNode : rootNodes){
			indexedRootNodes.put(idx, rootNode);
			indexedChildNodesOfRoot.put(idx, new HashSet<Node>(rootNode.getChildren()));
			idx++;
		}

		
		int size = indexedChildNodesOfRoot.size();
		Map<Integer, Set<Integer>> similars = new HashMap<Integer, Set<Integer>>();
		for(int i=0;i<size;i++){
			Set<Integer> temp = new HashSet<Integer>();
			temp.add(i);
			similars.put(i, temp);
		}

		
		for(int i=0; i<size; i++){
			for(int j=i; j<size; j++){
				double sim = calcJaccard(indexedChildNodesOfRoot.get(i), indexedChildNodesOfRoot.get(j));
				System.out.println(i + " x"  + j + " : " + sim);
				if(sim >= 0.9){
					Set<Integer> uniongroup = similars.get(i);
					for(int jj : similars.get(j)){
						uniongroup.addAll(similars.get(jj));
						similars.put(jj, uniongroup);
					}
				}
			}
		}
		
		Set<Set<Integer>> groups = new HashSet<Set<Integer>>();
		groups.addAll(similars.values());
		System.out.println("similars group size : " + groups.size());
		
	}
	
	private Map<Node, Integer> count(Set<Node> rootNodes){
		Map<Node, Integer> counter = new HashMap<Node, Integer>();
		for(Node rootNode : rootNodes){
			int c = counter.containsKey(rootNode) ? counter.get(rootNode) : 0;
			counter.put(rootNode, c + 1);
			for(Node childNode : rootNode.getChildren()){
				c = counter.containsKey(childNode) ? counter.get(childNode) : 0;
				counter.put(childNode, c + 1);
			}
		}
		for(Map.Entry<Node, Integer> entry : counter.entrySet()){
			System.out.println(entry.getKey().getData() + " -> "  + entry.getValue());
		}
		return counter;
	}
	
}
