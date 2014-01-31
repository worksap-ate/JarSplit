package com.github.cloverrose.jarsplit.main;

import org.objectweb.asm.signature.SignatureVisitor;

public class MySignatureVisitor extends SignatureVisitor {
    private MyDB db;

    public MySignatureVisitor(int api, MyDB db) {
        super(api);
        this.db = db;
    }

    public void visitFormalTypeParameter(String name){
        // class SomeClass<T>の場合name=T
        L.vvv("MySignatureVisitor.visitFormalTypeParameter:name: " + name);
    }

    public SignatureVisitor visitClassBound(){
        L.vvv("MySignatureVisitor.visitClassBound");
        return this;
    }

    public SignatureVisitor visitInterfaceBound(){
        L.vvv("MySignatureVisitor.visitInterfaceBound");
        return this;
    }

    public SignatureVisitor visitSuperclass(){
        L.vvv("MySignatureVisitor.visitSuperClass");
        return this;
    }

    public SignatureVisitor visitInterface(){
        L.vvv("MySignatureVisitor.visitInterface");
        return this;
    }

    public SignatureVisitor visitParameterType(){
        L.vvv("MySignatureVisitor.visitParameterType");
        return this;
    }

    public SignatureVisitor visitReturnType(){
        L.vvv("MySignatureVisitor.visitReturnType");
        return this;
    }

    public SignatureVisitor visitExceptionType(){
        L.vvv("MySignatureVisitor.visitExceptionType");
        return this;
    }

    public void visitBaseType(char descriptor){
        L.vvv("MySignatureVisitor.visitBaseType:descriptor: " + descriptor);
    }

    public void visitTypeVariable(String name){
        // this method is called when field's type likes List<T> where T is class parameter
        L.vvv("MySignatureVisitor.visitTypeVariable:name: " + name);
    }

    public SignatureVisitor visitArrayType(){
        // this method is called when field's type likes List<Integer[]>
        L.vvv("MySignatureVisitor.visitArrayType");
        return this;
    }

    public void visitClassType(String name){
        // java/util/Listやjava/lang/Integerといったように1番単純な型になったときに呼ばれる
        L.vvv("MySignatureVisitor.visitClassType:name: " + name);
        this.db.add(name);
    }

    public void visitInnerClassType(String name){
        L.vvv("MySignatureVisitor.visitInnerClassType:name: " + name);
    }

    public void visitTypeArgument(){
        L.vvv("MySignatureVisitor.visitTypeArgument");
    }
}
