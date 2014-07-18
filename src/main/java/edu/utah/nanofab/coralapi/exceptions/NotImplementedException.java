package edu.utah.nanofab.coralapi.exceptions;

public class NotImplementedException extends Exception{

	private static final long serialVersionUID = -6597828387676024469L;

	public NotImplementedException() {
		super();
	}

	public NotImplementedException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NotImplementedException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotImplementedException(String message) {
		super(message);
	}

	public NotImplementedException(Throwable cause) {
		super(cause);
	}

}
