package com.github.cloverrose.jarsplit.split;
import gnu.trove.set.hash.THashSet;

import java.util.*;
import com.github.cloverrose.jarsplit.scc.*;

public class DepthFirstSearch<T> {
	private void depthfirstsearch(Graph<T> graph, int v, List<Set<Integer>> attrs){
		for(int u : graph.getAdjacentVertexIndices(v)){
			depthfirstsearch(graph, u, attrs);
			attrs.get(v).addAll(attrs.get(u));
		}
		attrs.get(v).add(v);
	}
	
	public List<Set<Integer>> depthfirstsearchs(Graph<T> graph, Set<Integer> roots){
		List<Set<Integer>> attrs = new ArrayList<Set<Integer>>(graph.getVertexes().size());
		for(int i=0;i<graph.getVertexes().size();i++){
			attrs.add(new THashSet<Integer>());
		}
		for(int v : roots){
			depthfirstsearch(graph, v, attrs);
		}
		return attrs;
	}
}
