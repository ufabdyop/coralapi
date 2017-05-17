package edu.utah.nanofab.coralapi.exceptions;

public class RequestFailedException extends Exception{

  private static final long serialVersionUID = -6597828387996024469L;

  public RequestFailedException() {
    super();
  }

  public RequestFailedException(String message, Throwable cause) {
    super(message, cause);
  }

  public RequestFailedException(String message) {
    super(message);
  }

  public RequestFailedException(Throwable cause) {
    super(cause);
  }

}
