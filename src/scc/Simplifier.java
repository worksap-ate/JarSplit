package scc;
import java.util.*;

public class Simplifier<T> {
    public Graph<Set<T>> simplify(Graph<T> graph){
        Set<Set<T>> sccs = new SCC<T>().stronglyConnectedComponents(graph);
        Map<T, Set<T>> belongingComponent = new HashMap<T, Set<T>>();
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
    
    public Graph<Set<T>> simplify(Graph<T> graph, Collection<T> roots){
        Set<Set<T>> sccs = new SCC<T>().stronglyConnectedComponents(graph, roots);
        Map<T, Set<T>> belongingComponent = new HashMap<T, Set<T>>();
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
}
