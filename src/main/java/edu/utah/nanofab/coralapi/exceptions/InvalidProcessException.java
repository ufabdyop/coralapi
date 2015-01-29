package edu.utah.nanofab.coralapi.exceptions;

public class InvalidProcessException extends Exception {

  private static final long serialVersionUID = -6159399633208432096L;

  public InvalidProcessException() {
  }

  public InvalidProcessException(String message) {
    super(message);
  }

  public InvalidProcessException(Throwable cause) {
    super(cause);
  }

  public InvalidProcessException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidProcessException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
