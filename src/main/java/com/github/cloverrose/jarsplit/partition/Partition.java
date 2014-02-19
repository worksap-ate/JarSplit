package com.github.cloverrose.jarsplit.partition;
import java.util.*;

public class Partition {
	class Pair<X, Y>{
		X x;
		Y y;
		Pair(X x, Y y){
			this.x = x;
			this.y = y;
		}
	}
	
//	private double calcCost(List<List<Integer>> S){
//		if(S.isEmpty()){
//			return 0;
//		}
//		int l = S.get(0).size();
//		int ret = 0;
//		for(int i=0;i<l;i++){
//			for(List<Integer> point : S){
//				if(point.get(i) > 0){
//					ret++;
//					break;
//				}
//			}
//		}
//		return Math.pow(ret, 2);
//	}
	
//	private double calcF(List<List<List<Integer>>> clusters){
//		double ret = 0.0;
//		for(List<List<Integer>> S : clusters){
//			ret += calcCost(S);
//		}
//		return ret;
//	}

//	private List<List<List<Integer>>> assignsToClusters(List<List<Integer>> points, List<Integer> assigns, int k){
//		List<List<List<Integer>>> clusters = new ArrayList<List<List<Integer>>>(k);
//		for(int i=0;i<k;i++){
//			clusters.add(new ArrayList<List<Integer>>());
//		}
//		for(int i=0;i<points.size();i++){
//			List<Integer> point = points.get(i);
//			int assign = assigns.get(i);
//			clusters.get(assign).add(point);
//		}
//		return clusters;
//	}
//	
//	public List<Integer> approximation(List<List<Integer>> _points, int k){
//		List<Pair<List<Integer>,Integer>> entries = new ArrayList<Pair<List<Integer>, Integer>>();
//		for(List<Integer> point : _points){
//			int t = 0;
//			for(int v : point){
//				if(v > 0){
//					t++;
//				}
//			}
//			entries.add(new Pair<List<Integer>,Integer>(point, t));
//		}
//		Collections.sort(entries, new Comparator<Pair<List<Integer>, Integer>>() {
//			@Override
//			public int compare(
//					Pair<List<Integer>,Integer> entry1, Pair<List<Integer>,Integer> entry2) {
//	                	return (entry2.y).compareTo(entry1.y);
//	            	}
//		});
//		int N = 100;
//		List<List<Integer>> points = new ArrayList<List<Integer>>(N);
//		for(int i=0;i<N;i++){
//			Pair<List<Integer>, Integer> p = entries.get(i);
//			System.out.println(p.y);
//			points.add(p.x);
//		}
//		
//		List<Integer> assigns = new ArrayList<Integer>(points.size());
//		for(int i=0;i<points.size();i++){
//			assigns.add(0);
//		}
//		double minVal = calcF(assignsToClusters(points, assigns, k));
//		for(int count=0;;count++){
//			List<Integer> best = null;
//			for(int i=0;i<assigns.size();i++){
//				int assign = assigns.get(i);
//				if(assign != 0){
//					continue;
//				}
//				for(int j=1;j<k;j++){
//					List<Integer> cp = new ArrayList<Integer>(assigns.size());
//					for(int v : assigns){
//						cp.add(v);
//					}
//					cp.set(i, j);
//					double f = calcF(assignsToClusters(points, cp, k));
//					if(f < minVal){
//						best = cp;
//						minVal = f;
//						System.out.println(minVal);
//					}
//				}
//			}
//			if(best == null){
//				System.out.println("num of iterations: " + count);
//				return assigns;
//			}
//			assigns = best;
//		}
//	}
//	
//	public List<Integer> start(List<List<Integer>> _points, int k){
//		List<Pair<List<Integer>,Integer>> entries = new ArrayList<Pair<List<Integer>, Integer>>();
//		for(List<Integer> point : _points){
//			int t = 0;
//			for(int v : point){
//				if(v > 0){
//					t++;
//				}
//			}
//			entries.add(new Pair<List<Integer>,Integer>(point, t));
//		}
//		Collections.sort(entries, new Comparator<Pair<List<Integer>, Integer>>() {
//			@Override
//			public int compare(
//					Pair<List<Integer>,Integer> entry1, Pair<List<Integer>,Integer> entry2) {
//	                	return (entry2.y).compareTo(entry1.y);
//	            	}
//		});
//		int N = 100;
//		List<List<Integer>> points = new ArrayList<List<Integer>>(_points.size());
//		List<Integer> assigns = new ArrayList<Integer>(_points.size());
//		for(int n=0; n < entries.size(); n+=N){
//			for(int i=n;i<N + n && i<entries.size();i++){
//				Pair<List<Integer>, Integer> p = entries.get(i);
//				points.add(p.x);
//			}
//			assigns = inner(points, k, assigns);
//		}
//		return assigns;
//	}
//	public List<Integer> inner(List<List<Integer>> points, int k, List<Integer> assigns){
//		int currentSize = assigns.size();
//		int newPointsSize = points.size() - currentSize;
//		for(int i=0;i<newPointsSize;i++){
//			assigns.add(0);
//		}
//		double minVal = calcF2(points, assigns, k);
//		for(int count=0;;count++){
//			List<Integer> best = null;
//			for(int i=currentSize;i<assigns.size();i++){
//				int assign = assigns.get(i);
//				if(assign != 0){
//					continue;
//				}
//				for(int j=1;j<k;j++){
//					List<Integer> cp = new ArrayList<Integer>(assigns.size());
//					for(int v : assigns){
//						cp.add(v);
//					}
//					cp.set(i, j);
//					double f = calcF2(points, cp, k);
//					if(f < minVal){
//						best = cp;
//						minVal = f;
//						System.out.println(minVal);
//					}
//				}
//			}
//			if(best == null){
//				System.out.println("num of iterations: " + count);
//				return assigns;
//			}
//			assigns = best;
//		}
//	}
//	
	
