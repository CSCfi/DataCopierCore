package fi.csc.data;

import fi.csc.data.model.CopyRequest;
import fi.csc.data.model.Palvelu;
//import io.smallrye.config.ConfigMapping;
//import io.smallrye.config.WithName;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/v1/copy")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CoreResource {

    @POST
    public Response copy( CopyRequest ft) {
        int code = validate(ft);
        if (200 == code) {
            return Response.ok("Vain toteutus puuttuu").build();
        } else
            return Response.status(code).build();
    }


    private int validate(CopyRequest ft) {
        if (null == ft.requester)
            return 403;
        if (null == ft.source)
            return 400;
        if (null == ft.destination)
            return 400;
        if (ft.source.type.equals(ft.destination.type))
            return 409;
        return 200;
    }

}