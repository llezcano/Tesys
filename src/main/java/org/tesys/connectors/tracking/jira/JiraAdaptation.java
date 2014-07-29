package org.tesys.connectors.tracking.jira;
import org.tesys.core.project.tracking.IssuePOJO;
import org.tesys.core.project.tracking.UserPOJO;
import org.tesys.connectors.tracking.jira.model.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.tesys.core.project.tracking.Issue;
import org.tesys.core.project.tracking.User;
import org.tesys.util.InputOutput;
import org.tesys.util.JSONFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;



public class JiraAdaptation {
	
	private JsonNode issueSchema ;
	
	private JsonNode userSchema ;
	
	private JiraRESTClient client ;
	
	private final static Integer MAX_SIZE_ISSUE_QUERY = 1000 ;
	private final static Integer MAX_SIZE_USER_QUERY = 49 ;
	
	/** 
	 * MAIN for testing
	 * @param args
	 * @throws IOException
	 */
	public static void main (String args[]) throws IOException {
		JiraRESTClient client = new JiraRESTClient("http://ing.exa.unicen.edu.ar:8086/atlassian-jira-6.0/", "grodriguez", "654321") ;

		String user = InputOutput.readFile( "userSchema", Charset.defaultCharset()) ;
		String issue = InputOutput.readFile( "issueSchema", Charset.defaultCharset()) ;
		
		JiraAdaptation jira = new JiraAdaptation(client, user, issue ) ;
		
		
	}
	
	public JiraAdaptation(JiraRESTClient jiraClient, String userJsonSchema, String issueJsonSchema) throws JsonProcessingException, IOException {	
		client = jiraClient ;
		ObjectMapper mapper = new ObjectMapper();
		issueSchema = mapper.readTree(issueJsonSchema) ;
		userSchema = mapper.readTree(userJsonSchema) ;		
	}
		
	/**
	 * Realiza una consulta (con poco costo) para conocer la cantidad Issues en Jira
	 * @return	Cantidad de Issues de Jira
	 */
	public Integer getIssuesSize() {
		String response_form_client = client.getIssues("", 0, 1 ) ; // Esto es para consultar el tamaño maximo de issues
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readTree(response_form_client).path("total").asInt();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	
	/**
	 *  Para probar las consultas JQL ingresar aqui http://ing.exa.unicen.edu.ar:8086/atlassian-jira-6.0/issues/
	 * y hacer click "Busqueda Avanzada".
	 *  
	 *  Parseo y envio los issues (tareas)
	 * 
	 * @param jql 						Consulta realizada al cliente en Java Query Language.
	 * @param start						Scroll start
	 * @param end						Scroll end 
	 * @return							Issues que satisfacen la Query, la cantidad resultante sera igual a (start - end)
	 * @throws JsonProcessingException 
	 * @throws IOException
	 * @throws ClassCastException
	 */
	public Issue[] getIssues(String jql, Integer start, Integer end) throws JsonProcessingException, IOException, ClassCastException {
		// TODO validate params
		Integer size = ( MAX_SIZE_ISSUE_QUERY < end ) ? MAX_SIZE_ISSUE_QUERY : end ; 
		// Init vars
		Iterator<JsonNode> it ;
		JSONFilter jf =  new JSONFilter() ;		
		boolean hasMore = true ;
		ObjectMapper mapper = new ObjectMapper();
		Issue[] issuesPOJO = new IssuePOJO[ end-start ] ; 
		int count = 0 ;
		
		while (hasMore && count < end) {
			String response_form_client = client.getIssues(jql, start, size ) ;
			ArrayNode issues = (ArrayNode) mapper.readTree(response_form_client).path("issues") ;
			it = issues.elements() ;	
			hasMore = it.hasNext() ;
			JsonNode issue = null ;	
			
			//Parsing response
			while (it.hasNext()) {
				issue = it.next() ;
				JsonNode jsonIssue =  jf.filter(issue, issueSchema) ;				
				JiraIssue i = mapper.readValue(jsonIssue.toString(), JiraIssue.class); 
				IssuePOJOAdaptor adaptor = new IssuePOJOAdaptor() ;
				issuesPOJO[count++] = adaptor.adapt(i) ;
				it.remove() ;
			}
			start += size ;	//NEXT SCROLL	
		}
		return issuesPOJO;	
	}
	
	/**
	 * Devuelve todos los Issues del cliente Jira.
	 * 
	 * @return Arreglo de Issues.
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public Issue[] getAllIssues() throws JsonProcessingException, IOException  {
		return getIssues("", 0, getIssuesSize()) ;
	}
	
	/**
	 * Devuelve el issue que machea con la Key dada
	 * 
	 * @param key 				Valor clave del issue (es unico)
	 * @return					Issue que cumple con la key
	 * @throws JsonProcessingException
	 * @throws IOException
	 * @throws ClassCastException
	 */
	public Issue getIssue(String key) throws JsonProcessingException, IOException, ClassCastException  {
		return getIssues("key="+key, 0, 1)[0] ;
	}
		
	/**
	 * Realiza una consulta (con poco costo) para conocer la cantidad Usuarios en Jira
	 * @return	Cantidad de usuarios de Jira
	 */
	public Integer getUsersSize() {
		String clientJsonResponse = client.getUsers("jira-developers", 0, 1 ) ;
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readTree(clientJsonResponse).path("users").path("size").asInt() ;
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	

	public User getUser(String name) throws JsonProcessingException, IOException {
		JSONFilter jf =  new JSONFilter() ;
		ObjectMapper mapper = new ObjectMapper();
		JsonNode user = mapper.readTree(client.getUser(name)) ;
		JsonNode formatedUser =  jf.filter(user, userSchema) ; 
		UserPOJO u = mapper.readValue(formatedUser.toString(), UserPOJO.class); 
		return ( u.getName() == null ) ? null : u ;
		
	}
	
	public User[] getAllUsers() throws JsonProcessingException, IOException{
		return getUsers(0, getUsersSize()) ;
	}
	
	public User[] getUsers(Integer start, Integer end) throws JsonProcessingException, IOException{
		Integer size = (MAX_SIZE_USER_QUERY < end) ? MAX_SIZE_ISSUE_QUERY : end ;
		Iterator<JsonNode> it ;
		JSONFilter jf =  new JSONFilter() ;
		
		boolean hasMore = true ;

		User[] usersPOJO = new UserPOJO[end-start] ; 
		
		ObjectMapper mapper = new ObjectMapper();

		// Parseo y envio los users

		int count = 0 ;
		while (hasMore && count < end ) {
			String clientJsonResponse = client.getUsers("jira-developers", start, end ) ;
		
			ArrayNode users = (ArrayNode) ( mapper.readTree(clientJsonResponse).path("users").path("items")) ;	
		
			it = users.elements() ;
			JsonNode user = null ;
			hasMore = it.hasNext() ;
			while (it.hasNext()) {
			
				user = it.next() ;
				JsonNode json =  jf.filter(user, userSchema) ; 
				
				UserPOJO u = mapper.readValue(json.toString(), UserPOJO.class); 
				usersPOJO[count++] = u ;
				
			}
			start+= size ;	
		}
		return usersPOJO;
			
	}
	
}
