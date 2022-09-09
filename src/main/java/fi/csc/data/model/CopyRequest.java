package fi.csc.data.model;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.jboss.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <a href="https://wiki.csc.fi/SDS/DatasetCopierIDAAllasAPI">Sorry, internal specification</a>
 */
@RegisterForReflection
@Entity
@Table(name = "request")
public class CopyRequest extends PanacheEntityBase {

    private static final String INSERT = "INSERT INTO request (requester, email, source, destination) VALUES (?, ?, ?, ?)";
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

    /**
     * Tämän olion tietokantaan tallennus
     * @param con Connection SQL one (from injected pool)
     * @return int caseid which is database index
     */
    public int tallenna(Connection con) {
        int s = source.tallenna(con);
        int d = destination.tallenna(con);
        try {
            PreparedStatement statement = con.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS)
                    ;
            statement.setString(1, requester);
            statement.setBoolean(2, email);
            statement.setInt(3, s);
            statement.setInt(4, d);
            int tulos = statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    this.copyid = rs.getInt(1);
                    rs.close();
                    statement.close();
                    return copyid;
                }
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
            //con.commit(); //cause java.sql.SQLException: Attempting to commit while taking part in a transaction
            statement.close();
            con.commit();
                        if (1 == tulos) {
                            return -3;
                        } else {
                            LOG.error("Database write return: "+tulos);
                            return -2;
                        }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
