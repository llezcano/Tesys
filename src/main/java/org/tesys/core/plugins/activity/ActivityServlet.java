package org.tesys.core.plugins.activity;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;


@Path("/plugins/activity")
@Singleton
public class ActivityServlet {
    
    private Activity activity ;
    
    @PostConstruct
    public void Init() {
        activity = Activity.getInstance() ;
    }
    
    @PUT
    @Path("/{user}/reading")
  //  @Consumes(MediaType.TEXT_PLAIN)
    public void reading( @PathParam("user") String key, @QueryParam("mins") Integer mins,  Integer data ) {
        activity.reading( key, mins, data );
    }

    @PUT
    @Path("/{user}/writing")
    public void writing( @PathParam("user") String key, @QueryParam("mins") Integer mins, Integer data ) {
        activity.writing( key, mins, data );

    }
    
    
}
