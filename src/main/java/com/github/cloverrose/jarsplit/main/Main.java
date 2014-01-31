package com.github.cloverrose.jarsplit.main;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import com.github.cloverrose.jarsplit.split.*;





public class Main {
    private static byte[] getByte(InputStream iStream) throws IOException{
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = iStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }


    
    private static Map<String, byte[]> getBytes(String jarName) throws IOException{
    	Map<String, byte[]> ret = new THashMap<String, byte[]>();
        JarFile f = new JarFile(new File(jarName));
        Enumeration<? extends JarEntry> en = f.entries();
        while(en.hasMoreElements()) {
            JarEntry e = en.nextElement();
            String name = e.getName();
            if(! e.isDirectory()){
                ret.put(name, getByte(f.getInputStream(e)));
            }
        }
        f.close();
        return ret;
    }

    private static Set<String> convertToInternalNames(Set<String> fileNames) throws IOException{
        Set<String> ret = new THashSet<String>();
        for(String fileName : fileNames){
            if(fileName.contains(".class")){
                ret.add(fileName.replace(".class", "").replace("/", "."));
            }
        }
        return ret;
    }
    
    private static void readJar(MyDB db, String jarName) throws IOException {
    	MyClassVisitor cv = new MyClassVisitor(Opcodes.ASM4, db);
        JarFile f = new JarFile(new File(jarName));
        Enumeration<? extends JarEntry> en = f.entries();
        while(en.hasMoreElements()) {
            JarEntry e = en.nextElement();
            String name = e.getName();
            if(! e.isDirectory()){
                if(name.endsWith(".class")) {
                    ClassReader cr = new ClassReader(f.getInputStream(e));
                    cr.accept(cv, 0);
                }
            }
        }
        f.close();    	
    }
    
    public static void main(String[] args) throws IOException {
        if(args.length != 2){
            throw new RuntimeException("[.jar file name] and [number of partitions] are required.");
        }
        // e.g. cfm.jar or /home/USERNAME/Downloads/cfm.jar
        String jarName = args[0];
        int numPartitions = Integer.parseInt(args[1]);
        System.out.println("Splits " + jarName + " into " + numPartitions + " jars.");

        long start = System.currentTimeMillis();
        
        Map<String, byte[]> files = getBytes(jarName);
        System.out.println("FILE NUM " + files.size());
        Set<String> fileNames = files.keySet();
        Set<String> internalNames = convertToInternalNames(fileNames);

        MyDB db = new MyDB(internalNames);
        readJar(db, jarName);

        long end_read = System.currentTimeMillis();
        System.err.println("read all .class time: " + (end_read- start) + "[ms]");

        List<Set<String>> modules = new Spliter().split(db.getDependency(), db.getSuper2Subs(), numPartitions);
        new IsolatedModuleDistrubuter().distribute(numPartitions, modules, internalNames);
        long end_split = System.currentTimeMillis();
        System.err.println("split time: " + (end_split- end_read) + "[ms]");

        Set<String> notContain = new THashSet<String>();
        for(int i=0;i<modules.size();i++){
            System.out.println("number: " + i);
            final File target = new File("splitted" + i + ".jar");
            final JarOutputStream jarOutStream = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(target)));
            try {
                /* create .class files */
                for(String className : modules.get(i)) {
                    String fileName = className.replace(".", "/") + ".class";
                    if(files.containsKey(fileName)){
                        final JarEntry entry = new JarEntry(fileName);
                        jarOutStream.putNextEntry(entry);
                        jarOutStream.write(files.get(fileName));
                        jarOutStream.closeEntry();
                        fileNames.remove(fileName);
                    }else{
                        notContain.add(fileName);
                    }
                }
                jarOutStream.finish();
            } finally {
                jarOutStream.close();
            }
        }
        System.out.println("# un used files " + fileNames.size());
        for(String s : fileNames){
            System.out.println(s);
        }
        System.out.println("# notContain files " + notContain.size());
        /*for(String s : notContain){
            System.out.println(s);
        }*/
        long end = System.currentTimeMillis();
        System.err.println("write .class time: " + (end - end_split) + "[ms]");
        System.err.println("total time: " + (end - start) + "[ms]");
    }
}
