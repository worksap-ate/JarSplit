package split;
import java.util.*;

public class dp {
	private void main(Map<String, Set<String>> dependency, Map<String, Set<String>> super2subs){
		int size = dependency.size();

		Map<String, Integer> indexed = new HashMap<String, Integer>();
		int counter = 0;
		for(String name : dependency.keySet()){
			indexed.put(name, counter);
			counter++;
		}
	
		int matrix[][] = new int[size][size];
		
		// self
		for(int i=0;i<size;i++){
			matrix[i][i] = 0;
		}
		
		// dependency
		for(Map.Entry<String, Set<String>> entry : dependency.entrySet()){
			int from = indexed.get(entry.getKey());
			for(String child : entry.getValue()){
				matrix[from][indexed.get(child)] = 1;
			}
		}
		
		// super to subs dependency
		for(Map.Entry<String, Set<String>> entry : super2subs.entrySet()){
			String superName = entry.getKey();
			Set<String> subNames = entry.getValue();
			
			for(Map.Entry<String, Set<String>> depends : dependency.entrySet()){
				String key = depends.getKey();
				int from = indexed.get(key);

				if(depends.getValue().contains(superName) && !subNames.contains(key)){
					for(String subName : subNames){
						matrix[from][indexed.get(subName)] = 1;
					}
					
				}
			}
			
		}
	}
}
