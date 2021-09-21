package fi.csc.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Connection;

@Entity
@Table(name = "auth")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Auth  extends PanacheEntityBase implements Serializable{

    private static final long serialVersionUID = 56630575L;
    @Column(name = "authid", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    int authid;
    String username;
    String accessKey;
    String secretKey;
    String projectID;
    String token; // sekä app että auth

}
