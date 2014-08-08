package org.tesys.core.recommendations;

public class Recommendation implements Comparable<Recommendation>{
  private String developerName;
  private Double value; 

  public Recommendation(String developerName, Double value) {
    super();
    this.developerName = developerName;
    this.value = value;
  }
  
  public String getDeveloperName() {
    return developerName;
  }
  public void setDeveloperName(String developerName) {
    this.developerName = developerName;
  }
  public Double getValue() {
    return value;
  }
  public void setValue(Double value) {
    this.value = value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Recommendation)) {
      return false;
    }
    Recommendation other = (Recommendation) obj;
    if (value == null) {
      if (other.value != null) {
        return false;
      }
    } else if (!value.equals(other.value)) {
      return false;
    }
    return true;
  }
  
  
  @Override
  public String toString() {
    return "Recommendation [developerName=" + developerName + ", value=" + value + "]";
  }

  @Override
  public int compareTo(Recommendation o) {
    if( this.value.isNaN() && !o.value.isNaN() ) return 1;
    if( !this.value.isNaN() && o.value.isNaN() ) return -1;
    if( !this.value.isNaN() && o.value.isNaN() ) return 0;
    if( this.value < o.value) return 1;
    return -1;
  }
}