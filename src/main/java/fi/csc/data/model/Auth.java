package fi.csc.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//import javax.persistence.*;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*@Entity
@Table(name = "auth")*/
@JsonIgnoreProperties
public class Auth  /*extends PanacheEntityBase*/  implements Serializable{

    private static final long serialVersionUID = 56630575L;
    private static final String INSERT = "INSERT INTO auth (username, accessKey, " +
            "secretKey, projectID, token) VALUES (?, ?, ?, ?, ?)";
    /*@Column(name = "authid", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    int authid;*/
    public String username;
    public String accessKey;
    public String secretKey;
    public String projectID;
    public String token; // sekä app että auth

    public int tallenna(Connection con) {
        try {
            PreparedStatement stmnt = con.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
            safetywrite(username, 1 , stmnt);
            safetywrite(accessKey, 2 , stmnt);
            safetywrite(secretKey, 3 , stmnt);
            safetywrite(projectID, 4 , stmnt);
            safetywrite(token, 5 , stmnt);
            int tulos = stmnt.executeUpdate();
            ResultSet rs = stmnt.getGeneratedKeys();
            if (rs.next()) {
                    int caseid = rs.getInt(1);
                    stmnt.close();
                    return caseid;
                    }
            stmnt.close();
        } catch (SQLException s) {

                s.printStackTrace();
            }
        return -1;
    }

    private void safetywrite(String hm, int pos, PreparedStatement s) {
        try {
        if (null != hm)
            s.setString(pos, hm);
        else
            s.setNull(pos, java.sql.Types.NULL);
        } catch (SQLException e) {
                e.printStackTrace();
            }
    }
}
