package fi.csc.data.model;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.jboss.logging.Logger;

/**
 * https://wiki.csc.fi/SDS/DatasetCopierIDAAllasAPI
 */
@RegisterForReflection
public class CopyRequest {

    private static final String INSERT = "INSERT INTO request (requester, source, destination) VALUES (?, ?, ?)";
    private static final Logger LOG = Logger.getLogger(CopyRequest.class);
    private static final long serialVersionUID = 56630571L;

    /*@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int copyid;*/
    public String requester;
    public Palvelu source;
    public Palvelu destination;
    public int status;

    public boolean tallenna(Connection con) {
        int s = source.tallenna(con);
        int d = destination.tallenna(con);
        try {
            PreparedStatement statement = con.prepareStatement(INSERT/*, PreparedStatement.RETURN_GENERATED_KEYS*/);
            statement.setString(1, requester);
            statement.setInt(2, s);
            statement.setInt(3, d);
            int tulos = statement.executeUpdate();
            //con.commit(); //cause java.sql.SQLException: Attempting to commit while taking part in a transaction
            statement.close();
                        if (1 == tulos) {
                            return true;
                        } else {
                            LOG.error("Database write return: "+tulos);
                            return false;
                        }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
