package edu.utah.nanofab.coralapi.exceptions;

public class InvalidRoleException extends Exception {

  private static final long serialVersionUID = -9085663306952598579L;

  public InvalidRoleException() {
  }

  public InvalidRoleException(String message) {
    super(message);
  }

  public InvalidRoleException(Throwable cause) {
    super(cause);
  }

  public InvalidRoleException(String message, Throwable cause) {
    super(message, cause);
  }


}
