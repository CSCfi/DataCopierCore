package fi.csc.data;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/run")
@RegisterRestClient(configKey="engine-api")
public interface RunService {
    @GET
    @Path("/{id}")
    String runById(@PathParam int id);
}
