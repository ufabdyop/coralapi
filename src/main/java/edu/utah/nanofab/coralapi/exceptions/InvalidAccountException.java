package edu.utah.nanofab.coralapi.exceptions;

public class InvalidAccountException extends Exception {

  private static final long serialVersionUID = 1993576608606149349L;

  public InvalidAccountException() {
  }

  public InvalidAccountException(String message) {
    super(message);
  }

  public InvalidAccountException(Throwable cause) {
    super(cause);
  }

  public InvalidAccountException(String message, Throwable cause) {
    super(message, cause);
  }


}
