package fi.csc.data.model;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.jboss.logging.Logger;

import javax.persistence.Id;


/**
 * <a href="https://wiki.csc.fi/SDS/DatasetCopierIDAAllasAPI">Sorry, internal specification</a>
 */
@RegisterForReflection
/*@Entity
@Table(name = "request")*/
public class CopyRequest /* extends PanacheEntityBase */ {

    private static final String SELECT = "SELECT copyid, email, status, MB, nofiles, wallclock FROM request WHERE copyid=?";
    private static final Logger LOG = Logger.getLogger(CopyRequest.class);
    private static final long serialVersionUID = 56630571L;

    @Id
    /*@GeneratedValue(strategy = GenerationType.IDENTITY)*/
    public int copyid;
    public String requester;
    public boolean email;
    public Palvelu source;
    public Palvelu destination;
    public int status;
    public int MB;
    public int nofiles;
    public double wallclock;

    public CopyRequest(int id, boolean email, int status, int MB, int nofiles, double wallclock) {
        this.copyid = id;
        this.email = email;
        this.status = status;
        this.MB = MB;
        this.nofiles = nofiles;
        this.wallclock = wallclock;
        this.requester = "censored";
    }
    public static CopyRequest findById(int id, Connection con) {
       try {
            PreparedStatement statement = con.prepareStatement(SELECT);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return new CopyRequest(rs.getInt(1), rs.getBoolean(2),
                        rs.getInt(3), rs.getInt(4), rs.getInt(5),
                rs.getDouble(6));
            }
           } catch (SQLException e) {
            e.printStackTrace();
        }
       return null;
    }

}
