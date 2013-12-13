package scc;
import java.util.*;
import gnu.trove.set.hash.THashSet;

public class SCC<T> {
    private class Counter{
        public int value;
        public Counter(int count){
            this.value = count;
        }
    }
    private static final int NULL = -1;
    private void visit(
            Graph<T> g, Integer vertex, Set<Set<Integer>> sccs,
            Stack<Integer> S, ArrayList<Integer> low, ArrayList<Integer> num, Counter counter){
        counter.value++;
        low.set(vertex, counter.value);
        num.set(vertex, counter.value);
        S.push(vertex);
        for(Integer w : g.getAdjacentVertexIndices(vertex)){
            for(Set<Integer> scc : sccs){
                if(scc.contains(w)){
                    continue;
                }
            }
            if(num.get(w) == NULL){
                visit(g, w, sccs, S, low, num, counter);
                low.set(vertex, Math.min(low.get(vertex), low.get(w)));
            }else if(S.contains(w)){
                low.set(vertex, Math.min(low.get(vertex), num.get(w)));
            }
        }
        if(low.get(vertex) == num.get(vertex)){
            Set<Integer> newScc = new THashSet<Integer>();
            while(true){
                Integer w = S.pop();
                newScc.add(w);
                if(w == vertex){
                    break;
                }
            }
            sccs.add(newScc);
        }
    }

    public Set<Set<Integer>> stronglyConnectedComponentsCore(Graph<T> g){
        return this.stronglyConnectedComponentsCore(g, g.getVertexes());
    }

    public Set<Set<Integer>> stronglyConnectedComponentsCore(Graph<T> g, Collection<T> roots){
        Collection<Integer> roots_ = new ArrayList<Integer>(roots.size());
        for(T root : roots){
            roots_.add(g.toIndex(root));
        }
        Set<Set<Integer>> sccs = new THashSet<Set<Integer>>();
        for(Integer v : roots_){
            for(Set<Integer> scc : sccs){
                if(scc.contains(v)){
                    continue;
                }
            }
            ArrayList<Integer> num = new ArrayList<Integer>(g.getVertexes().size());
            ArrayList<Integer> low = new ArrayList<Integer>(g.getVertexes().size());
            for(int i=0; i<g.getVertexes().size(); i++){
                num.add(NULL);
                low.add(NULL);
            }
            Stack<Integer> S = new Stack<Integer>();
            Counter counter = new Counter(0);
            visit(g, v, sccs, S, low, num, counter);
        }
        return sccs;
    }

    public Set<Set<T>> stronglyConnectedComponents(Graph<T> g){
        return this.stronglyConnectedComponents(g, g.getVertexes());
    }

    public Set<Set<T>> stronglyConnectedComponents(Graph<T> g, Collection<T> roots){
        Set<Set<Integer>> sccs = this.stronglyConnectedComponentsCore(g, roots);
        Set<Set<T>> sccs_ = new THashSet<Set<T>>(sccs.size());
        for(Collection<Integer> scc : sccs){
            Set<T> scc_ = new THashSet<T>(scc.size());
            for(Integer v : scc){
                scc_.add(g.fromIndex(v));
            }
            sccs_.add(scc_);
        }
        return sccs_;
    }
}
