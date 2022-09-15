package fi.csc.data;

import fi.csc.data.model.CopyRequest;
import fi.csc.data.model.Palvelu;
import fi.csc.data.services.CopyRequestService;
import fi.csc.data.services.RunService;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;


import java.sql.Connection;
import java.sql.SQLException;

import static fi.csc.data.model.Palvelu.PalveluID.ALLAS;
import static fi.csc.data.model.Palvelu.PalveluID.ALLASPUBLIC;
import static fi.csc.data.model.Palvelu.PalveluID.B2DROP;
import static fi.csc.data.model.Palvelu.PalveluID.FAIRDATACLOSED;
import static fi.csc.data.model.Palvelu.PalveluID.FAIRDATAOPEN;
import static fi.csc.data.model.Palvelu.PalveluID.IDASTAGING;


@Path("/v1/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CoreResource {

    private final static int OK = 200;
    public final static int AD = 403; //Forbidden
    private final static String KEYERROR = "API key was INVALID";
    public final static Response ACCESSDENIED = Response.status(AD, KEYERROR).build();


    @ConfigProperty(name = "dc.apikey")
    String apikey;
    @Inject
    @RestClient
    RunService runService;
    @Inject
    CopyRequestService crs;
    @Inject
    AgroalDataSource defaultDataSource;
     @Inject
     @DataSource("write")
     AgroalDataSource writeDataSource;

    @Inject
    Logger log;

    //@Transactional
    @POST
    @Path("copy")
    public Response copy( CopyRequest ft, @HeaderParam("Apikey") String apikeytocheck) {
        if (!apikey.equals(apikeytocheck)) {
            log.error("Invalid Apikey: "+ apikeytocheck);
            return ACCESSDENIED;
        }


        int code = validate(ft);
        if (OK == code) {
            try  {
                Connection connection = writeDataSource.getConnection();
                int id = ft.tallenna(connection);
                if (id > 0) {
                    connection.commit();
                    connection.close();
                    runService.runById(id);
                    return Response.ok(id+"\n").build();
                } else {
                    connection.close();
                    return Response.status(500, "Pyynnön tallennus epäonnistui\n").build();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return Response.status(500, "Tietokantayhteysongelma\n").build();
            }

        } else
            return Response.status(code).build();
    }
    @Path("status/{id}")
    @GET
    public Response status(@PathParam("id") Integer id, @HeaderParam("Apikey") String apikeytocheck) {
        if (!apikey.equals(apikeytocheck)) {
            log.error("Invalid Apikey: "+ apikeytocheck);
            return ACCESSDENIED;
        }
        try {
            CopyRequest cr = crs.getById(id, defaultDataSource.getConnection());
            if (null == cr) {
                return Response.status(404, "Can't find any information by id: " + id).build();
            } else {
                return Response.ok(cr).build();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return Response.status(500, "Tietokantayhteysongelma\n").build();
        }
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
        int code = validatePalvelu(ft.source, true);
        if (OK != code)
            return code;
        return validatePalvelu(ft.destination, false);
    }

    private int validatePalvelu(Palvelu service, boolean source) {
        if (null == service.auth)
            if (!source && !((service.type.equals(ALLASPUBLIC) || service.type.equals(FAIRDATAOPEN)))) {
                log.error("Mandatory Auth object missing");
                return 403;
            }
        if (null == service.param)
            return 400;
        if (null == service.param.polku)
            return 400;
        if (!(service.type.equals(FAIRDATAOPEN) || service.type.equals(FAIRDATACLOSED)))
                if (null == service.param.omistaja)
                    return 400;
        if (service.type.equals(ALLASPUBLIC) || service.type.equals(ALLAS))
            if (null == service.protocol)
                return 402; //missig protocol even code is payment
        if (!source && (!(service.type.equals(IDASTAGING)
                || service.type.equals(ALLAS)
                || service.type.equals(B2DROP))))
            return 418; // teapot is not a valid Destination
        return 200;
    }

}