package com.github.cloverrose.jarsplit.scc;
import java.util.*;
import gnu.trove.map.hash.THashMap;

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
                    Set<T> target = belongingComponent.get(w);
                    if(!target.equals(scc)){
                        ret.addEdge(scc, target);
                    }
                }
            }
        }
        return ret;
    }
}
