package org.tesys.core.analysis.telemetry;

public class MetricPOJO {
  
  private String key;
  private String nombre;
  private String descripcion;
  private String procedencia;

  public MetricPOJO() {
    //for jackson
  }
  
  public MetricPOJO(String key, String nombre, String descripcion, String procedencia) {
    super();
    this.key = key;
    this.nombre = nombre;
    this.descripcion = descripcion;
    this.procedencia = procedencia;
  }
  
  
  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    this.key = key;
  }
  public String getNombre() {
    return nombre;
  }
  public void setNombre(String nombre) {
    this.nombre = nombre;
  }
  public String getDescripcion() {
    return descripcion;
  }
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }
  public String getProcedencia() {
    return procedencia;
  }
  public void setProcedencia(String procedencia) {
    this.procedencia = procedencia;
  }
  
  
  
}
