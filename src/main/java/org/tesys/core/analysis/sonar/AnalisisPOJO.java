package org.tesys.core.analysis.sonar;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;

import org.tesys.core.project.scm.RevisionPOJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnalisisPOJO implements Comparable<AnalisisPOJO> {
  

  private RevisionPOJO revision;
  private List<KeyValuePOJO> results;
  private boolean scaned;
  
 
  public AnalisisPOJO(RevisionPOJO rev) {
    results  = new ArrayList<KeyValuePOJO>();
    this.revision = rev;
    scaned = false;
  }
  
  public List<KeyValuePOJO> getResults() {
    return results;
  }
 

  public void add(KeyValuePOJO k) {
    results.add(k);
  }
  
  public String getID() {
    return this.revision.getID();
  }
  
  
  public AnalisisPOJO() {}

  public RevisionPOJO getRevision() {
    return revision;
  }

  public void setRevision(RevisionPOJO revision) {
    this.revision = revision;
  }
  
  public boolean isScaned() {
    return scaned;
  }

  public void setScaned(boolean scaned) {
    this.scaned = scaned;
  }

  @Override
  public int compareTo(AnalisisPOJO o) {
    if (this.getRevision().getDate() < o.getRevision().getDate()) {
      return -1;
    }
    return 1;
  }

  
}

