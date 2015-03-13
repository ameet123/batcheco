package com.att.datalake.loco.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;
/**
 * generic exception class which wraps other exceptions into this
 * and uses enums specific to various areas.
 * @author ac2211
 *
 */
public class LocoException extends RuntimeException {
	private static final long serialVersionUID = 2577405312882888246L;

	public static LocoException wrap(Throwable exception, ErrorCode errorCode) {
        if (exception instanceof LocoException) {
            LocoException se = (LocoException)exception;
        	if (errorCode != null && errorCode != se.getErrorCode()) {
                return new LocoException(exception.getMessage(), exception, errorCode);
			}
			return se;
        } else {
            return new LocoException(exception.getMessage(), exception, errorCode);
        }
    }
    
    public static LocoException wrap(Throwable exception) {
    	return wrap(exception, null);
    }
    
    private ErrorCode errorCode;
    private final Map<String,Object> properties = new TreeMap<String,Object>();
    
    public LocoException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public LocoException(String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public LocoException(Throwable cause, ErrorCode errorCode) {
		super(cause);
		this.errorCode = errorCode;
	}

	public LocoException(String message, Throwable cause, ErrorCode errorCode) {
		super(message, cause);
		this.errorCode = errorCode;
	}
	
	public ErrorCode getErrorCode() {
        return errorCode;
    }
	
	public LocoException setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
        return this;
    }
	
	public Map<String, Object> getProperties() {
		return properties;
	}
	
    @SuppressWarnings("unchecked")
	public <T> T get(String name) {
        return (T)properties.get(name);
    }
	
    public LocoException set(String name, Object value) {
        properties.put(name, value);
        return this;
    }
    
    public void printStackTrace(PrintStream s) {
        synchronized (s) {
            printStackTrace(new PrintWriter(s));
        }
    }

    public void printStackTrace(PrintWriter s) { 
        synchronized (s) {
            s.println(this);
            s.println("\t-------------------------------");
            if (errorCode != null) {
	        	s.println("\t" + errorCode + ":   " + errorCode.getClass().getSimpleName()); 
			}
            for (String key : properties.keySet()) {
            	s.println("\t" + key + "=[" + properties.get(key) + "]"); 
            }
            s.println("\t-------------------------------");
            StackTraceElement[] trace = getStackTrace();
            for (int i=0; i < trace.length; i++) {
                s.println("\tat " + trace[i]);
            }

            Throwable ourCause = getCause();
            if (ourCause != null) {
                ourCause.printStackTrace(s);
            }
            s.flush();
        }
    }    
}