package split;

import java.util.*;
import scc.*;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

public class CopyOfGraphMaker {
	private void addSpecialEdge(String ownerName, String parentName, Map<String, Set<String>> super2subs, Set<String> work){
		if(work.add(parentName)){
			if(super2subs.containsKey(parentName)){
				for(String childName : super2subs.get(parentName)){
					addSpecialEdge(ownerName, childName, super2subs, work);
				}
			}
		}
	}

	public Set<Node> makeGraph(Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs, int numRoots){
		// make isA
		Map<String, Set<String>> isA = new THashMap<String, Set<String>>();
		for(Map.Entry<String, Set<String>> e : super2subs.entrySet()){
			String superName = e.getKey();
			for(String childName : e.getValue()){
				if(!isA.containsKey(childName)){
					isA.put(childName, new THashSet<String>());
				}
				isA.get(childName).add(superName);
			}
		}

		// make hasA
		Map<String, Set<String>> hasA = new THashMap<String, Set<String>>();
		for(Map.Entry<String, Set<String>> e : dependency.entrySet()){
			String ownerName = e.getKey();
			for(String instanceName : e.getValue()){
				if(!isA.containsKey(ownerName) || !isA.get(ownerName).contains(instanceName)){
					if(!hasA.containsKey(ownerName)){
						hasA.put(ownerName, new THashSet<String>());
					}
					hasA.get(ownerName).add(instanceName);
				}
			}
		}
		
//		Set<String> roots = new HashSet<String>();
//		roots.addAll(hasA.keySet());
//		roots.addAll(isA.keySet());
//
		// addEdge owner -> instance
		Graph<String> graph = new Graph<String>();
		for(Map.Entry<String, Set<String>> e : hasA.entrySet()){
			String ownerName = e.getKey();
			for(String instanceName : e.getValue()){
				graph.addEdge(ownerName, instanceName);
				// roots.remove(instanceName);
			}
		}

		// addEdge child -> parent
		for(Map.Entry<String, Set<String>> e : isA.entrySet()){
			String childName = e.getKey();
			for(String parentName : e.getValue()){
				graph.addEdge(childName, parentName);
				// roots.remove(parentName);
			}
		}

		// addEdge owner -> instance -> child ( -> child)*
		for(Map.Entry<String, Set<String>> e : hasA.entrySet()){
			String ownerName = e.getKey();
			Set<String> work = new THashSet<String>();
			for(String instanceName : e.getValue()){
				addSpecialEdge(ownerName, instanceName, super2subs, work);
			}
			for(String n : work){
				if(!n.equals(ownerName)){
					graph.addEdge(ownerName, n);
					// roots.remove(n);
				}
			}
		}

		System.err.println("First Graph");
		System.err.println(graph.toStringIndex());
		Set<String> roots = graph.getRoots();
		System.out.println(roots.size());
		
		Graph<Set<String>> simpleGraph = new Simplifier<String>().simplify(graph, roots);
		
		System.err.println("\nFinal Graph");
		System.out.println(simpleGraph.toStringIndex());
		Set<Set<String>> simpleRoots = simpleGraph.getRoots();
		System.err.println(simpleRoots.size());
		
		//---
		Set<Integer> rootIndices = new THashSet<Integer>();
		for(Set<String> rootName : simpleGraph.getRoots()){
			rootIndices.add(simpleGraph.toIndex(rootName));
		}
		List<Set<Integer>> attrs = new CopyOfSpliter<Set<String>>().depthfirstsearchs(simpleGraph, rootIndices);
		for(int v=0;v<attrs.size();v++){
			System.out.println(v + " : " + attrs.get(v));
		}
		//---
		
		

		return null;
	}
}
