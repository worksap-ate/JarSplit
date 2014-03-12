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

	
	/**
	 * ベクトル中の非ゼロの要素数の数が大きい順に並び変える
	 * 
	 * @param _points
	 * @return
	 */
	private List<List<Integer>> sortPoints(List<List<Integer>> _points){
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
		return points;
	}
	public List<Integer> start(List<List<Integer>> _points, int k){
		List<List<Integer>> points = sortPoints(_points);
		return assign(points, k);
	}
	
	
	/**
	 * ベクトルの引き算 x = x - y
	 * @param x
	 * @param y
	 */
	private void sub(List<Integer> x, List<Integer> y){
		for(int i=0;i<x.size();i++){
			x.set(i, x.get(i) - y.get(i));
		}
	}
	/**
	 * ベクトルの足し算 x = x + y
	 * @param x
	 * @param y
	 */
	private void add(List<Integer> x, List<Integer> y){
		for(int i=0;i<x.size();i++){
			x.set(i, x.get(i) + y.get(i));
		}
	}
	
	/**
	 * 各clusterの評価(Eval(cluster))はそのcluster出現する非ゼロの要素数とする（少ないほどよい）
	 * 分割全体の評価(Eval(folded))は全てのclusterについてEval(cluster)の最大値とする（少ないほどよい）
	 * @param folded
	 * @return
	 */
	private int Eval(List<List<Integer>> folded){
		int ret = 0;
		for(List<Integer> cluster : folded){
			int e = 0;
			for(int v : cluster){
				e += v > 0 ? 1 : 0;
			}
			if(e > ret){
				ret = e;
			}
		}
		return ret;
	}

	
	/**
	 * roots(points)ををどう分割するか計算する。
	 * i番目のrootは返値のListのi番目（ret.get(i)）に割り振る
	 * 
	 * @param points
	 * @param k
	 * @return
	 */
	public List<Integer> assign(List<List<Integer>> points, int k){
		List<Integer> assigns = new ArrayList<Integer>(points.size());
		for(int i=0;i<points.size();i++){
			assigns.add(0);
		}

		int l = points.get(0).size();
		
		/**
		 * clusterの集合をfoldedとする。
		 * 各clusterは、そのclusterに含まれる全てのpoint(ベクトル)のj番目の要素の和をとったベクトルである。
		 * 初期状態では全てのベクトルがcluster0に含まれている。
		 * 
		 * 要素の和をとっているのは高速化のためであり、本来は各clusterはベクトルの集合として定義する。
		 */
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

		// Eval(folded)が小さくなるように局所最適解を求める
		// 非ゼロの要素数が大きいものからcluster0から別のcluster j へ移動していく
		int minVal = Eval(folded);
		System.out.println("minVal = " + minVal);
		for(int i=0;i<points.size();i++){
			List<Integer> point = points.get(i);
			sub(folded.get(0), point);
			int move = 0;
			for (int j = 1; j < k; j++) {
				add(folded.get(j), point);
				int f = Eval(folded);
				System.out.println("f = " + f);
				if (f < minVal) {
					minVal = f;
					move = j;
					System.out.println("________________________" + "move " + move);
				}
				sub(folded.get(j), point);
			}
			assigns.set(i, move);
			add(folded.get(move), point);
		}
		return assigns;
	}
}