	public List<Integer> start2(List<List<Integer>> _points, int k){
		List<Pair<List<Integer>,Integer>> entries = new ArrayList<Pair<List<Integer>, Integer>>();
		for(List<Integer> point : _points){
			int t = 0;
			for(int v : point){
				if(v > 0){
					t++;
				}
			}
			entries.add(new Pair<List<Integer>,Integer>(point, t));
		}
		Collections.sort(entries, new Comparator<Pair<List<Integer>, Integer>>() {
			@Override
			public int compare(
					Pair<List<Integer>,Integer> entry1, Pair<List<Integer>,Integer> entry2) {
	                	return (entry1.y).compareTo(entry2.y);
	            	}
		});
		List<List<Integer>> points = new ArrayList<List<Integer>>(entries.size());
		for(Pair<List<Integer>, Integer> p : entries){
			points.add(p.x);
		}
		return inner3(points, k);
	}
	
	
	private void sub(List<Integer> x, List<Integer> y){
		for(int i=0;i<x.size();i++){
			x.set(i, x.get(i) - y.get(i));
		}
	}
	private void add(List<Integer> x, List<Integer> y){
		for(int i=0;i<x.size();i++){
			x.set(i, x.get(i) + y.get(i));
		}
	}
	
	private int calcF3(List<List<Integer>> folded){
		int ret = 0;
		for(List<Integer> t : folded){
			int s = 0;
			for(int v : t){
				s += v > 0 ? 1 : 0;
			}
			if(s > ret){
				ret = s;
			}
		}
		return ret;
	}
	public List<Integer> inner3(List<List<Integer>> points, int k){
		List<Integer> assigns = new ArrayList<Integer>(points.size());
		for(int i=0;i<points.size();i++){
			assigns.add(0);
		}

		int l = points.get(0).size();
		
		List<List<Integer>> folded = new ArrayList<List<Integer>>(k);
		
		// cluster 0
		List<Integer> t1 = new ArrayList<Integer>(l);
		for(int j=0;j<l;j++){
			int s = 0;
			for(List<Integer> point : points){
				s += point.get(j);
			}
			t1.add(s);
		}
		folded.add(t1);

		// cluster from 1 to k-1
		for(int i=1;i<k;i++){
			List<Integer> t = new ArrayList<Integer>(l);
			for(int j=0;j<l;j++){
				t.add(0);
			}
			folded.add(t);
		}

		int minVal = calcF3(folded);
		for(int i=0;i<points.size();i++){
			List<Integer> point = points.get(i);
			sub(folded.get(0), points.get(i));
			int move = 0;
			for (int j = 1; j < k; j++) {
				add(folded.get(j), point);
				int f = calcF3(folded);
				if (f < minVal) {
					minVal = f;
					move = j;
				}
				sub(folded.get(j), point);
			}
			assigns.set(i, move);
			add(folded.get(move), point);
		}
		return assigns;
	}
	
//	public List<Integer> inner2(List<List<Integer>> points, int k){
//		int l = points.get(0).size();
//		List<Integer> assigns = new ArrayList<Integer>(points.size());
//		for(int i=0;i<points.size();i++){
//			assigns.add(0);
//		}
//		
//		List<List<Integer>> folded = new ArrayList<List<Integer>>(k - 1);
//		for(int i=0;i<k-1;i++){
//			List<Integer> t = new ArrayList<Integer>(l);
//			for(int j=0;j<l;j++){
//				t.add(0);
//			}
//			folded.add(t);
//		}
//
//		double minVal = calcF2(points, assigns, k);
//		for(int i=0;i<points.size();i++){
//			System.out.println("point " + i);
//			for (int j = 1; j < k; j++) {
//				List<Integer> cp = new ArrayList<Integer>(assigns);
//				cp.set(i, j);
//				double f = calcF2(points, cp, k);
//				if (f < minVal) {
//					assigns = cp;
//					minVal = f;
//					System.out.println(minVal);
//				}
//			}
//		}
//		return assigns;
//	}
//	
//	private List<List<Integer>> convertAssigns(List<Integer> assigns, int k){
//		List<List<Integer>> ret = new ArrayList<List<Integer>>(k);
//		for(int i=0;i<k;i++){
//			ret.add(new ArrayList<Integer>());
//		}
//		for(int i=0;i<assigns.size();i++){
//			int assign = assigns.get(i);
//			ret.get(assign).add(i);
//		}
//		return ret;
//	}
//
//	private double calcF2(List<List<Integer>> points, List<Integer> assigns, int k){
//		List<List<Integer>> assigns2 = convertAssigns(assigns, k);
//		double ret = 0.0;
//		int l = points.get(0).size();
//		for(List<Integer> aa : assigns2){
//			if(aa.isEmpty()){
//				continue;
//			}
//			int r = 0;
//			for(int i=0;i<l;i++){
//				for(int pIndex : aa){
//					List<Integer> point = points.get(pIndex);
//					if(point.get(i) > 0){
//						r++;
//						break;
//					}
//				}
//			}
//			ret += Math.pow(r, 2);
//		}
//		return ret;
//	}
	

}
