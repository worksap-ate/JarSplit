package com.github.cloverrose.jarsplit.split;

import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IsolatedModuleDistrubuter {
	public void distribute(int numPartitions, List<Set<String>> modules, Set<String> internalNames){
		Set<String> isolatedModules = new THashSet<String>(internalNames);
		for(Set<String> module : modules){
			isolatedModules.removeAll(module);
		}
		System.err.println("isolated modules " + isolatedModules.size());
	    for(int i=0; i< numPartitions - modules.size(); i++){
            modules.add(new THashSet<String>());
        }
	    
	    int idx = 0;
	    Set<String> module = modules.get(idx);
        int counter = 0;
        int threshold = isolatedModules.size() / numPartitions;
        for(String name : isolatedModules){
        	module.add(name);
        	counter++;
        	if(counter > threshold){
        		idx++;
        		module = modules.get(idx);
        		counter = 0;
        	}
        }
	}
}

//for(Set<String> module : modules){
//fileNames.removeAll(module);
//}
//
//System.out.println("isolated classes " + fileNames.size());
//for(String fileName : fileNames){
//System.out.println(fileName);
//}
//
//if(modules.size() < numPartitions){
//List<String> useFileNames = new ArrayList<String>();
//List<Set<String>> isolatedModules = new ArrayList<Set<String>>();
//for(int i=0; i< numPartitions - modules.size(); i++){
//  isolatedModules.add(new THashSet<String>());
//}
//int sum = 0;
//for(Set<String> m : modules){
//  sum += m.size();
//}
//double avg = ((double)sum) / modules.size();
//int n = 0;
//int c = 0;
//for(String fileName : fileNames){
//  if(fileName.contains(".class")){
//      String className = fileName.replace(".class", "").replace("/", ".");
//      isolatedModules.get(n).add(className);
//      useFileNames.add(fileName);
//      c++;
//      if(c > avg){
//          n++;
//          if(n == isolatedModules.size()){
//              break;
//          }
//          c=0;
//      }
//  }
//  c++;
//}
//for(Set<String> isolatedModule : isolatedModules){
//  fileNames.removeAll(useFileNames);
//  modules.add(isolatedModule);
//}
//}
//int n = 0;
//int c = 0;
//int t = fileNames.size() / modules.size();
//for(String fileName : fileNames){
//if(fileName.contains(".class")){
//  String className = fileName.replace(".class", "").replace("/", ".");
//  modules.get(n).add(className);
//  c++;
//  if(c > t){
//      n++;
//      c=0;
//  }
//}
//}