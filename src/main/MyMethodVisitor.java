package main;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class MyMethodVisitor extends MethodVisitor {
	private MyDB db;

	public MyMethodVisitor(int api, MyDB db) {
		super(api);
		this.db = db;
	}

	public void visitTypeInsn(int opcode,
                              String desc) {
    	db.addDesc(desc);
    }

    public void visitFieldInsn(int opcode,
                               String owner, String name, String desc) {
        db.addDesc(desc);
    }

    public void visitMethodInsn(int opcode,
                                String owner, String name, String desc) {
        db.addMethodDesc(desc);
    }

    public void visitInvokeDynamicInsn(String name,
            String desc,
            Handle bsm,
            Object... bsmArgs){
    	db.addMethodDesc(desc);    	
    }

    public void visitLdcInsn(Object cst) {
        if(cst instanceof Type) {
            db.addType((Type) cst);
        }
    }

    public void visitMultiANewArrayInsn(
             String desc, int dims) {
        db.addDesc(desc);
    }

    public void visitLocalVariable(String name,
            String desc,
            String signature,
            Label start,
            Label end,
            int index){    	
    	if(signature != null){
    		L.vvv("MyMethodVisitor:visitLocalVariable:signature: " + signature);
    		db.addSignatureType(signature, this.api);
    	}else{
    		L.vvv("MyMethodVisitor:visitLocalVariable:desc: " + desc);
    		db.addDesc(desc);
    	}
    }
}
