package org.tesys.core.analysis.sonar;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.tesys.core.TesysPath;
import org.tesys.core.analysis.sonar.metricsdatatypes.Metrics;
import org.tesys.core.db.ElasticsearchDao;
import org.tesys.core.project.scm.RevisionPOJO;
import org.tesys.core.project.scm.SCMManager;
import org.tesys.util.Searcher;

public class SonarAnalizer {

    private static final Logger LOG = Logger.getLogger(SonarAnalizer.class
	    .getName());
    
    private static FileHandler handler;

    private static final String USER_HOME = "user.home";

    /**
     * Carpeta en el sistema donde se guardan los datos generales de tesys
     */
    public static final File BUILD_FILE = new File(
	    System.getProperty(USER_HOME), ".tesys");

    /**
     * workspace, donde se hacen los checkouts para recuperar codigo viejo y analizarlo
     */
    public static final File WORKSPACE = new File(
	    System.getProperty(USER_HOME), ".tesys/workspace");

    private SCMManager scm;
    private SonarExtractor sonarExtractor;

    private static SonarAnalizer instance = null;

    private SonarAnalizer() {
	scm = SCMManager.getInstance();
	sonarExtractor = new SonarExtractor();
	try {
	    handler = new FileHandler(TesysPath.Path +"logs/tesys-log.%u.%g.xml", 1024 * 1024, 10);
	} catch (SecurityException | IOException e) {
	    LOG.log(Level.SEVERE, e.getMessage());
	    handler = null;
	}
	LOG.addHandler(handler);

    }

    public static synchronized SonarAnalizer getInstance() {
	if (instance == null) {
	    instance = new SonarAnalizer();
	}
	return instance;
    }

    /**
     * Consigue todos los commits hechos y agarra los que todavia no fueron
     * analizados por esta misma clase.
     * 
     * Una vez que tiene los no analizados, commit por commit, hace checkouts y
     * ejecuta al sonar por cada checkout.
     * 
     * Luego extrae todos los datos desde la api timemachine de sonar y guarda
     * en la base de datos que revisiones fueron analizadas como asi tambien los
     * analisis
     * 
     * Devuelve true cuando se termina de hacer los analisis.
     * 
     * Esta clase trabaja en la carpeta .tesys del home por defecto, y espera
     * que en dicha carpeta alla un archivo que sea pra ejecuatr sonar
     * 
     * Como asi tambien guarda los resultados de los checkouts en
     * .tesys/workspace el archivo ant debe estar correctamente configurado para
     * encontrar los codigos en ese lugar
     * 
     */
    public synchronized void executeSonarAnalysis() {

	LOG.log(Level.INFO, "Se solicito una analisis de sonar");
	
	List<AnalisisPOJO> analisisAcumulados = getAnalisisAcumulados();
	
	/**
	 * Se intenta recuperar el ultimo analisis acumulado para poder comparar con una
	 * base
	 */
	ElasticsearchDao<AnalisisPOJO> ultimoAnalisisDAO = new ElasticsearchDao<AnalisisPOJO>(
		AnalisisPOJO.class, ElasticsearchDao.DEFAULT_RESOURCE_LAST_ANALYSIS);

	if( ultimoAnalisisDAO.exists("0") ) {
	    LOG.log(Level.INFO, "Se recupera analisis anterior");
	    analisisAcumulados.add( 0, ultimoAnalisisDAO.read("0") );
        } else if (analisisAcumulados.isEmpty()) {
            //caso del primer commit que no hay con que compararlo
            LOG.log(Level.INFO, "El analisis fue cancelado por falta de commits");
            return;
        }

	/*
	 * El ultimo analisis se actualiza
	 */
	ultimoAnalisisDAO.update("0", analisisAcumulados.get( analisisAcumulados.size() - 1 ));

	List<SonarMetricPOJO> metricas = getMetrics();
	List<AnalisisPOJO> analisisPorCommit = getAnalisisPorCommit(
		analisisAcumulados, metricas);
	analisisAcumulados.clear();

	ElasticsearchDao<AnalisisPOJO> dao = new ElasticsearchDao<AnalisisPOJO>(
		AnalisisPOJO.class, ElasticsearchDao.DEFAULT_RESOURCE_ANALYSIS);

	List<AnalisisPOJO> analisisPorTareaAlmacenados = dao.readAll();
	List<AnalisisPOJO> analisisPorTarea = getAnalisisPorTarea(
		analisisPorCommit, analisisPorTareaAlmacenados, metricas);

	LOG.log(Level.INFO, "Se almacenaran los analisis de sonar");
	this.storeAnalysis(analisisPorTarea, dao);

    }

