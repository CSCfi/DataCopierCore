package fi.csc.data.services;

import fi.csc.data.model.CopyRequest;

import javax.enterprise.context.ApplicationScoped;
import java.sql.Connection;

@ApplicationScoped
public class CopyRequestService {

    public CopyRequest  getById(int id, Connection con) {
        return CopyRequest.findById(id, con);
    }
}
