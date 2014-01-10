package split;

import java.util.*;

import kmeans.Kmeans;
import scc.*;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import edu.princeton.cs.algs4.directed.*;


public class Spliter {
	private void addSpecialEdge(String ownerName, String parentName, Map<String, Set<String>> super2subs, Set<String> work){
		if(work.add(parentName)){
			if(super2subs.containsKey(parentName)){
				for(String childName : super2subs.get(parentName)){
					addSpecialEdge(ownerName, childName, super2subs, work);
				}
			}
		}
	}

	private Map<String, Integer> createMappings(Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs){
		Map<String, Integer> mappings = new THashMap<String, Integer>();		
		int count = 0;
		for(Map.Entry<String, Set<String>> e : dependency.entrySet()){
			String key = e.getKey();
			if(!mappings.containsKey(key)){
				mappings.put(key, count);
				count++;
			}
			for(String value : e.getValue()){
				if(!mappings.containsKey(value)){
					mappings.put(value, count);
					count++;
				}
			}
		}
		for(Map.Entry<String, Set<String>> e : super2subs.entrySet()){
			String key = e.getKey();
			if(!mappings.containsKey(key)){
				mappings.put(key, count);
				count++;
			}
			for(String value : e.getValue()){
				if(!mappings.containsKey(value)){
					mappings.put(value, count);
					count++;
				}
			}
		}
		
		return mappings;
	}
	
	private Digraph createGraph(Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs, Map<String, Integer> mappings){
		Digraph graph = new Digraph(mappings.size());
		// addEdge owner -> instance
		for(Map.Entry<String, Set<String>> e : dependency.entrySet()){
			String ownerName = e.getKey();
			for(String instanceName : e.getValue()){
				graph.addEdge(mappings.get(ownerName), mappings.get(instanceName));
			}
		}

		// addEdge child -> parent
		for(Map.Entry<String, Set<String>> e : super2subs.entrySet()){
			String superName = e.getKey();
			for(String childName : e.getValue()){
				graph.addEdge(mappings.get(childName), mappings.get(superName));
			}
		}

		// addEdge owner -> instance -> child ( -> child)*
		for(Map.Entry<String, Set<String>> e : dependency.entrySet()){
			String ownerName = e.getKey();
			Set<String> work = new THashSet<String>();
			for(String instanceName : e.getValue()){
				addSpecialEdge(ownerName, instanceName, super2subs, work);
			}
			for(String n : work){
				if(!n.equals(ownerName)){
					graph.addEdge(mappings.get(ownerName), mappings.get(n));
				}
			}
		}
		
		return graph;
	}
	
    private Graph<Set<String>> simplify(KosarajuSharirSCC scc, Digraph graph, Map<String, Integer> mappings){
    	int M = scc.count();
    	List<Set<String>> components = new ArrayList<Set<String>>(M);
        for(int i=0;i<M;i++) {
            components.add(new THashSet<String>());
        }
        for(Map.Entry<String, Integer> e : mappings.entrySet()){
        	components.get(scc.id(e.getValue())).add(e.getKey());
        }
        
    	Map<String, Set<String>> belongingComponent = new THashMap<String, Set<String>>();
    	for(Set<String> component : components){
            for(String vertex : component){
                belongingComponent.put(vertex, component);
            }
        }
    	
    	Map<Integer, String> revMappings = new THashMap<Integer, String>(mappings.size());
    	for(Map.Entry<String, Integer> e : mappings.entrySet()){
    		revMappings.put(e.getValue(), e.getKey());
    	}
    	Graph<Set<String>> ret = new Graph<Set<String>>();
    	for(Set<String> component : components){
            for(String vertex : component){
                for(int w : graph.adj(mappings.get(vertex))){
                	if(scc.id(w) != scc.id(mappings.get(vertex))){
                		ret.addEdge(component, belongingComponent.get(revMappings.get(w)));
                	}
                }
            }
        }
    	return ret;
    }
	
	public List<Set<String>> split(Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs, int numRoots){
		Map<String, Integer> mappings = createMappings(dependency, super2subs);
		Digraph graph = createGraph(dependency, super2subs, mappings);
		KosarajuSharirSCC scc = new KosarajuSharirSCC(graph);		
		Graph<Set<String>> simpleGraph = simplify(scc, graph, mappings);
		Set<Set<String>> simpleRoots = simpleGraph.getRoots();

		for(Set<String> r : simpleRoots){
			for(String rr : r){
				System.out.println("ROOT " + rr);
			}
		}
		System.err.println(simpleRoots.size());
		
		//---
		Set<Integer> rootIndices = simpleGraph.getRootIndices();
		List<Set<Integer>> attrs = new DepthFirstSearch<Set<String>>().depthfirstsearchs(simpleGraph, rootIndices);
		for(int v : rootIndices){
			System.out.println(v + " : " + attrs.get(v));
		}
		
		if(rootIndices.size() <= numRoots){
			List<Set<String>> ret = new ArrayList<Set<String>>(rootIndices.size());
			for(int v : rootIndices){
				Set<String> temp = new THashSet<String>(attrs.get(v).size());
				for(int u : attrs.get(v)){
					temp.addAll(simpleGraph.fromIndex(u));
				}
				ret.add(temp);
			}
			return ret;
		}
		
		int size = simpleGraph.getVertexes().size();
		List<List<Double>> points = new ArrayList<List<Double>>(rootIndices.size());
		Map<Integer, Integer> association = new THashMap<Integer, Integer>();
		int c = 0;
		for(int v : rootIndices){
			List<Double> point = new ArrayList<Double>(size);
			for(int i=0;i<size;i++){
				if(attrs.get(v).contains(i)){
					point.add(1.0);
				}else{
					point.add(0.0);
				}
			}
			points.add(point);
			association.put(c, v);
			c++;
			System.out.println(point);
		}
		List<Integer> assigns = new Kmeans().start(points, numRoots);
		System.out.println(assigns);
		List<Set<String>> ret = new ArrayList<Set<String>>(numRoots);
		for(int i=0;i<numRoots;i++){
			ret.add(new THashSet<String>());
		}
		for(int i=0;i<assigns.size();i++){
			int assign = assigns.get(i);
			int v = association.get(i);
			for(Integer u : attrs.get(v)){
				ret.get(assign).addAll(simpleGraph.fromIndex(u));
			}
		}
		return ret;
		//---
	}	
}