    public List<SonarMetricPOJO> getMetrics() {
	return sonarExtractor.getMetrics();
    }

    /*
     * Se tienen todos los resultados del sonar pero estos resultados son
     * acumulados, lo que significa que el analisis de la revision 3 va a tener
     * la cantidad de lineas de la revision 1, 2 y 3. Para que estos analisis se
     * puedan utilizar apropiadamente hay que obtener las verdaderas metricas
     * del commit en cuestion
     */

    public List<AnalisisPOJO> getAnalisisAcumulados() {

	ElasticsearchDao<RevisionPOJO> dao = new ElasticsearchDao<RevisionPOJO>(
		RevisionPOJO.class, ElasticsearchDao.DEFAULT_RESOURCE_REVISION);

	List<RevisionPOJO> revisiones = dao.readAll();

	Collections.sort(revisiones);

	// Se sacan todas las escaneadas menos la ultima (que va a servir de
	// referencia)
	for (int i = 0; i < revisiones.size(); i++) {
	    if (!revisiones.get(i).isScaned()) {
		revisiones = revisiones.subList(i, revisiones.size());
		break;
	    }
	}

	if (revisiones.size() < 2) {
	    return new LinkedList<AnalisisPOJO>();
	}
	
	LOG.log(Level.INFO, "Se van a analizar " + revisiones.size() + " revisiones");

	String ruta;
	
	for (RevisionPOJO revision : revisiones) {

	    // Se realiza un checkout de la revision actual
	    ruta = scm.doCheckout(revision.getRevision(), revision.getRepository(),
		    WORKSPACE);

	    
	    LOG.log(Level.INFO, "Analizando " + revision.getRevision());
	    // Se analiza con sonar
	    analizar(BUILD_FILE);

	    // Se indica que dicha revision ya fue escaneada asi mas adelante no
	    // se vuelve a escanear
	    revision.setScaned(true);
	    revision.setPath(ruta);
	    dao.update(revision.getID(), revision);

	}

	// Una vez que se terminan de analizar las revisiones se pruga el
	// directorio
	purgeDirectory(WORKSPACE);

	return sonarExtractor.getResults(revisiones);

    }

    /**
     * Executa sonar runner
     * 
     */
    private void analizar(File buildFile) {
	Process p = null;
	try {

	    ProcessBuilder pb = new ProcessBuilder("./analizar.sh");
	    pb.directory(buildFile);
	    p = pb.start();

	    
	    p.waitFor();
	   

	} catch (Exception e) {
	    LOG.log(Level.SEVERE, e.toString(), e);
	}
	
    }

    /**
     * Dado un directorio elimina el contenido pero no el directorio
     */
    private void purgeDirectory(File dir) {
	LOG.log(Level.INFO, "Se limpia el directorio de trabajo");
	for (File file : dir.listFiles()) {
	    if (file.isDirectory()) {
		purgeDirectory(file);
	    }
	    file.delete();
	}
    }

    public void storeAnalysis(List<AnalisisPOJO> resultados,
	    ElasticsearchDao<AnalisisPOJO> dao) {

	for (AnalisisPOJO analisis : resultados) {
	    dao.create(analisis.getID(), analisis);
	}

    }

