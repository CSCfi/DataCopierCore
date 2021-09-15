package fi.csc.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Auth implements Serializable {

    private static final long serialVersionUID = 56630575L;

    String username;
    String accessKey;
    String secretKey;
    String projectID;
    String token; // sekä app että auth
}
