package fi.csc.data.model;

import java.io.Serializable;

/**
 * https://wiki.csc.fi/SDS/DatasetCopierIDAAllasAPI
 */
public class Palvelu implements Serializable {

    private static final long serialVersionUID = 56630574L;

    public enum PalveluID {
        IDA,
        IDASTAGING,
        FAIRDATAOPEN,
        FAIRDATACLOSED,
        ALLAS,
        ALLASPUBLIC
    }

    public enum Protokolla {
        S3,
        SWIFT
    }

    public PalveluID type;
    public Protokolla protocol;
    public Auth auth;
    public OmistajaPolku param;
}
