package fi.csc.data.services;

import fi.csc.data.RequestHeaderFactory;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/run")
@RegisterRestClient(configKey="engine-api")
@RegisterClientHeaders(RequestHeaderFactory.class)
public interface RunService {
    @GET
    @Path("/{id}")
    String runById(@PathParam int id);
}
