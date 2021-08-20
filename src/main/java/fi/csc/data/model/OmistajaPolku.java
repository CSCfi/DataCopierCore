package fi.csc.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Tässä on yhdistetty https://wiki.csc.fi/SDS/DatasetCopierIDAAllasAPI Params-käsite
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OmistajaPolku {
    String omistaja; //ProjectArea, Project, Bucket
    String polku; //Path, DownloadURL, EtsinPID,
}
