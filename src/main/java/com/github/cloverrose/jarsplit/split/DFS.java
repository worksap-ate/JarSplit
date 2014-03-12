package com.github.cloverrose.jarsplit.split;

import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.cloverrose.jarsplit.scc.Graph;

public class DFS {
	

 	private void depthiter(Graph<String> graph, String vertex, Set<String> work){
 		if(work.add(vertex)){
 			for(String u : graph.getAdjacentVertexes(vertex)){
 				depthiter(graph, u, work);
 			}
 		}
 	}
	

	public Set<String> depthfirstsearch(Graph<String> graph, String root){
 		Set<String> work = new THashSet<String>();
 		depthiter(graph, root, work);
 		return work;
 	}
	
	
	public List<Set<String>> dfs(Graph<String> graph, Set<String> roots){
		List<Set<String>> ret = new ArrayList<Set<String>>();
		for(String root : roots){
			ret.add(depthfirstsearch(graph, root));

		}
		return ret;		
	}

}
