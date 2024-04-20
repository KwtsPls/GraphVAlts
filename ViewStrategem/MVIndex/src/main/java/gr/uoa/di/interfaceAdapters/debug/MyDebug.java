package gr.uoa.di.interfaceAdapters.debug;

import java.util.function.Supplier;

import org.apache.log4j.Logger;

public class MyDebug {
	private final static Logger logger = Logger.getLogger(MyDebug.class);
	private final static boolean enabled = false;

	public static void log(Supplier<String> supplier) {
		if (enabled) {
			logger.info(supplier.get());
		}
	}

	public static void log(Object string) {
		if (enabled) {
			logger.info(string);
		}
	}
	
	public static void logHeader(Supplier<String> supplier) {
		if (enabled) {
			logger.info("******************************* Header *******************************");
			logger.info(supplier.get());
			logger.info("**********************************************************************");
		}
	}

	static public void print(Object obj) {
		if (enabled)
			System.err.print(obj);
	}

	static public void println(Object obj) {
		if (enabled)
			System.err.println(obj);
	}

	static public void printAndLog(Supplier<String> supplier) {
		String string = supplier.get();
		println(string );		
		log(string );
	}

	
}
