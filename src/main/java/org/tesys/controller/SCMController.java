package org.tesys.controller;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.tesys.core.project.scm.InvalidCommitException;
import org.tesys.core.project.scm.SCMManager;
import org.tesys.core.project.scm.ScmPostCommitDataPOJO;
import org.tesys.core.project.scm.ScmPreCommitDataPOJO;


@Path("/scm")
@Singleton
public class SCMController {

  private static final String FAIL_CODE = "0";
  private static final String OK_CODE = "1";
  private SCMManager scmManager;


  @PostConstruct
  public void init() {
    scmManager = SCMManager.getInstance();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public String isCommitAllowed(ScmPreCommitDataPOJO scmData) {

    try {
      if( scmManager.isCommitAllowed(scmData) ) {
        return OK_CODE;
      }
    } catch (InvalidCommitException e) {
      return e.getMessage();
    }
    return FAIL_CODE;
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public String storeCommit(ScmPostCommitDataPOJO scmData) {

    try {
      if (scmManager.storeCommit(scmData) ) {
        return OK_CODE;
      }
    } catch (RuntimeException e) {
      return e.getMessage();
    }
    return FAIL_CODE;
  }


}
