package kmeans;

import java.util.*;

public class Kmeans {
	class Pair<X, Y>{
		X x;
		Y y;
		Pair(X x, Y y){
			this.x = x;
			this.y = y;
		}
	}
	private double calcDistance(List<Double> point1, List<Double> point2){
		double ret = 0.0;
		int size = point1.size();
		for(int i=0;i<size;i++){
			ret += Math.pow((point1.get(i) - point2.get(i)), 2);
		}
		return Math.sqrt(ret);		
	}

	private List<Double> calcCentroid(int index, List<List<Double>> points, List<Integer> assigns){
		int num = points.size();
		int dimension = points.get(0).size();
		
		List<Double> ret = new ArrayList<Double>(dimension);
		for(int i=0;i<dimension; i++){
			ret.add(0.0);
		}
		int n = 0;
		for(int i=0;i<num;i++){
			int assign = assigns.get(i);
			if(assign == index){
				n++;
				List<Double> p = points.get(i);
				for(int j=0;j<dimension;j++){
					ret.set(j, ret.get(j) + p.get(j));
				}
			}
		}
		if(n==0){
			return ret;
		}else{
			for(int i=0;i<dimension;i++){
				ret.set(i, ret.get(i) / n);
			}
			return ret;
		}
	}

	private double calcDistanceBetweenNearestCentroidDistance(List<Double> point, List<List<Double>> centroids){
		int k=centroids.size();
		double nearest_distance = Double.MAX_VALUE;
		for(int i=0;i<k;i++){
			List<Double> centroid = centroids.get(i);
			double distance = calcDistance(point, centroid);
			if(distance < nearest_distance){
				nearest_distance = distance;
			}
		}
		return nearest_distance;	
	}

	private int calcDistanceBetweenNearestCentroidIndex(List<Double> point, List<List<Double>> centroids){
		int k=centroids.size();
		int nearest_centroid = -1;
		double nearest_distance = Double.MAX_VALUE;
		for(int i=0;i<k;i++){
			List<Double> centroid = centroids.get(i);
			double distance = calcDistance(point, centroid);
			if(distance < nearest_distance){
				nearest_distance = distance;
				nearest_centroid = i;
			}
		}
		return nearest_centroid;	
	}
	
	private <T> List<T> copy(List<T> xs){
		List<T> ret = new ArrayList<T>(xs.size());
		for(T x : xs){
			ret.add(x);
		}
		return ret;
	}
	private boolean eq(List<Integer> xs, List<Integer> ys){
		for(int i=0;i<xs.size();i++){
			int x = xs.get(i);
			int y = ys.get(i);
			if(x != y){
				return false;
			}
		}
		return true;
	}
	private Pair<List<List<Double>>, List<Integer>> kpp(List<List<Double>> points, int k){
		List<List<Double>> centroids = new ArrayList<List<Double>>(k);
		Random rand = new Random();
		int random_index = rand.nextInt(points.size());
		centroids.add(copy(points.get(random_index)));
		
		for(int i=1;i<k;i++){
			List<Double> distances = new ArrayList<Double>();
			for(List<Double> p : points){
				distances.add(calcDistanceBetweenNearestCentroidDistance(p, centroids));
			}
			double sum_distance = 0.0;
			for(Double distance : distances){
				sum_distance += distance * rand.nextDouble();
			}
			
			for(int j=0;j<distances.size();j++){
				double distance = distances.get(j);
				sum_distance -= distance;
				if(sum_distance <= 0){
					centroids.add(copy(points.get(j)));
					break;
				}
			}
		}
		
		List<Integer> assigns = new ArrayList<Integer>();
		for(List<Double> p : points){
			assigns.add(this.calcDistanceBetweenNearestCentroidIndex(p, centroids));
		}
		
		return new Pair<List<List<Double>>, List<Integer>>(centroids, assigns);
		
	}
	
	
	public List<Integer> start(List<List<Double>> points, int k){
		Pair<List<List<Double>>, List<Integer>> kpp_ret = kpp(points, k);
		List<List<Double>> centroids = kpp_ret.x;
		List<Integer> assigns = kpp_ret.y;
		
		System.out.println("dimension: " + centroids.get(0).size());
		System.out.println("num: " + points.size());
		
		for(int count=0;;count++){
			List<Integer> prev_assigns = assigns;
			
			for(int i=0;i<k;i++){
				centroids.set(i, calcCentroid(i, points, assigns));
			}
			assigns = new ArrayList<Integer>();
			for(List<Double> p : points){
				assigns.add(calcDistanceBetweenNearestCentroidIndex(p, centroids));
			}
			if(eq(assigns, prev_assigns)){
				System.err.println("num of iterations: " + count + "\n");
				break;
			}
		}
		return assigns;
	}
	
	private List<List<Double>> make_sample(int dimension, int num){
		Random rand = new Random();
		List<List<Double>> points = new ArrayList<List<Double>>();
		for(int i=0;i<num;i++){
			List<Double> temp = new ArrayList<Double>();
			for(int j=0;j<dimension;j++){
				temp.add(rand.nextDouble());
			}
			points.add(temp);
		}
		for(int i=0;i<points.size();i++){
			List<Double> p = points.get(i);
			if(i<num/2){
				p.set(0, p.get(0) + 0.5);
			}else{
				p.set(0, p.get(0) - 0.5);
			}
		}
		return points;
	}
	
	
	
	public void test(){
		List<List<Double>> points = make_sample(100, 1000);
		List<Integer> assigns = start(points, 2);
		System.err.println(assigns);
	}
	
	public static void main(String[] args){
		new Kmeans().test();
	}
}
