package split;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;


public class Spliter {
	public void start(Map<String, Set<String>> dependency){
		Map<String, Node> allNodes = new HashMap<String, Node>();
		Set<String> roots = new HashSet<String>();
		
		for(String key : dependency.keySet()){
			Node rootNode = new Node(key);
			allNodes.put(key, rootNode);
			roots.add(key);
		}
	
		for(Map.Entry<String, Set<String>> entry : dependency.entrySet()){
			Node rootNode = allNodes.get(entry.getKey());
			for(String child : entry.getValue()){
				Node childNode;
				if(allNodes.containsKey(child)){
					childNode = allNodes.get(child);
					roots.remove(child);
				}else{
					childNode = new Node(child);
					allNodes.put(child, childNode);
				}
				rootNode.addChild(childNode);
			}	
		}

		System.out.println("______RET_______");
		for(String root : roots){
			allNodes.get(root).show(0);
			// System.out.println("root: " + root);
		}
	}
	
	
	public Set<String> getRoots(Set<String> roots_iter, Map<String, Node> nodes){
		Set<String> ret = new HashSet<String>();
		for(String key : roots_iter){
			ret.add(key);
		}
		for(String root : roots_iter){
			Node rootNode = nodes.get(root);
			for(Node child : rootNode.getChildren()){
				String childName = child.getData();
				if(ret.contains(childName)){
					ret.remove(childName);
				}
			}
		}
		return ret;
	}

