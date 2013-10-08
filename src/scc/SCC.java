package scc;
import java.util.*;

public class SCC<T> {
    private class Counter{
        private int value;
        public Counter(int count){
            this.value = count;
        }
        public int getValue(){
            return this.value;
        }
        public void inc(){
            this.value++;
        }
    }

    private void visit(
            Graph<T> g, T vertex, Set<Set<T>> sccs,
            Stack<T> S, Map<T, Integer> low, Map<T, Integer> num, Counter counter){
        counter.inc();
        low.put(vertex, counter.getValue());
        num.put(vertex, counter.getValue());
        S.add(vertex);
        for(T w : g.getAdjacentVertexes(vertex)){
            if(!num.containsKey(w)){
                visit(g, w, sccs, S, low, num, counter);
                low.put(vertex, Math.min(low.get(vertex), low.get(w)));
            }else if(S.contains(w)){
                low.put(vertex, Math.min(low.get(vertex), num.get(w)));
            }
        }
        if(low.get(vertex) == num.get(vertex)){
            Set<T> newScc = new HashSet<T>();
            while(true){
                T w = S.pop();
                newScc.add(w);
                if(vertex.equals(w)){
                    break;
                }
            }
            sccs.add(newScc);
        }
    }

    public Set<Set<T>> stronglyConnectedComponents(Graph<T> g){
        Set<Set<T>> sccs = new HashSet<Set<T>>();
        for(T v : g.getVertexes()){
        	for(Set<T> scc : sccs){
        		if(scc.contains(v)){
        			continue;
       			}
        	}
        	System.out.println("start " + v);
            Map<T, Integer> num = new HashMap<T, Integer>();
            Map<T, Integer> low = new HashMap<T, Integer>();
            Stack<T> S = new Stack<T>();
            Counter counter = new Counter(0);
            visit(g, v, sccs, S, low, num, counter);
        }
        return sccs;
    }
    
    public Set<Set<T>> stronglyConnectedComponents(Graph<T> g, Collection<T> roots){
        Set<Set<T>> sccs = new HashSet<Set<T>>();
        for(T v : roots){
        	for(Set<T> scc : sccs){
        		if(scc.contains(v)){
        			continue;
       			}
        	}
        	System.out.println("start " + v);
            Map<T, Integer> num = new HashMap<T, Integer>();
            Map<T, Integer> low = new HashMap<T, Integer>();
            Stack<T> S = new Stack<T>();
            Counter counter = new Counter(0);
            visit(g, v, sccs, S, low, num, counter);
        }
        return sccs;
    }
}
