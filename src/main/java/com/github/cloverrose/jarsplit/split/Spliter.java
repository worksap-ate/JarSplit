package com.github.cloverrose.jarsplit.split;

import java.util.*;

import com.github.cloverrose.jarsplit.scc.*;
import com.github.cloverrose.jarsplit.partition.*;

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

	/**
	 * 依存関係graphを簡単に解析して、DUMMYノードを追加して
	 * root要素をそのDUMMYノードの子要素にする。
	 * 
	 * 簡単に解析とは：root要素とその直接の子要素のみを見て、
	 * 子要素が似ているroot要素同士を同じクラスタに分類する
	 * @param graph
	 * @param numRoots
	 */
	private void partition(Graph<String> graph, int numRoots){
		Set<Integer> _roots = graph.getRootIndices();
		List<Integer> roots = new ArrayList<Integer>(_roots);
	    int num = roots.size();
		
	    Map<Integer, Integer> _children = new THashMap<Integer, Integer>();
		for(Integer vertex : _roots){
			for(Integer child : graph.getAdjacentVertexIndices(vertex)){
				_children.put(child, _children.containsKey(child) ? _children.get(child) + 1 : 1);
			}
		}
		// 全てのrootに参照されているものを取り除く
	    List<Integer> children = new ArrayList<Integer>(_children.size());
	    for(Map.Entry<Integer, Integer> e : _children.entrySet()){
			if(e.getValue() != num){
				children.add(e.getKey());
			}
		}

	    int dimension = children.size();
		System.out.println("Dimension = " + dimension);
		System.out.println("Num = " + num);		
		List<List<Integer>> points = new ArrayList<List<Integer>>(num);
		for(int i=0;i<num;i++){
			List<Integer> point = new ArrayList<Integer>(dimension);
			for(int j=0;j<dimension;j++){
				point.add(0);
			}
			for(Integer child : graph.getAdjacentVertexIndices(roots.get(i))){
				int j = children.indexOf(child);
				if(j != -1){
					point.set(j, 1);
				}
			}
			points.add(point);
		}
		
		List<Integer> assigns = new Partition().start(points, numRoots);
		System.out.println(assigns);

		// DUMMYノードを追加し、
		// Partitionで求めた分割に従って、DUMMYノードに割り振る
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
		Set<String> roots = graph.getRoots();
		System.out.println(roots.size());
		System.out.println("VERTEX NUM " + graph.getVertexes().size());
		
		partition(graph, numRoots);
		
		// DFSをしてDUMMYノードから到達可能な要素を全て計算する
		List<Set<String>> ret = new DFS().dfs(graph, graph.getRoots());
		
		return ret;
	}
}
