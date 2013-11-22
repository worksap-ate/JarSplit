package split;
import java.util.*;

public class Spliter {
	public List<Set<String>> start(Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs, int numRoots){
		Set<Node> rootNodes = new GraphMaker().makeGraph(dependency, super2subs, numRoots);
		return depthfirstsearchs(rootNodes);
	}

	private void depthiter(Node rootNode, Set<Node> work){
		if(work.add(rootNode)){
			for(Node child : rootNode.getChildren()){
				depthiter(child, work);
			}
		}
	}

	public Set<Node> depthfirstsearch(Node rootNode){
		long start = System.currentTimeMillis();

		System.out.println("start " + rootNode.getData());
		Set<Node> work = new HashSet<Node>();
		depthiter(rootNode, work);
		System.out.println(work.size());
		long end = System.currentTimeMillis();
		System.err.println("depth first search for 1 node time: " + (end - start) + "[ms]");
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
		return ret;
	}
}
