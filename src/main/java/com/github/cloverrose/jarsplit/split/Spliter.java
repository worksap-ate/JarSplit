package com.github.cloverrose.jarsplit.main;

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
		
		Graph<Set<String>> simpleGraph = new Simplifier<String>().simplify(graph, roots);
		
		System.err.println("\nFinal Graph");
		System.out.println(simpleGraph.toStringIndex());
		Set<Set<String>> simpleRoots = simpleGraph.getRoots();
		System.err.println(simpleRoots.size());
		
		//---
		Set<Integer> rootIndices = simpleGraph.getRootIndices();
		List<Set<Integer>> attrs = new DepthFirstSearch<Set<String>>().depthfirstsearchs(simpleGraph, rootIndices);
//		for(int v : rootIndices){
//			System.out.println(v + " : " + attrs.get(v));
//		}
		
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
			// System.out.println(point);
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
