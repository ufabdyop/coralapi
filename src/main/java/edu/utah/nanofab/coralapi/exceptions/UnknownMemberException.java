package edu.utah.nanofab.coralapi.exceptions;

public class UnknownMemberException extends Exception {
    
  private static final long serialVersionUID = -6218977173646693539L;

  public UnknownMemberException() {
    super();
  }

  public UnknownMemberException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public UnknownMemberException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnknownMemberException(String message) {
    super(message);
  }

  public UnknownMemberException(Throwable cause) {
    super(cause);
  }

}
