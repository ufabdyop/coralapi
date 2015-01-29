package edu.utah.nanofab.coralapi.exceptions;

public class InvalidDateException extends Exception {

  private static final long serialVersionUID = -151586149900090144L;

  public InvalidDateException() {
  }

  public InvalidDateException(String message) {
    super(message);
  }

  public InvalidDateException(Throwable cause) {
    super(cause);
  }

  public InvalidDateException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidDateException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
