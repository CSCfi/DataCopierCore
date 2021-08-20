package fi.csc.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Auth {
    String username;
    String accessKey;
    String secretKey;
    String projectID;
    String token; // sekä app että auth
}
