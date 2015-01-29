package edu.utah.nanofab.coralapi.exceptions;

public class InvalidMemberException extends Exception {

  private static final long serialVersionUID = 4343417199043368589L;

  public InvalidMemberException() {
  }

  public InvalidMemberException(String message) {
    super(message);
  }

  public InvalidMemberException(Throwable cause) {
    super(cause);
  }

  public InvalidMemberException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidMemberException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
