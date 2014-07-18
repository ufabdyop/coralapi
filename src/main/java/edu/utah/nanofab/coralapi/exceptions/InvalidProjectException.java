package edu.utah.nanofab.coralapi.exceptions;

public class InvalidProjectException extends Exception {
	
	private static final long serialVersionUID = -5574315061900704163L;

	public InvalidProjectException() {
	}

	public InvalidProjectException(String message) {
		super(message);
	}

	public InvalidProjectException(Throwable cause) {
		super(cause);
	}

	public InvalidProjectException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidProjectException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