    public List<AnalisisPOJO> getAnalisisPorCommit(
	    List<AnalisisPOJO> analisisAcumulados,
	    List<SonarMetricPOJO> metricas) {

	/*
	 * El acumulado en [0] ya esta guardado en la db por commit, aca nomas
	 * se va a usar para tener una referencia de las metricas que no
	 * corresponden pero no se debe almacenar denuevo
	 */

	List<AnalisisPOJO> analisisPorCommit = new LinkedList<AnalisisPOJO>();

	// Por cada analisisAcumulado salvo por el primero que es de referencia
	for (int i = 1; i < analisisAcumulados.size(); i++) {

	    // Agarro los dos analisis que voy "a restar"
	    AnalisisPOJO analisisAcumuladoPrevio = analisisAcumulados
		    .get(i - 1);
	    AnalisisPOJO analisisAcumuladoActual = analisisAcumulados.get(i);
	    // Creo un nuevo analisis que es correspondiente a la revision del
	    // que se le van a restar valores
	    AnalisisPOJO nuevoAnalisisPorCommit = new AnalisisPOJO(
		    analisisAcumuladoActual.getRevision());
	    
	    LOG.log(Level.INFO, "Juntando los analisis de " + 
		    analisisAcumuladoPrevio.getRevision().getRevision() +
		    analisisAcumuladoActual.getRevision().getRevision() );

	    List<KeyValuePOJO> resultadosPrevios = analisisAcumuladoPrevio
		    .getResults();
	    List<KeyValuePOJO> resultadosActuales = analisisAcumuladoActual
		    .getResults();

	    // Por cada una de los resultados (de la forma: "lines" = "10")
	    for (int j = 0; j < resultadosActuales.size(); j++) {

		// Se crea un metricHandler, que es quien sabe que hacer con el
		// dato (caso de lineas es restar)
		// pero otro tipo de metricas requiere un tipo diferente de
		// diferencia
		Metrics metricHandler = null;
		// metric name es por ejemplo "lines"
		String metricName = resultadosActuales.get(j).getKey();
		// Los valores actuales y anteriores, por ejemplo 2 y 10, por lo
		// que el valor final debe ser 8
		String valorActual = resultadosActuales.get(j).getValue()
			.toString();
		String valorPrevio = resultadosPrevios.get(j).getValue()
			.toString();

		// dado "lines" se obtiene toda la informacion de ese tipo de
		// metrica
		SonarMetricPOJO metric = Searcher.searchMetric(metricName,
			metricas);
		// En particular el tipo (que los define Sonar), el caso de
		// lines es INT
		// Y este tipo sirve para llamar el metricHandler apropiado
		String metricType = metric.getType();

		// Si la metrica vale "null" se descarta (nunca se almacena)
		// "profile" es un dato que se guarda que no es una metrica asi
		// que se ignora
		if (!"null".equals(valorActual)
			&& !"profile".equals(metricName)
			&& !"profile_version".equals(metricName)) {

		    /*
		     * Se instancia metricHandler en el handler apropiado
		     * mediante refleccion Esto es, cada tipo de dato que define
		     * sonar para sus metricas (que se pueden ver en la api
		     * "/api/metrics") tiene una clase con el mismo nombre.
		     * 
		     * De esta forma la metrica lines que es de tipo INT (valor
		     * que toma metricType) va a ser un new de la clase INT en
		     * el paquete metric data type
		     * 
		     * Se puede implementar esta funcionalidad haciendo una
		     * cascada de if con news pero si sequiere agregar una nueva
		     * metric hay que modificar esta clase, de esta forma que
		     * solo agregar la clase nueva que sea el handler ya
		     * andaria.
		     */
		    

		    Object object = null;

		    try {
			object = Class.forName(
				"org.tesys.core.analysis.sonar.metricsdatatypes"
					+ "." + metricType).getConstructors()[1]
				.newInstance(valorActual, valorPrevio);

			metricHandler = (Metrics) object;

			// Si se encontro un handler para ese tipo
			if (metricHandler != null) {
			    // Se agrega una nueva metrica con valor por commit
			    // al analisis por commit
			    nuevoAnalisisPorCommit.add(new KeyValuePOJO(
				    metricName, metricHandler
					    .getDifferenceBetweenAnalysis()));
			    
			    
			}

		    } catch (InstantiationException | IllegalAccessException
			    | IllegalArgumentException | SecurityException
			    | InvocationTargetException
			    | ClassNotFoundException e) {
			LOG.log(Level.SEVERE, e.toString(), e);
		    }

		}

	    }

	    // Una vez calculadas todas las metricas se agrega un nuevo analisis
	    // por commit al resultado
	    analisisPorCommit.add(nuevoAnalisisPorCommit);

	}

	return analisisPorCommit;
    }

