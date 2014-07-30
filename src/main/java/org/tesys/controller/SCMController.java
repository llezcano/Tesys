package org.tesys.controller;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.tesys.core.project.scm.SCMManager;
import org.tesys.core.project.scm.ScmPostCommitDataPOJO;
import org.tesys.core.project.scm.ScmPreCommitDataPOJO;


@Path("/scm")
@Singleton
public class SCMController {
  
  private SCMManager scmManager;
  
  
  @PostConstruct
  public void init() {
    scmManager = SCMManager.getInstance();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public String isCommitAllowed(ScmPreCommitDataPOJO scmData) {
    
    return scmManager.isCommitAllowed(scmData);
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public String storeCommit(ScmPostCommitDataPOJO scmData) {

    return scmManager.storeCommit(scmData);
  }


}
