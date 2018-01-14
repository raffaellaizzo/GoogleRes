package it.free;


import org.apache.log4j.Logger;

/**
 * @author Almaviva
 *
 */
public class LogHelper{
	
	//Utility classes should not have a public or default constructor
	private LogHelper(){}
		
	private static String applicationRootloggerName = null;
	
	private static final String errorLoggerName = "ErrorLogger";
	
	/**
	 * @param applicationRootloggerName
	 */
	public static void setRootloggerName(String applicationRootloggerName){
		
		LogHelper.applicationRootloggerName = applicationRootloggerName;		
	}
	
	
	//-----------------------Root Logger dell'applicazione---------------------------
	public static void trace( Object obj ){
		
		Logger.getLogger(applicationRootloggerName).trace(obj);
	}
	
	public static void debug( Object obj ){
		
		Logger.getLogger(applicationRootloggerName).debug( obj );
	}
	
	public static void info( Object obj ){
		
		Logger.getLogger(applicationRootloggerName).info(obj);
	}

	
	public static void warn( Object obj ){
		
		Logger.getLogger(applicationRootloggerName).warn(obj);
	}
		
	public static void error(Throwable e){
		String message = e.getMessage();
		if(message != null) {
			Logger.getLogger(applicationRootloggerName).error(message.replace('\t', ' '));
			Logger.getLogger(errorLoggerName).error(message, e);
		} else {
			Logger.getLogger(applicationRootloggerName).error(e, e);
			Logger.getLogger(errorLoggerName).error(e);
		}
	}
	
	public static void fatal( Object obj ){
		
		Logger.getLogger(applicationRootloggerName).fatal(obj);
	}
	// -----------------------------------------------
	
	
	//----------------------- Logger specifico---------------------------
	public static void trace( Object obj, Class<? extends Object> clazz ){
		
		Logger.getLogger(clazz).trace(obj);
	}
	
	public static void debug( Object obj, Class<? extends Object> clazz ){
		
		Logger.getLogger(clazz).debug( obj );
	}
	
	public static void info( Object obj, Class<? extends Object> clazz ){
		
		Logger.getLogger(clazz).info(obj);
	}

	
	public static void warn( Object obj, Class<? extends Object> clazz ){
		
		Logger.getLogger(clazz).warn(obj);
	}
	
	
	public static void error(Throwable e, Class<? extends Object> clazz ){
		/*
		String message = e.getMessage();
		if(message != null) {
			Logger.getLogger(clazz).error(message.replace('\t', ' '));
			Logger.getLogger(errorLoggerName).error(message, e);
		} else {
			Logger.getLogger(clazz).error(e);
			Logger.getLogger(errorLoggerName).error(e, e);
		}
		*/
		String message = e.toString();
			Logger.getLogger(clazz).error(message.replace('\t', ' '));
			Logger.getLogger(errorLoggerName).error(message, e);
	}
	
	public static void fatal( Object obj, Class<? extends Object> clazz ){
		
		Logger.getLogger(clazz).fatal(obj);
	}
	// -----------------------------------------------
	
}
