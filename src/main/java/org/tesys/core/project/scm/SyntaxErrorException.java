package org.tesys.core.project.scm;

public class SyntaxErrorException extends InvalidCommitException {

  private static final long serialVersionUID = 1L;

  @Override
  public String getMessage() {
    return Messages.getString("syntaxerroruser");
  }

  
}
