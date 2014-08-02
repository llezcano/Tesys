package org.tesys.core.analysis.sonar;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.tesys.core.db.Database;
import org.tesys.core.project.scm.RevisionPOJO;
import org.tesys.core.project.scm.SCMManager;


public class SonarAnalizer {

  private static final String USER_HOME = "user.home";
  
  public static final File BUILD_FILE =
      new File(System.getProperty(USER_HOME), ".tesys/build.xml");
  public static final File WORKSPACE =
      new File(System.getProperty(USER_HOME), ".tesys/workspace");

  private SCMManager scm;
  private StoreResults sr;
  private Database db;
  private SonarExtractor se;

  private static SonarAnalizer instance = null;

  private SonarAnalizer() {
    scm = SCMManager.getInstance();
    sr = StoreResults.getInstance();
    db = new Database();
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

    RevisionPOJO[] revSinEscanear =  db.getUnscanedRevisions();
    
    for ( int i=0; i<revSinEscanear.length; i++ ) { 
      
      if( "0".equals( revSinEscanear[i].getRevision() ) ) {
        purgeDirectory(WORKSPACE); 
      } else { 
      //TODO en realidad anda con repo = "" 
        scm.doCheckout(revSinEscanear[i].getRevision(), revSinEscanear[i].getRepository()); 
      } 
      
      analizar();
      revSinEscanear[i].setScaned(true);

      db.store( revSinEscanear[i].getID(), revSinEscanear[i] );
    }
      
    sr.storeAnalysis( se.getResults(revSinEscanear) );

    purgeDirectory(WORKSPACE);


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
    p.setUserProperty("ant.file", BUILD_FILE.getAbsolutePath());
    p.init();
    ProjectHelper helper = ProjectHelper.getProjectHelper();
    p.addReference("ant.projectHelper", helper);
    helper.parse(p, BUILD_FILE);
    p.executeTarget(p.getDefaultTarget());
  }


  /**
   * Dado un directorio elimina el contenido pero no el directorio
   */
  private void purgeDirectory(File dir) {
    for (File file : dir.listFiles()) {
      if (file.isDirectory()) {
        purgeDirectory(file);
      }
      file.delete();
    }
  }



}
