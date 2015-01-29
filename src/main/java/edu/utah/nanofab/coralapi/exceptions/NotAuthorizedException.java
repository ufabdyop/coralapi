package edu.utah.nanofab.coralapi.exceptions;

public class NotAuthorizedException extends Exception {

  private static final long serialVersionUID = 6197428357356563840L;

  public NotAuthorizedException() {
    super();
  }

  public NotAuthorizedException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public NotAuthorizedException(String message, Throwable cause) {
    super(message, cause);
  }

  public NotAuthorizedException(String message) {
    super(message);
  }

  public NotAuthorizedException(Throwable cause) {
    super(cause);
  }
  
}
