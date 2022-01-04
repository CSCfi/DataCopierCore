package fi.csc.data;

import fi.csc.data.model.CopyRequest;
import fi.csc.data.model.Palvelu;
import io.agroal.api.AgroalDataSource;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
//import org.jboss.resteasy.annotations.jaxrs.HeaderParam;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
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
import static fi.csc.data.model.Palvelu.PalveluID.FAIRDATACLOSED;
import static fi.csc.data.model.Palvelu.PalveluID.FAIRDATAOPEN;
import static fi.csc.data.model.Palvelu.PalveluID.IDASTAGING;


@Path("/v1/copy")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CoreResource {
    private final static int OK = 200;
    public final static int AD = 403; //Forbidden
    private final static String KEYERROR = "API key was INVALID";
    public final static Response ACCESSDENIED = Response.status(AD, KEYERROR).build();
    //public final static String QUEQUENAME = "copyrequest";

    @ConfigProperty(name = "dc.apikey")
    String apikey;

    @Inject
    AgroalDataSource defaultDataSource;

    @Inject
    Logger log;

    @Transactional
    @POST
    public Response copy( CopyRequest ft, @HeaderParam("apikey") String apikeytocheck) {
        if (!apikey.equals(apikeytocheck))
            return ACCESSDENIED;
        int code = validate(ft);
        if (OK == code) {
            try  {
                Connection connection = defaultDataSource.getConnection();
                if (ft.tallenna(connection)) {
                    connection.close();
                    return Response.ok("Pyyntö lähetetty").build();
                }
                else {
                    connection.close();
                    return Response.status(500, "Pyynnön tallennus epäonnistui").build();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return Response.status(500, "Tietokantayhteysongelma").build();
            }

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
        if (!source && (!(service.type.equals(IDASTAGING) || service.type.equals(ALLAS))))
            return 418; // teapot is not a valid Destination
        return 200;
    }

}