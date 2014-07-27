package org.tesys.core.project.scm;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/scm")
public class SCMManager {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SvnDataPOJO sayHello() {
        SvnDataPOJO s = new SvnDataPOJO();
        s.setMessage("hola");
        s.setRepository("como");
        s.setUser("andas");
        return s;
    }
}