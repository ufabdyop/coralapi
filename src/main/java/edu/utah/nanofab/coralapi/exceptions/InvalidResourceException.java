package edu.utah.nanofab.coralapi.exceptions;

public class InvalidResourceException extends Exception {

  private static final long serialVersionUID = 3143388924254118551L;

  public InvalidResourceException() {
  }

  public InvalidResourceException(String message) {
    super(message);
  }

  public InvalidResourceException(Throwable cause) {
    super(cause);
  }

  public InvalidResourceException(String message, Throwable cause) {
    super(message, cause);
  }


}
