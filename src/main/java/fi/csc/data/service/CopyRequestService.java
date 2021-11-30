package fi.csc.data.service;

import fi.csc.data.model.CopyRequest;
import javax.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * Tätä käytetään Enginen puolella (mutta tämä on täällä coressa, koska mallikin on coressa)
 */
@ApplicationScoped
public class CopyRequestService {

    public List<CopyRequest> findNullStatus() {
        return (List<CopyRequest>)CopyRequest.find("status is null", new String[]{});
    }

}
