package org.tesys.core.estructures;

import java.util.LinkedList;
import java.util.List;

public class Developers {
  private List<Developer> developers;

  public Developers() {
    developers = new LinkedList<Developer>();
  }

  public Developers(List<Developer> developers) {
    super();
    this.developers = developers;
  }

  public List<Developer> getDevelopers() {
    return developers;
  }

  public void setDevelopers(List<Developer> developers) {
    this.developers = developers;
  }
  
  public void addDeveloper(Developer d) {
    this.developers.add(d);
  }
  
}
