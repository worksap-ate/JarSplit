package scc;
import java.util.*;
import java.io.PrintStream;

import gnu.trove.set.hash.THashSet;
import gnu.trove.map.hash.THashMap; 

public class Graph<T> {
     /**
     * fromVertex -> [toVertex1, ..., toVertexN]
     */
    private Map<T, Collection<T>> edges;

    public Graph(){
        this.edges = new THashMap<T, Collection<T>>();
    }

    public Collection<T> getVertexes(){
        return this.edges.keySet();
    }

    public Collection<T> getAdjacentVertexes(T vertex){
        if(this.edges.containsKey(vertex)){
            return this.edges.get(vertex);
        }else{
            return new THashSet<T>();
        }
    }

    public void addEdge(T src, T dst){
        if(!this.edges.containsKey(src)){
            this.edges.put(src, new THashSet<T>());
        }
        this.edges.get(src).add(dst);
    }

    public void show(){
        this.show(System.out);
    }

    public void show(PrintStream out){
        out.println("[");
        for(Map.Entry<T, Collection<T>> entry : this.edges.entrySet()){
            out.println("    " + entry.getKey() + " -> " + entry.getValue() + ",");
        }
        out.println("]");
    }
}
