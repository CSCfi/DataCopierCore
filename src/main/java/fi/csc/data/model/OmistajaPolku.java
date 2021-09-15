package fi.csc.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Tässä on yhdistetty https://wiki.csc.fi/SDS/DatasetCopierIDAAllasAPI Params-käsite
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OmistajaPolku  implements Serializable {
    private static final long serialVersionUID = 56630573L;
    public String omistaja; //ProjectArea, Project, Bucket
    public String polku; //Path, DownloadURL, EtsinPID,
}
