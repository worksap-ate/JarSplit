package main;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;

public class MyDB {
	private Map<String, Map<String, Integer>> dependency;
	private String current;
    
	public MyDB(){
		this.dependency = new HashMap<String, Map<String, Integer>>();
	}
	
	public void setCurrent(String key){
		key = convert(key);
		L.debug("MyDB.setCurrentkey: " + key);
		this.current = key;
		if(!this.dependency.containsKey(key)){
			this.dependency.put(key, new HashMap<String, Integer>());
		}
	}
	
	public void add(String depend){
		depend = convert(depend);
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
		return s.replace('/', '.');
	}
   
	public String toString(){
		return this.dependency.toString();
	}
}
