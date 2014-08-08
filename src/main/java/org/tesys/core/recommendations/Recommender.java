package org.tesys.core.recommendations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.tesys.core.estructures.Developer;
import org.tesys.core.estructures.IValue;
import org.tesys.core.estructures.Issue;
import org.tesys.core.estructures.Metric;
import org.tesys.core.estructures.Multiplicacion;
import org.tesys.core.estructures.SimpleValue;

public class Recommender {



  public List<Recommendation> recommendate(Metric m, String type) {
    
    /*
    Developer arian = new Developer();
    Developer leandro = new Developer();
    arian.setName("arian");
    leandro.setName("lean");
    arian.setPenalizador(1.0);
    leandro.setPenalizador(1.0);
    
    List<Issue> issuesArian = new LinkedList<Issue>();
    
    Issue issue = new Issue("Ruby-7");
    issue.setIssueType("1");
    issue.setUser("arian");
    issue.addMetric("lines", 50);
    issue.addMetric("bugs", 10);
    issuesArian.add(issue);
    
    Issue issue1 = new Issue("Ruby-8");
    issue1.setIssueType("2");
    issue1.setUser("arian");
    issue1.addMetric("lines", 10);
    issue1.addMetric("bugs", 2);
    issuesArian.add(issue1);
    
    arian.setIssues(issuesArian);
    
    List<Issue> issuesLenadro = new LinkedList<Issue>();
    
    Issue issue2 = new Issue("Ruby-45");
    issue2.setIssueType("1");
    issue2.setUser("leandro");
    issue2.setIssueId("1");
    issue2.addMetric("lines", 10);
    issue2.addMetric("bugs", 2);
    issue2.addMetric("asd", 10);
    issuesLenadro.add(issue2);
    
    leandro.setIssues(issuesLenadro);
    
    List<Developer> developers = new LinkedList<Developer>();
    
    developers.add(arian);
    developers.add(leandro);
    
    
    IValue mul = new Multiplicacion( new SimpleValue("lines"), new SimpleValue("bugs"));
    
    Metric lineasPorBugs = new Metric("linbug", "lineas por bugs", 
        "lineas de codigo por cantidad de bugs", "User Made", mul);
    */

    List<Developer> developers = new LinkedList<Developer>(); //TODO los developers de la db


    List<Recommendation> recomendaciones = new ArrayList<Recommendation>();

    for (Developer d : developers) {
      Double fin = new Double(Double.NaN);
      for (Issue i : d.getIssues()) {
        if (i.getIssueType().equals(type)) {
          Double value = null;
          try {
            value = m.evaluate(i);
          } catch (NullPointerException e) {}
          if (value != null) {
            if (fin.equals(Double.NaN)) {
              fin = 0.0;
            }
            fin += value;
          }
        }
      }
      recomendaciones.add(new Recommendation(d.getName(), fin / d.getIssues().size()));
    }

    Collections.sort(recomendaciones);

    return recomendaciones;
  }

}
