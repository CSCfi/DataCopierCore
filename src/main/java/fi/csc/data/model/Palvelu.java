package fi.csc.data.model;

/**
 * https://wiki.csc.fi/SDS/DatasetCopierIDAAllasAPI
 */
public class Palvelu {

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
