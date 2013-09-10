package main;

public class L {
	public static final int ERROR = 5;
	public static final int WARN = 4;
	public static final int INFO = 3;
	public static final int DEBUG = 2;
	public static final int VVV = 1;

	private static int LEVEL = INFO;
	
	public static void setLevel(int level){
		LEVEL = level;
	}

	public static void log(Object o, int level){
		if(level >= LEVEL){
			System.out.println(o);
		}
	}
	
	public static void error(Object o){
		log(o, ERROR);
	}
	public static void warn(Object o){
		log(o, WARN);
	}
	public static void info(Object o){
		log(o, INFO);
	}
	public static void debug(Object o){
		log(o, DEBUG);
	}
	public static void vvv(Object o){
		log(o, VVV);
	}
}
