package com.github.cloverrose.jarsplit.split;

import java.util.*;

import com.github.cloverrose.jarsplit.kmeans.Kmeans;
import com.github.cloverrose.jarsplit.scc.*;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

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
	
	private void addDummyNode(Graph<String> graph, int numRoots){
		Set<String> roots = graph.getRoots();
		int n = roots.size() / numRoots;
		int c = 0;
		int i = 0;
		for(String root : roots){
			graph.addEdge("DUMMY" + c, root);
			i++;
			if(i == n){
				c++;
				i=0;
			}
		}
	}
	

	private void simplifyGraphByFirstChildren(Graph<String> graph, int numRoots){
		Set<Integer> _roots = graph.getRootIndices();
		Map<Integer, Integer> _children = new THashMap<Integer, Integer>();
		for(Integer vertex : _roots){
			for(Integer child : graph.getAdjacentVertexIndices(vertex)){
				if(_children.containsKey(child)){
					_children.put(child, _children.get(child) + 1);
				}else{
					_children.put(child, 1);
				}
			}
		}
		List<Map.Entry<Integer,Integer>> entries = new ArrayList<Map.Entry<Integer, Integer>>(_children.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<Integer, Integer>>() {
			@Override
			public int compare(
					Map.Entry<Integer,Integer> entry1, Map.Entry<Integer,Integer> entry2) {
	                	return ((Integer)entry2.getValue()).compareTo((Integer)entry1.getValue());
	            	}
		});
	    List<Integer> children = new ArrayList<Integer>(100);
	    int cc = 0;
	    for(Map.Entry<Integer, Integer> e : entries){
	    	if(e.getValue() == 1){
	    		break;
	    	}
	    	children.add(e.getKey());
	    	cc++;
	    	if(cc == 100){
	    		break;
	    	}
	    }
		List<Integer> roots = new ArrayList<Integer>(_roots);
		// List<Integer> children = new ArrayList<Integer>(_children);
		int dimension = children.size();
		int num = roots.size();
		System.out.println("Dimension = " + dimension);
		System.out.println("Num = " + num);		
		List<List<Double>> points = new ArrayList<List<Double>>(num);
		for(int i=0;i<num;i++){
			if(i % 1000 == 0){
				System.out.println(i);
			}
			List<Double> point = new ArrayList<Double>(dimension);
			for(int j=0;j<dimension;j++){
				point.add(0.0);
			}
			for(Integer child : graph.getAdjacentVertexIndices(roots.get(i))){
				int j = children.indexOf(child);
				if(j != -1){
					point.set(j, 1.0);
				}
			}
			points.add(point);
		}
		
		List<Integer> assigns = new Kmeans().start(points, numRoots);

		
		for(int i=0; i<numRoots; i++){
			for(int j=0;j<assigns.size();j++){
				int assign = assigns.get(j);
				if(assign == i){
					graph.addEdge("DUMMY" + i, graph.fromIndex(roots.get(j)));
				}
			}
		}
	}


	
	
	public List<Set<String>> split(Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs, int numRoots){
		// addEdge owner -> instance
		Graph<String> graph = new Graph<String>();
		for(Map.Entry<String, Set<String>> e : dependency.entrySet()){
			String ownerName = e.getKey();
			graph.addEdge(ownerName, ownerName);
			for(String instanceName : e.getValue()){
				graph.addEdge(ownerName, instanceName);
			}
		}

		// addEdge child -> parent
		for(Map.Entry<String, Set<String>> e : super2subs.entrySet()){
			String parentName = e.getKey();
			for(String childName : e.getValue()){
				graph.addEdge(childName, parentName);
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
				graph.addEdge(ownerName, n);
			}
		}

		System.err.println("First Graph");
		// System.err.println(graph.toStringIndex());
		Set<String> roots = graph.getRoots();
		System.out.println(roots.size());
		System.out.println("VERTEX NUM " + graph.getVertexes().size());
		
		// simplifyGraphByFirstChildren(graph, numRoots);
		addDummyNode(graph, numRoots);
		List<Set<String>> ret = new DFS().dfs(graph, graph.getRoots());
		
//		Graph<Set<String>> simpleGraph = new Simplifier<String>().simplify(graph, roots);
//		
//		System.err.println("\nFinal Graph");
//		System.out.println(simpleGraph.toStringIndex());
//		Set<Set<String>> simpleRoots = simpleGraph.getRoots();
//		System.err.println(simpleRoots.size());
		
		//---
//		Set<Integer> rootIndices = simpleGraph.getRootIndices();
//		List<Set<Integer>> attrs = new DepthFirstSearch<Set<String>>().depthfirstsearchs(simpleGraph, rootIndices);
////		
//		Set<Integer> rootIndices = graph.getRootIndices();
//		List<Set<Integer>> attrs = new DepthFirstSearch<String>().depthfirstsearchs(graph, rootIndices);
//		
//		for(int v : rootIndices){
//			System.out.println(v + " : " + attrs.get(v));
//		}
		
//		if(rootIndices.size() <= numRoots){
//			List<Set<String>> ret = new ArrayList<Set<String>>(rootIndices.size());
//			for(int v : rootIndices){
//				Set<String> temp = new THashSet<String>(attrs.get(v).size());
//				for(int u : attrs.get(v)){
//					temp.addAll(simpleGraph.fromIndex(u));
//				}
//				ret.add(temp);
//			}
//			return ret;
//		}
		
//		int size = simpleGraph.getVertexes().size();
//		List<List<Double>> points = new ArrayList<List<Double>>(rootIndices.size());
//		Map<Integer, Integer> association = new THashMap<Integer, Integer>();
//		int c = 0;
//		for(int v : rootIndices){
//			List<Double> point = new ArrayList<Double>(size);
//			for(int i=0;i<size;i++){
//				if(attrs.get(v).contains(i)){
//					point.add(1.0);
//				}else{
//					point.add(0.0);
//				}
//			}
//			points.add(point);
//			association.put(c, v);
//			c++;
//			// System.out.println(point);
//		}
//		List<Integer> assigns = new Kmeans().start(points, numRoots);
//		System.out.println(assigns);
//		List<Set<String>> ret = new ArrayList<Set<String>>(numRoots);
//		for(int i=0;i<numRoots;i++){
//			ret.add(new THashSet<String>());
//		}
//		for(int i=0;i<assigns.size();i++){
//			int assign = assigns.get(i);
//			int v = association.get(i);
//			for(Integer u : attrs.get(v)){
//				ret.get(assign).addAll(simpleGraph.fromIndex(u));
//			}
//		}
		return ret;
		//---
	}
}
