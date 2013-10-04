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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
		MyDB db = new MyDB();
		MyClassVisitor cv = new MyClassVisitor(Opcodes.ASM4, db);
        String jarName = "/home/USERNAME/Downloads/cfm.jar";
        // jarName = "/home/USERNAME/Desktop/samplejar.jar";
        // jarName = "/home/USERNAME/Desktop/loopsamplejar.jar";
        // jarName = "/home/USERNAME/Desktop/asmsample4.jar";
        
        final File source = new File(jarName);
		List<String> dirs = new ArrayList<String>();
		Map<String, byte[]> files = new HashMap<String, byte[]>();

		JarFile f = new JarFile(source);
		Enumeration<? extends JarEntry> en = f.entries();
		while(en.hasMoreElements()) {
            JarEntry e = en.nextElement();
            String name = e.getName();
            if(e.isDirectory()){
            	System.out.println(" ディレクトリ名: [" + name + "]");
            	dirs.add(name);
            }else{
            	System.out.println(" ファイル名: [" + name + "]");
            	files.put(name, getByte(f.getInputStream(e)));
            	
                if(name.endsWith(".class")) {
                    ClassReader cr = new ClassReader(f.getInputStream(e));
                    cr.accept(cv, 0);
                }
            }
        }
        f.close();
        
        split.Spliter spliter = new split.Spliter();
        List<Set<String>> modules = spliter.start(db.getDependency(), db.getSuper2Subs());
        
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
					System.out.println(" ファイル名: [" + fileName + "]");
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
	}
}
