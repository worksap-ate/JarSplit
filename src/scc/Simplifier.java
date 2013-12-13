package scc;
import java.util.*;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

public class Simplifier<T> {
    public Graph<Set<T>> simplify(Graph<T> graph){
        return this.simplify(graph, graph.getVertexes());
    }

    public Graph<Set<T>> simplify(Graph<T> graph, Collection<T> roots){
        Set<Set<T>> sccs = new SCC<T>().stronglyConnectedComponents(graph, roots);
        Map<T, Set<T>> belongingComponent = new THashMap<T, Set<T>>();
        for(Set<T> scc : sccs){
            for(T vertex : scc){
                belongingComponent.put(vertex, scc);
            }
        }

        Graph<Set<T>> ret = new Graph<Set<T>>();
        for(Set<T> scc : sccs){
            for(T vertex : scc){
                for(T w : graph.getAdjacentVertexes(vertex)){
                    ret.addEdge(scc, belongingComponent.get(w));
                }
            }
        }
        return ret;
    }
 
    
    private <S> Map<Integer, Set<Integer>> calcPre(Graph<S> graph){
    	Map<Integer, Set<Integer>> pre = new THashMap<Integer, Set<Integer>>();
    	for(int v=0;v<graph.getVertexes().size();v++){
    		for(int w : graph.getAdjacentVertexIndices(v)){
    			if(!pre.containsKey(w)){
    				pre.put(w, new THashSet<Integer>());
    			}
    			pre.get(w).add(v);
    		}
    	}
    	return pre;
    }
    
    private Graph<Set<T>> sim(Graph<Set<T>> graph){
    	Map<Integer, List<Integer>> simulationNodes = new THashMap<Integer, List<Integer>>();
    	Map<Integer, Set<Integer>> pre = calcPre(graph);
    	for(int v=0;v<graph.getVertexes().size();v++){
    		for(int w=v+1;w<graph.getVertexes().size();w++){
    			boolean postSame = graph.getAdjacentVertexIndices(v).equals(graph.getAdjacentVertexIndices(w));
    			boolean preSame = (!pre.containsKey(v) && !pre.containsKey(w)) || (pre.containsKey(v) && pre.containsKey(w) && pre.get(v).equals(pre.get(w)));
    			if(postSame && preSame){
    				if(!simulationNodes.containsKey(v)){
    					simulationNodes.put(v, new ArrayList<Integer>());
    				}
    				simulationNodes.get(v).add(v);
    			}
    		}
    	}
    	
    	if(simulationNodes.isEmpty()){
    		return graph;
    	}

    	Set<Integer> duplicatedNodes = new THashSet<Integer>();
    	for(List<Integer> v : simulationNodes.values()){
    		duplicatedNodes.addAll(v);
    	}

    	Graph<Set<T>> g = new Graph<Set<T>>();
    	for(int v=0;v<graph.getVertexes().size();v++){
    		if(duplicatedNodes.contains(v)){
    			continue;
    		}
    		Set<T> src = new THashSet<T>();
    		src.addAll(graph.fromIndex(v));
    		if(simulationNodes.containsKey(v)){
    			for(int d : simulationNodes.get(v)){
    				src.addAll(graph.fromIndex(d));
    			}
    		}
    		for(int w : graph.getAdjacentVertexIndices(v)){
    			if(duplicatedNodes.contains(w)){
    				continue;
    			}
    			Set<T> dst = new THashSet<T>();
    			dst.addAll(graph.fromIndex(w));
    			if(simulationNodes.containsKey(w)){
    				for(int d : simulationNodes.get(w)){
    					dst.addAll(graph.fromIndex(d));
    				}
    			}
    			g.addEdge(src, dst);
    		}
    	}
    	return sim(g);
    }
}
