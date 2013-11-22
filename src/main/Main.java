package main;

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

	public static void main(String[] args) throws IOException {
		if(args.length != 1){
        	throw new RuntimeException(".jar file name is required.");
        }
    	// e.g. cfm.jar or /home/USERNAME/Downloads/cfm.jar
    	String jarName = args[0];
        System.out.println("target jar: " + jarName);
        
		long start = System.currentTimeMillis();

		MyDB db = new MyDB();
		MyClassVisitor cv = new MyClassVisitor(Opcodes.ASM4, db);
        
        final File source = new File(jarName);
		List<String> dirs = new ArrayList<String>();
		Map<String, byte[]> files = new HashMap<String, byte[]>();

		JarFile f = new JarFile(source);
		Enumeration<? extends JarEntry> en = f.entries();
		while(en.hasMoreElements()) {
            JarEntry e = en.nextElement();
            String name = e.getName();
            if(e.isDirectory()){
            	// System.out.println(" ディレクトリ名: [" + name + "]");
            	dirs.add(name);
            }else{
            	// System.out.println(" ファイル名: [" + name + "]");
            	files.put(name, getByte(f.getInputStream(e)));
            	
                if(name.endsWith(".class")) {
                    ClassReader cr = new ClassReader(f.getInputStream(e));
                    cr.accept(cv, 0);
                }
            }
        }
        f.close();
        
        long end_read = System.currentTimeMillis();
        System.err.println("read all .class time: " + (end_read- start) + "[ms]");

        split.Spliter spliter = new split.Spliter();
        List<Set<String>> modules = spliter.start(db.getDependency(), db.getSuper2Subs(), 5);
        
        long end_split = System.currentTimeMillis();
        System.err.println("split time: " + (end_split- end_read) + "[ms]");
        
        for(int i=0;i<modules.size();i++){
			System.out.println("number: " + i);
	        final File target = new File("jarsample" + i + ".jar");
	        final JarOutputStream jarOutStream = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(target)));
			try {
//				/* create directories (needless?) */
//				for (String d : dirs) {
//					System.out.println(" ディレクトリ名: [" + d + "]");
//					final JarEntry entry = new JarEntry(d);
//					entry.setMethod(JarEntry.STORED);
//					entry.setSize(0);
//					entry.setCrc(0);
//					jarOutStream.putNextEntry(entry);
//					jarOutStream.closeEntry();
//				}
				/* create files (include META-INF/MANIFEST.MF) */
				for(String className : modules.get(i)) {
					String fileName = className.replace(".", "/") + ".class";
					// System.out.println(" ファイル名: [" + fileName + "]");
					if(files.containsKey(fileName)){
						final JarEntry entry = new JarEntry(fileName);
						jarOutStream.putNextEntry(entry);
						jarOutStream.write(files.get(fileName));
						jarOutStream.closeEntry();
					}
				}
				jarOutStream.finish();
			} finally {
				jarOutStream.close();
			}
		}
        long end = System.currentTimeMillis();
        System.err.println("write .class time: " + (end - end_split) + "[ms]");
        System.err.println("total time: " + (end - start) + "[ms]");
	}
}
