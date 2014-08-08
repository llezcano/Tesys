package org.tesys.core.estructures;

import java.util.LinkedList;
import java.util.List;

public class Main {
  
  public static void main(String[] args) {
    Developer arian = new Developer();
    Developer leandro = new Developer();
    arian.setName("arian");
    leandro.setName("lean");
    arian.setPenalizador(1.0);
    leandro.setPenalizador(1.0);
    
    Developers developers = new Developers();
    
    developers.addDeveloper(arian);
    developers.addDeveloper(leandro);
    
    List<Issue> issuesArian = new LinkedList<Issue>();
    
    Issue issue = new Issue("Ruby-7");
    issue.setIssueType("1");
    issue.setUser("arian");
    issue.addMetric("lines", 50);
    issue.addMetric("bugs", 10);
    issuesArian.add(issue);
    
    Issue issue1 = new Issue("Ruby-8");
    issue1.setIssueType("1");
    issue1.setUser("arian");
    issue1.addMetric("lines", 10);
    issue1.addMetric("bugs", 1);
    issuesArian.add(issue1);
    
    arian.setIssues(issuesArian);
    
    IValue mul = new Multiplicacion( new SimpleValue("lines"), new SimpleValue("bugs"));
    
    Metric lineasPorBugs = new Metric("linbug", "lineas por bugs", 
        "lineas de codigo por cantidad de bugs", "User Made", mul);

    
    for (Developer d : developers.getDevelopers()) {
      Double fin = new Double(0.0);
      for (Issue i : d.getIssues()) {
        fin += lineasPorBugs.getValue( i );
      }
      System.out.println(fin/d.getIssues().size());
    }

  }

}