    /**
     * Junta las metricas de uno o varios commits correspondientes a la misma
     * tarea de jira
     * 
     * @param analisisJsonPorCommit
     *            analisis por commit generador por esta misma clase
     * @param revisiones
     *            los datos de las revisiones generados por la clase
     *            svnrevisions
     * @return analisis por tarea de jira
     */
    public List<AnalisisPOJO> getAnalisisPorTarea(
	    List<AnalisisPOJO> analisisPorCommit,
	    List<AnalisisPOJO> analisisPorTareaAlmacenados,
	    List<SonarMetricPOJO> metricas) {

	List<AnalisisPOJO> analisisPorTareaGuardados = analisisPorTareaAlmacenados;

	for (AnalisisPOJO commitAnalisis : analisisPorCommit) {

	    LOG.log(Level.INFO, "Procesando el analisis de " + 
		    commitAnalisis.getRevision().getProjectTrackingTask() );
	    
	    AnalisisPOJO guardado = Searcher.searchIssue(
		    analisisPorTareaGuardados, commitAnalisis.getRevision()
			    .getProjectTrackingTask());

	    if (guardado == null) {
		// Si todavia no existe la tarea se guarda el analisis sin los
		// datos propios de un commit
		commitAnalisis.getRevision().setDate(0);
		commitAnalisis.getRevision().setRevision(null);
		commitAnalisis.getRevision().setRepository(null);
		analisisPorTareaGuardados.add(commitAnalisis);
	    } else {

		AnalisisPOJO nuevoAnalisisPorTarea = new AnalisisPOJO(
			guardado.getRevision());

		List<KeyValuePOJO> resultadosPrevios = guardado.getResults();
		List<KeyValuePOJO> resultadosActuales = commitAnalisis
			.getResults();

		analisisPorTareaGuardados.remove(guardado);

		for (int j = 0; j < resultadosActuales.size(); j++) {

		    Metrics metricHandler = null;
		    String metricName = resultadosActuales.get(j).getKey();
		    Double valorActual = (Double) resultadosActuales.get(j)
			    .getValue();

		    Double valorPrevio = Searcher.searchMetricValue(
			    resultadosPrevios, metricName);
		    

		    if (valorPrevio != null) {
			SonarMetricPOJO metric = Searcher.searchMetric(
				metricName, metricas);
			String metricType = metric.getType();

			Object object = null;

			try {
			    object = Class.forName(
				    "org.tesys.core.analysis.sonar.metricsdatatypes"
					    + "." + metricType)
				    .getConstructors()[0].newInstance(
				    valorActual, valorPrevio);
			} catch (InstantiationException
				| IllegalAccessException
				| IllegalArgumentException | SecurityException
				| InvocationTargetException
				| ClassNotFoundException e) {
			    LOG.log(Level.SEVERE, e.toString(), e);
			}

			metricHandler = (Metrics) object;

			if (metricHandler != null) {

			    nuevoAnalisisPorTarea.add(new KeyValuePOJO(
				    metricName, metricHandler
					    .getNewAnalysisPerTask()));

			    
			}

		    }

		}
		analisisPorTareaGuardados.add(nuevoAnalisisPorTarea);

	    }

	}

	return analisisPorTareaGuardados;
    }

}
