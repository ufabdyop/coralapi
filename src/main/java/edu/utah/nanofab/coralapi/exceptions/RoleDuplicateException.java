package edu.utah.nanofab.coralapi.exceptions;

public class RoleDuplicateException extends Exception {

  private static final long serialVersionUID = -1147842887697558846L;

  public RoleDuplicateException() {
    super();
  }


  public RoleDuplicateException(String message, Throwable cause) {
    super(message, cause);
  }

  public RoleDuplicateException(String message) {
    super(message);
  }

  public RoleDuplicateException(Throwable cause) {
    super(cause);
  }
  
}
