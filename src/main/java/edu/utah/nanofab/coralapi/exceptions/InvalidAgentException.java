package edu.utah.nanofab.coralapi.exceptions;

public class InvalidAgentException extends Exception {

  private static final long serialVersionUID = 4536947868575787290L;

  public InvalidAgentException() {
  }

  public InvalidAgentException(String message) {
    super(message);
  }

  public InvalidAgentException(Throwable cause) {
    super(cause);
  }

  public InvalidAgentException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidAgentException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