	public static String toString(Map<String, Set<String>> o){
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String, Set<String>> entry : o.entrySet()){
			sb.append(entry.getKey() + " : {");
			for(String e : entry.getValue()){
				sb.append(e + ", ");
			}
			sb.append("}\n");
		}
		return sb.toString();
	}
	
	public Map<String, Node> convertToNode(Map<String, Set<String>> dependency){
		Map<String, Node> nodes = new HashMap<String, Node>();
		for(Map.Entry<String, Set<String>> entry : dependency.entrySet()){
			String key = entry.getKey();
			if(!nodes.containsKey(key)){
				nodes.put(key, new Node(key));
			}
			for(String key2 : entry.getValue()){
				if(!nodes.containsKey(key2)){
					nodes.put(key2, new Node(key2));
				}
			}
		}
		return nodes;
	}

	
	public void addRelation(Map<String, Set<String>> dependency, Map<String, Node> nodes){
		for(Map.Entry<String, Set<String>> entry : dependency.entrySet()){
			Node node = nodes.get(entry.getKey());
			for(String key2 : entry.getValue()){
				node.addChild(nodes.get(key2));
			}
		}
	}

	public void addNonMySuperClass(Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs){
		for(Map.Entry<String, Set<String>> entry : super2subs.entrySet()){
			String superName = entry.getKey();
			Set<String> subNames = entry.getValue();
			
			for(Map.Entry<String, Set<String>> depends : dependency.entrySet()){
				if(depends.getValue().contains(superName) && !subNames.contains(depends.getKey())){
					depends.getValue().addAll(subNames);
				}
			}
			
		}
	}	

	public void addNonMySuperClass2(Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs, Map<String, Node> nodes){
		for(Map.Entry<String, Set<String>> entry : super2subs.entrySet()){
			String superName = entry.getKey();
			Set<String> subNames = entry.getValue();
			Set<Node> subNodes = new HashSet<Node>();
			for(String subName : entry.getValue()){
				subNodes.add(nodes.get(subName));
			}
			
			for(Map.Entry<String, Set<String>> depends : dependency.entrySet()){
				String key = depends.getKey();
				Node targetNode = nodes.get(key);

				if(depends.getValue().contains(superName) && !subNames.contains(key)){
					for(Node subNode : subNodes){
						targetNode.addChild(subNode);
					}
					
				}
			}
			
		}
	}
	
	
	private Map<String, Set<String>> mergeTwoDependency(Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs){
		Map<String, Set<String>> ret = new HashMap<String, Set<String>>();
		for(Map.Entry<String, Set<String>> entry : dependency.entrySet()){
			String root = entry.getKey();
			Set<String> children = entry.getValue();
			
			Set<String> temp = new HashSet<String>();
			ret.put(root, temp);
			temp.addAll(children);

			for(Map.Entry<String, Set<String>> entry2 : super2subs.entrySet()){
				String superName = entry2.getKey();
				Set<String> subNames = entry2.getValue();
				if(children.contains(superName) && !subNames.contains(root)){
					temp.addAll(subNames);
				}
			}
		}
		return ret;
	}
	
	private Map<String, Set<String>> removeJava(Map<String, Set<String>> dependency){
		Map<String, Set<String>> ret = new HashMap<String, Set<String>>();

		for(Map.Entry<String, Set<String>> entry : dependency.entrySet()){
			Set<String> temp = new HashSet<String>();
			ret.put(entry.getKey(), temp);
			for(String depend : entry.getValue()){
				if(!depend.startsWith("java.")){
					temp.add(depend);
				}
			}
		}
		return ret;
	}
	private String getPackageName(String name) {
 		int n = name.lastIndexOf('.');
 		if(n > -1){
 			name = name.substring(0, n);
 		}
	 	return name;
 	}

 	private String getRootPackageName(String name) {
 		int n = name.indexOf('.');
 		if(n > -1){
 			name = name.substring(0, n);
 		}
	 	return name;
 	}
 	
	private Map<String, Set<String>> replacePackage(Map<String, Set<String>> dependency){
		Map<String, Set<String>> ret = new HashMap<String, Set<String>>();

		for(Map.Entry<String, Set<String>> entry : dependency.entrySet()){
			String keyPackageName = getPackageName(entry.getKey());
			Set<String> temp;
			if(ret.containsKey(keyPackageName)){
				temp = ret.get(keyPackageName);
			}else{
				temp = new HashSet<String>();
				ret.put(keyPackageName, temp);
			}
			for(String depend : entry.getValue()){
				String packageName = getPackageName(depend);
				temp.add(packageName);
			}
		}
		
		
		return ret;
	}

	public void start2_(Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs){
		System.out.println("hogehoge2");
		dependency = mergeTwoDependency(dependency, super2subs);
		System.out.println("hogehoge");
		dependency = removeJava(dependency);
		dependency = replacePackage(dependency);
		Map<String, Node> nodes = convertToNode(dependency);
		addRelation(dependency, nodes);
		// addNonMySuperClass2(dependency, super2subs, nodes);
		System.out.println(dependency);
		for(String root : getRoots(dependency.keySet(), nodes)){
			System.out.println("root: " + root);
			// nodes.get(root).show(0);
			// System.out.println(widthfirstsearch(nodes.get(root)));
		}
		
		Set<Node> rootNodes = new HashSet<Node>();
		for(String root : getRoots(dependency.keySet(), nodes)){
			rootNodes.add(nodes.get(root));
		}
		// System.out.println(widthfirstsearchs(rootNodes));
		
		depthfirstsearchs(rootNodes);
	}

	
	public void start2(Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs){
		Map<String, Node> nodes = convertToNode(dependency);
		addRelation(dependency, nodes);
		addNonMySuperClass2(dependency, super2subs, nodes);

		
		
		Set<Node> rootNodes = new HashSet<Node>();
		for(String root : getRoots(dependency.keySet(), nodes)){
			rootNodes.add(nodes.get(root));
		}
		depthfirstsearchs(rootNodes);
	}

	private Set<Node> widthfirstsearchs(Set<Node> rootNodes){
		Map<Node, Set<Node>> dependency = new HashMap<Node, Set<Node>>();
		
		for(Node rootNode : rootNodes){
			Deque<Node> queue = new ArrayDeque<Node>();
			Set<Node> visiting = new HashSet<Node>();

			queue.offer(rootNode);
			visiting.add(rootNode);
			while(!queue.isEmpty()){
				Node node = queue.poll();
				// System.out.println(node.getData());
				for(Node childNode : node.getChildren()){
					if(!visiting.contains(childNode)){
						queue.offer(childNode);
						visiting.add(childNode);
					}
				}		
			}
			dependency.put(rootNode, visiting);
		}
		Set<Node> maybeRoots = new HashSet<Node>();
		for(Node rootNode : rootNodes){
			maybeRoots.add(rootNode);
		}
		for(Node rootNode : rootNodes){
			for(Node rootNode2 : rootNodes){
				if(rootNode2 != rootNode){
					if(dependency.get(rootNode2).contains(rootNode)){
						maybeRoots.remove(rootNode);
					}
				}
			}
		}
		System.out.println(maybeRoots);
		System.out.println(maybeRoots.size());
		
		for(Node rootNode : maybeRoots){
			System.out.println("ROOT:");
			System.out.println(rootNode.getData());
			Set<Node> dependency_iter = dependency.get(rootNode);
			System.out.println("DEPENDENCY SIZE: " + dependency_iter.size());	
		}
		return null;
	
	}
	

	private Set<Node> depthfirstsearch_(Node rootNode){
		if(rootNode.dependency != null){
			return rootNode.dependency;
		}
		rootNode.dependency = new HashSet<Node>();
		rootNode.dependency.add(rootNode);
		for(Node childNode : rootNode.getChildren()){
			rootNode.dependency.addAll(depthfirstsearch(childNode));
		}
		return rootNode.dependency;
	}
	
	private Set<Node> depthfirstsearch__(Node rootNode){
		if(rootNode.visitting){
			return rootNode.dependency;
		}
		rootNode.visitting = true;
		rootNode.dependency.add(rootNode);
		for(Node childNode : rootNode.getChildren()){
			rootNode.dependency.addAll(depthfirstsearch(childNode));
		}
		return rootNode.dependency;
	}

	private void dfs(Node rootNode){
		Deque<Node> stack = new ArrayDeque<Node>();
		stack.push(rootNode);
		rootNode.visitting = true;
		while(!stack.isEmpty()) {
			Node node = stack.pop();
			System.out.println(node.getData());
			for(Node childNode : node.getChildren()) {
				if(childNode.visitting = false){
					stack.push(childNode);
				}
			}
		}
	}
	
	private void depthiter(Node rootNode, Set<Node> work){
		if(work.add(rootNode)){
			for(Node child : rootNode.getChildren()){
				depthiter(child, work);
			}
		}		
	}
	private Set<Node> depthfirstsearch(Node rootNode){
		Set<Node> work = new HashSet<Node>();
		depthiter(rootNode, work);
		
		for(Node n : work){
			System.out.println(n.getData());
		}
		System.out.println(work.size());
		return work;
	}
	private Set<Node> _depthfirstsearch(Node rootNode){
		rootNode.visitting = true;
		System.out.println(rootNode);
		System.out.println(rootNode.dependency.size());
		System.out.println(rootNode.todo_notify.size());
		rootNode.dependency.add(rootNode);
		for(Node childNode : rootNode.getChildren()){
			// childNode.todo_notify.add(rootNode);
			// childNode.todo_notify.addAll(rootNode.todo_notify);

			if(!childNode.visitting){
				rootNode.dependency.addAll(depthfirstsearch(childNode));
				System.out.println(rootNode.dependency.size());
			}
		}
		// for(Node notifying : rootNode.todo_notify){
		// 	notifying.dependency.addAll(rootNode.dependency);
		// }
		return rootNode.dependency;
	}

	private void depthfirstsearchs(Set<Node> rootNodes){
		for(Node rootNode : rootNodes){
			depthfirstsearch(rootNode);
		}
		for(Node rootNode : rootNodes){
			System.out.println("ROOT:");
			System.out.println(rootNode.getData());
			System.out.println("DEPENDENCY SIZE: " + rootNode.dependency.size());
			for(Node child : rootNode.dependency){
				System.out.println("------------");
				System.out.println(child.getData());
				for(Node childchild : child.dependency){
					System.out.println(childchild.getData());
				}
			}
		}
	}
	
	
	
	// ---
	public void start3(Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs){
		Map<String, Integer> dictionary = createDict(dependency, super2subs);
		System.out.println("DICT SIZE: " + dictionary.size());
		int cnt = 0;
		for(String key : dictionary.keySet()){
			if(key.startsWith("jp.co.worksap.companyac.")){
				cnt ++;
			}
		}
		System.out.println(cnt);
		System.out.println("1");
		BitSet[] direct = buildDirectDependencies(dictionary, dependency, super2subs);
		System.out.println(direct);
		System.out.println("2");
		//removeJava(direct, dictionary);
		//System.out.println("3");
		
		// dependency = replacePackage(dependency);
		// 自身への参照は一度無視する
        for (int i = 0, n = dictionary.size(); i < n; i++) {
            direct[i].clear(i);
        }
        // 依存関係を伝播し、間接依存関係の表に変換する
        BitSet[] indirect = toIndirectDependencies(direct);
        System.out.println(indirect);
	}

	private Map<String, Integer> createDict(Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs){
		Map<String, Integer> dict = new HashMap<String, Integer>();
		for(Map.Entry<String, Set<String>> entry : dependency.entrySet()){
			if(!dict.containsKey(entry.getKey())){
				dict.put(entry.getKey(), dict.size());
			}
			for(String child : entry.getValue()){
				if(!dict.containsKey(child)){
					dict.put(child, dict.size());
				}		
			}
		}
		for(Map.Entry<String, Set<String>> entry : super2subs.entrySet()){
			if(!dict.containsKey(entry.getKey())){
				dict.put(entry.getKey(), dict.size());
			}
			for(String sub : entry.getValue()){
				if(!dict.containsKey(sub)){
					dict.put(sub, dict.size());
				}		
			}
		}
		return dict;
	}
	
	private static BitSet[] buildDirectDependencies(Map<String, Integer> dic, Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs) {
        // a -> b という直接の依存がある場合、direct[index(a), index(b)] = true にする。
        BitSet[] direct = newMatrix(dic.size());
        for(Map.Entry<String, Set<String>> entry : dependency.entrySet()){
        	int sourceIndex = dic.get(entry.getKey());
        	for(String child : entry.getValue()){
        		int targetIndex = dic.get(child);
        		direct[sourceIndex].set(targetIndex);
        	}
        }
        
        
        for(Map.Entry<String, Set<String>> entry : dependency.entrySet()){
			String root = entry.getKey();
			Set<String> children = entry.getValue();
			
			for(Map.Entry<String, Set<String>> entry2 : super2subs.entrySet()){
				String superName = entry2.getKey();
				Set<String> subNames = entry2.getValue();
				if(children.contains(superName) && !subNames.contains(root)){
					for(String subName : subNames){
						direct[dic.get(root)].set(dic.get(subName));				
					}
				}
			}
		}
        return direct;
    }
	
	
	private void removeJava(BitSet[] direct, Map<String, Integer> dict){
		for(Map.Entry<String, Integer> entry : dict.entrySet()){
			String key = entry.getKey();
			int index = dict.get(key);
			if(key.startsWith("java.")){
				for(int i = 0; i<dict.size(); i++){
					direct[index].set(i, false);
				}
				for(BitSet b : direct){
					b.set(index, false);
				}
			}
		}
	}
	
	private static BitSet[] toIndirectDependencies(BitSet[] direct) {
        // i -> j の依存があるとき、i は j が依存するすべての要素を間接的に依存する
        // DEP[i, j] => for all k, DEP[i, k] = DEP[i, k] + DEP[j, k]
        // これを収斂するまで繰り返すと、DEP[i, j] はi -*> jの間接依存関係になる
        BitSet[] indirect = copyMatrix(direct);
        boolean changed;
        do {
        	System.out.println("hoge");
            changed = false;
            for (int i = 0; i < indirect.length; i++) {
            	System.out.println("i:" + i);
            	BitSet from = indirect[i];
                for (int j = from.nextSetBit(0); j >= 0; j = from.nextSetBit(j + 1)) {
                    BitSet to = indirect[j];
                    for (int k = to.nextSetBit(0); k >= 0; k = to.nextSetBit(k + 1)) {
                        changed |= !from.get(k);
                        from.set(k);
                    }
                }
            }
        } while (changed); // 変更がなくなるまで繰り返し
        
        return indirect;
    }
	
	private static BitSet[] newMatrix(int size) {
        BitSet[] loops = new BitSet[size];
        for (int i = 0; i < size; i++) {
            loops[i] = new BitSet(size);
        }
        return loops;
    }
	private static BitSet[] copyMatrix(BitSet[] matrix) {
	        BitSet[] loops = new BitSet[matrix.length];
	        for (int i = 0; i < matrix.length; i++) {
	            loops[i] = (BitSet) matrix[i].clone();
	        }
	        return loops;
	    }
	    
}
