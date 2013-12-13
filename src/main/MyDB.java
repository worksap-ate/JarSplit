package main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;

public class MyDB {
	private Map<String, Map<String, Integer>> dependency;
	private Map<String, Set<String>> super2subs; // superclass->subclass(es)
	private String current;
    
	public MyDB(){
		this.dependency = new HashMap<String, Map<String, Integer>>();
		this.super2subs = new HashMap<String, Set<String>>();
	}
	
	public void setCurrent(String key){
		key = convert(key);
		L.debug("MyDB.setCurrentkey: " + key);
		this.current = key;
		if(!this.dependency.containsKey(key)){
			this.dependency.put(key, new HashMap<String, Integer>());
		}
	}
	
	void addSuper2Subs(String superName, String subName){
		superName = convert(superName);
		subName = convert(subName);

		if(superName.startsWith("java.")){
			return;
		}
		if(superName.startsWith("java.lang.Object")){
			return;
		}
		
		if(!this.super2subs.containsKey(superName)){
			this.super2subs.put(superName, new HashSet<String>());
		}
		this.super2subs.get(superName).add(subName);
	}
	
	public void add(String depend){
		depend = convert(depend);
		
		if(depend.startsWith("java.")){
			return;
		}
		if(depend.startsWith("java.lang.Object")){
			return;
		}

		if(depend.equals(current)){
			return;
		}
		L.debug("MyDB.add:depend: " + current + " -> " + depend);
		Map<String, Integer> temp = this.dependency.get(this.current);
		if(!temp.containsKey(depend)){
			temp.put(depend, 1);
		}else{
			temp.put(depend, temp.get(depend) + 1);
		}
	}
	
	public void addType(Type type){
		switch(type.getSort()) {
		case Type.ARRAY:
			String t1 = type.getElementType().getClassName();
			L.vvv("MyClassVisitor.visitField:t1: " + t1);
			this.add(t1);
            break;
        case Type.OBJECT:
            String t2 = type.getClassName();
            L.vvv("MyClassVisitor.visitField:t2: " + t2);
            this.add(t2);
            break;
        }
	}
	
	public void addDesc(String desc){
		Type type = Type.getType(desc);
		this.addType(type);
	}
	
	public void addMethodDesc(String desc) {
        this.addType(Type.getReturnType(desc));
		for(Type t : Type.getArgumentTypes(desc)){
			this.addType(t);
		}
    }
	
	public void addSignature(String signature, int api){
		/**
		 * SignatureReaderにはacceptとacceptTypeがある.
		 * どちらを呼べばいいかはSignatureReaderのコンストラクタ引数signatureによって判断する.
		 * ClassVisitor.visitやClassVisitor.visitMethodの引数としてsignatureが渡された場合はacceptを呼ぶ
		 * http://asm.ow2.org/asm40/javadoc/user/org/objectweb/asm/signature/SignatureReader.html
		 */
		new SignatureReader(signature).accept(new MySignatureVisitor(api, this));
	}
	
	public void addSignatureType(String signature, int api){
		/**
		 * ClassVisitor.visitFieldやMethodVisitor.visitLocalVariableの引数としてsignatureが渡された場合はacceptTypeを呼ぶ
		 */
		new SignatureReader(signature).acceptType(new MySignatureVisitor(api, this));
	}


	private String convert(String s) {
//		int index = s.indexOf("$");
//		if(index > -1){
//			// System.out.println(s +  " -> " + s.substring(0, index));
//			s = s.substring(0, index);
//		}
		return s.replace('/', '.');
	}
   
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String, Map<String, Integer>> e : this.dependency.entrySet()){
			String key = e.getKey();
			Map<String, Integer> value = e.getValue();
			sb.append(key + " = {");
			for(Map.Entry<String, Integer> e2 : value.entrySet()){
				sb.append(e2.getKey() + ", ");
			}
			sb.append("}\n");
		}
		return sb.toString();
	}
	

	public Map<String, Set<String>> getDependency(){
		Map<String, Set<String>> ret = new HashMap<String, Set<String>>();
		for(Map.Entry<String, Map<String, Integer>> entry : this.dependency.entrySet()){
			Set<String> temp = new HashSet<String>();
			for(String t : entry.getValue().keySet()){
				temp.add(t);
			}
			ret.put(entry.getKey(), temp);
		}
		return ret;
	}
	
	public Map<String, Set<String>> getSuper2Subs(){
		Map<String, Set<String>> ret = new HashMap<String, Set<String>>();
		for(Map.Entry<String, Set<String>> entry : this.super2subs.entrySet()){
			Set<String> temp = new HashSet<String>();
			for(String t : entry.getValue()){
				temp.add(t);
			}
			ret.put(entry.getKey(), temp);
		}
		return ret;
	}
}
