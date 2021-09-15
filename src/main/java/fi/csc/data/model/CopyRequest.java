package fi.csc.data.model;

import java.io.Serializable;

/**
 * https://wiki.csc.fi/SDS/DatasetCopierIDAAllasAPI
 */
public class CopyRequest implements Serializable  {

    private static final long serialVersionUID = 56630571L;

    public String requester;
    public Palvelu source;
    public Palvelu destination;

}
