package fi.csc.data.services;

import fi.csc.data.model.CopyRequest;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CopyRequestService {

    public CopyRequest  getById(int id) {
        return CopyRequest.findById(id);
    }
}
