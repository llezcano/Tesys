package org.tesys.core.project.scm;

public class MultiCommandException extends InvalidCommitException {

  private static final long serialVersionUID = 1L;

  @Override
  public String getMessage() {
    return Messages.getString("sytaxerrormultiplecommands");
  }

}
