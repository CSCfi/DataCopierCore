package fi.csc.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Tässä on yhdistetty https://wiki.csc.fi/SDS/DatasetCopierIDAAllasAPI Params-käsite
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OmistajaPolku {
    public String omistaja; //ProjectArea, Project, Bucket
    public String polku; //Path, DownloadURL, EtsinPID,
}
