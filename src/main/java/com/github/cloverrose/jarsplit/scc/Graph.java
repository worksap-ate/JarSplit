package com.github.cloverrose.jarsplit.scc;
import java.util.*;
import gnu.trove.set.hash.THashSet;

public class Graph<T> {
     /**
     * fromVertex -> [toVertex1, ..., toVertexN]
     */
    private ArrayList<Set<Integer>> edges;
    private ArrayList<T> relations;

    public Graph(){
        this.edges = new ArrayList<Set<Integer>>();
        this.relations = new ArrayList<T>();
    }

    public Graph(int size){
        this.edges = new ArrayList<Set<Integer>>(size);
        this.relations = new ArrayList<T>(size);
    }

    public Collection<T> getVertexes(){
        return this.relations;
    }

    public int toIndex(T vertex){
        return this.relations.indexOf(vertex);
    }

    public T fromIndex(int index){
        return this.relations.get(index);
    }

    public Collection<Integer> getAdjacentVertexIndices(Integer vertex){
        return this.edges.get(vertex);
    }

    public Collection<T> getAdjacentVertexes(T vertex){
        Collection<Integer> dstIndices = this.edges.get(this.toIndex(vertex));
        Collection<T> ret = new ArrayList<T>(dstIndices.size());
        for(int dstIndex : dstIndices){
            ret.add(this.fromIndex(dstIndex));
        }
        return ret;
    }

    public void addEdge(T src, T dst){
        if(!this.relations.contains(src)){
            this.relations.add(src);
            this.edges.add(new THashSet<Integer>());
        }
        if(!this.relations.contains(dst)){
            this.relations.add(dst);
            this.edges.add(new THashSet<Integer>());
        }
        this.edges.get(this.relations.indexOf(src)).add(this.relations.indexOf(dst));
    }

    public Set<Integer> getRootIndices(){
        Set<Integer> ret = new THashSet<Integer>(this.getVertexes().size());
        for(int v=0;v<this.getVertexes().size();v++){
            ret.add(v);
        }
        for(int v=0;v<this.getVertexes().size();v++){
            for(int w : this.getAdjacentVertexIndices(v)){
                if(w != v){
                    ret.remove(w);
                }
            }
        }
        return ret;
    }

    public Set<T> getRoots(){
        Set<Integer> ret = getRootIndices();
        Set<T> ret_ = new THashSet<T>(ret.size());
        for(int v : ret){
            ret_.add(this.fromIndex(v));
        }
        return ret_;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for(int srcIndex=0; srcIndex<this.edges.size(); srcIndex++){
            T src = this.relations.get(srcIndex);
            Collection<T> dsts = new ArrayList<T>(this.edges.get(srcIndex).size());
            for(int dstIndex : this.edges.get(srcIndex)){
                T dst = this.relations.get(dstIndex);
                dsts.add(dst);
            }
            sb.append("    " + src + " -> " + dsts + ",\n");
        }
        sb.append("]");
        return sb.toString();
    }

    public String toStringIndex(){
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for(int srcIndex=0; srcIndex<this.edges.size(); srcIndex++){
            sb.append("    " + srcIndex + " -> " + this.edges.get(srcIndex) + ",\n");
        }
        sb.append("]");
        return sb.toString();
    }
}
