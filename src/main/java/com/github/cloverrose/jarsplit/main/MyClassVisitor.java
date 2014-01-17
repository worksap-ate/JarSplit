package com.github.cloverrose.jarsplit.main;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;


public class MyClassVisitor extends ClassVisitor {
	private MyDB db;

	public MyClassVisitor(int api, MyDB db) {
		super(api);
		this.db = db;
	}

	public void visit(int version,
	         int access,
	         String name,
	         String signature,  // signature doesn't contain superclass and interfaces information.
	         String superName,
	         String[] interfaces){
		
		L.vvv("MyClassVisitor.visit:name: " + name);
		db.setCurrent(name);

		if(signature != null){
			db.addSignature(signature, this.api);
		}
		if(superName != null){
			L.vvv("MyClassVisitor.visit:superName: " + superName);
			// db.add(superName);
			
			db.addSuper2Subs(superName, name);
		}
		if(interfaces != null){
			for(String i : interfaces){
				L.vvv("MyClassVisitor.visit:interfaces: " + i);
				// db.add(i);
				
				db.addSuper2Subs(i, name);
			}
		}
	}
	
	public FieldVisitor visitField(int access,
            String name,
            String desc,
            String signature,
            Object value){

		L.vvv("MyClassVisitor.visitField:name: " + name);
		if(signature != null){
			L.vvv("MyClassVisitor.visitField:signature: " + signature);
			db.addSignatureType(signature, this.api);
		}else{
			L.vvv("MyClassVisitor.visitField:desc: " + desc);
			db.addDesc(desc);
		}

        if(value != null && value instanceof Type) {
        	L.vvv("MyClassVisitor.visitField:value " + value);
            db.addType((Type) value);
        }
	    return null;
	}
	
	public MethodVisitor visitMethod(int access,
            String name,
            String desc,
            String signature,
            String[] exceptions){
		L.info("MyClassVisitor:visitMethod:name: " + name);
		
		if(signature != null){
			L.vvv("MyClassVisitor:visitMethod:signature: " + signature);
			db.addSignature(signature, this.api);
		}else{
			L.vvv("MyClassVisitor:visitMethod:desc: " + desc);
			db.addMethodDesc(desc);
		}
		
		if(exceptions != null){
			for(String e : exceptions){
				L.vvv("MyClassVisitor:visitMethod:exceptions: " + e);
				db.add(e);
			}
		}
		
		return new MyMethodVisitor(this.api, this.db);
	}
}
