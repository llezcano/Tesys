package org.tesys.core.analysis.sonar;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.tesys.core.db.DatabaseFacade;
import org.tesys.core.project.scm.RevisionPOJO;
import org.tesys.core.project.scm.SCMManager;


public class SonarAnalizer {

  public static final File buildFile =
      new File(System.getProperty("user.home"), ".tesys/build.xml");
  public static final File workspace =
      new File(System.getProperty("user.home"), ".tesys/workspace");

  private SCMManager scm;
  private StoreResults sr;
  private DatabaseFacade db;
  private SonarExtractor se;

  private static SonarAnalizer instance = null;

  private SonarAnalizer() {
    scm = SCMManager.getInstance();
    sr = StoreResults.getInstance();
    db = new DatabaseFacade();
    se = new SonarExtractor();
  }

  public static SonarAnalizer getInstance() {
    if (instance == null) {
      instance = new SonarAnalizer();
    }
    return instance;
  }


  /**
   * Consigue todos los commits hechos y agarra los que todavia no fueron analizados por esta misma
   * clase.
   * 
   * Una vez que tiene los no analizados, commit por commit, hace checkouts y ejecuta al sonar por
   * cada checkout.
   * 
   * Luego extrae todos los datos desde la api timemachine de sonar y guarda en la base de datos que
   * revisiones fueron analizadas como asi tambien los analisis
   * 
   * Devuelve true cuando se termina de hacer los analisis.
   * 
   * Esta clase trabaja en la carpeta .tesys del home por defecto, y espera que en dicha carpeta
   * alla un archivo que sea una tarea ant para ejecutar analisis (build.xml)
   * 
   * Como asi tambien guarda los resultados de los checkouts en .tesys/workspace el archivo ant debe
   * estar correctamente configurado para encontrar los codigos en ese lugar
   * 
   */
  public boolean executeSonarAnalysis() {

    List<RevisionPOJO> revisionesSinEscanear =  db.getUnscanedRevisions();
    
    for ( RevisionPOJO rev : revisionesSinEscanear ) { 
      
      if( rev.getRevision().equals("0") ) {
        purgeDirectory(workspace); 
      } else { 
        scm.doCheckout(rev.getRevision(), rev.getRepository()); //en realidad anda con repo = "" 
      } 
      
      analizar();
      rev.setScaned(true);
      db.storeCommit( rev );
    }
      
    sr.storeAnalysis( se.getResults(revisionesSinEscanear) );

    purgeDirectory(workspace);


    return true;
  }

  /**
   * Guarda los tipos de metricas que soporta la instancia de sonar (/api/metrics)
   * 
   * @return true cuando las guarda
   */
  public boolean storeMetrics() {
    sr.storeMetrics(se.getMetrics());
    return true;
  }

  /**
   * Executa una tarea ant ubicada en buildFile
   */
  private void analizar() {
    Project p = new Project();
    p.setUserProperty("ant.file", buildFile.getAbsolutePath());
    p.init();
    ProjectHelper helper = ProjectHelper.getProjectHelper();
    p.addReference("ant.projectHelper", helper);
    helper.parse(p, buildFile);
    p.executeTarget(p.getDefaultTarget());
  }


  /**
   * Dado un directorio elimina el contenido pero no el directorio
   */
  private void purgeDirectory(File dir) {
    for (File file : dir.listFiles()) {
      if (file.isDirectory())
        purgeDirectory(file);
      file.delete();
    }
  }



}
