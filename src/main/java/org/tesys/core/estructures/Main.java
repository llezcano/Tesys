package org.tesys.core.estructures;

import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;

import org.tesys.util.RESTClient;



public class Main {
  
  public static void main(String[] args) {
    
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
    issue2.addMetric("lines", 10);
    issue2.addMetric("bugs", 2);
    issue2.addMetric("asd", 10);
    issuesLenadro.add(issue2);
    
    leandro.setIssues(issuesLenadro);
    
    RESTClient rc = null;
    try {
      rc = new RESTClient("http://localhost:8080/core/rest/");
    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    

    IValue mul = new Multiplicacion( new SimpleValue("lines"), new SimpleValue("bugs"));
  
    
    Metric lineasPorBugs = new Metric("linbug", "lineas por bugs", 
        "lineas de codigo por cantidad de bugs", "User Made", mul);
    
    System.out.println(lineasPorBugs);
    
    
    /*Metric is = rc.POST("/analyzer", mul).readEntity( Metric.class );
    
    System.out.println(  is.getKey() );
    System.out.println(  is.getDescripcion() );
    System.out.println(  is.getNombre() );
    System.out.println(  is.getProcedencia() );
    System.out.println(  is.evaluate(issue)) ;*/

  }



}
