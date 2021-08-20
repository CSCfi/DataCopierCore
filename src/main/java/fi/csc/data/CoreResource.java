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

    @POST
    public Response copy( CopyRequest ft) {
        int code = validate(ft);
        if (OK == code) {
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
        int code = validatePalvelu(ft.source, true);
        if (OK != code)
            return code;
        return validatePalvelu(ft.destination, false);
    }

    private int validatePalvelu(Palvelu service, boolean source) {
        if (null == service.auth)
            if (!source && (service.type.equals(ALLASPUBLIC) || service.type.equals(FAIRDATAOPEN)))
                return 403;
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