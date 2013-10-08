package test;
import scc.*;
import java.util.*;

public class SampleDatas {
    public Graph<Integer> makeSample(int sumpleNumber){
        if(sumpleNumber == 1){
            return this.makeSample1();
        }else if(sumpleNumber == 2){
            return this.makeSample2();
        }else if(sumpleNumber == 3){
            return this.makeSample3();
        }else{
            return null;
        }
    }

    private Graph<Integer> makeSample1() {
        Graph<Integer> graph = new Graph<Integer>();
        graph.addEdge(0, 1);
        graph.addEdge(0, 3);
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 1);
        graph.addEdge(3, 4);
        graph.addEdge(4, 3);
        return graph;
    }

    private Graph<Integer> makeSample2() {
        Graph<Integer> graph = new Graph<Integer>();
        graph.addEdge(0, 2);
        graph.addEdge(1, 0);
        graph.addEdge(2, 0);
        graph.addEdge(2, 1);
        return graph;
    }

    private Graph<Integer> makeSample3() {
        Graph<Integer> graph = new Graph<Integer>();
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        return graph;
    }


    public Graph<Node> makeNodeSample(int sumpleNumber){
        if(sumpleNumber == 1){
            return this.makeNodeSample1();
        }else if(sumpleNumber == 2){
            return this.makeNodeSample2();
        }else if(sumpleNumber == 3){
            return this.makeNodeSample3();
        }else{
            return null;
        }
    }

    private Graph<Node> makeNodeSample1() {
        Graph<Node> graph = new Graph<Node>();
        Node n0 = new Node(0);
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Node n3 = new Node(3);
        Node n4 = new Node(4);

        graph.addEdge(n0, n1);
        graph.addEdge(n0, n3);
        graph.addEdge(n1, n2);
        graph.addEdge(n1, n3);
        graph.addEdge(n2, n1);
        graph.addEdge(n3, n4);
        graph.addEdge(n4, n3);
        return graph;
    }

    private Graph<Node> makeNodeSample2() {
        Graph<Node> graph = new Graph<Node>();
        Node n0 = new Node(0);
        Node n1 = new Node(1);
        Node n2 = new Node(2);

        graph.addEdge(n0, n2);
        graph.addEdge(n1, n0);
        graph.addEdge(n2, n0);
        graph.addEdge(n2, n1);
        return graph;
    }

    private Graph<Node> makeNodeSample3() {
        Graph<Node> graph = new Graph<Node>();
        Node n0 = new Node(0);
        Node n1 = new Node(1);
        Node n2 = new Node(2);

        graph.addEdge(n0, n1);
        graph.addEdge(n1, n2);
        graph.addEdge(n2, n0);
        return graph;
    }

    public Graph<Node> makeNodeSampleLarge(int numOfVertexes, int maxNumOfAdjacentVertexes) {
        Graph<Node> graph = new Graph<Node>();
        List<Node> nodes = new ArrayList<Node>(numOfVertexes);
        for(int i=0;i<numOfVertexes;i++){
            nodes.add(new Node(i));
        }

        Random rnd = new Random();
        for(int i=0;i<numOfVertexes;i++){
            Node src = nodes.get(i);
            for(int j=0;j<rnd.nextInt(maxNumOfAdjacentVertexes - 1) + 1;j++){
                graph.addEdge(src, nodes.get(rnd.nextInt(numOfVertexes)));
            }
        }
        return graph;
    }
}
