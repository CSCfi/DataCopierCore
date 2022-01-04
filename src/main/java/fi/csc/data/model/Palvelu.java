package fi.csc.data.model;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * https://wiki.csc.fi/SDS/DatasetCopierIDAAllasAPI
 */
public class Palvelu implements Serializable {

    private static final String INSERT = "INSERT INTO palvelu (PalveluID, Protokolla, Auth, omistaja, polku) VALUES (?, ?, ?, ?, ?)";
    private static final long serialVersionUID = 56630574L;

    public enum PalveluID {
        IDA(1),
        IDASTAGING(2),
        FAIRDATAOPEN(3),
        FAIRDATACLOSED(4),
        ALLAS(5),
        ALLASPUBLIC(6),
        B2DROP(7);

        private final int no;

        PalveluID(int i) {
            this.no = i;
        }

        public int getNo() {
            return no;
        }
    }

    public enum Protokolla {
        S3(1),
        SWIFT(2);

        private final int no;

        Protokolla(int i) {
            this.no = i;
        }

        public int getNo() {
            return no;
        }
    }

    public PalveluID type;
    public Protokolla protocol;
    public Auth auth;
    public OmistajaPolku param;

    public int tallenna(Connection con) {
        int authid = 0;
        if (null != auth)
            authid = auth.tallenna(con);
        else
            System.err.println("Auth was null");
        try {
            PreparedStatement stmnt = con.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
            stmnt.setInt(1, type.getNo());
            if (null != protocol)
                stmnt.setInt(2, protocol.getNo());
            else
                stmnt.setNull(2,java.sql.Types.NULL);
            if (null != auth)
                stmnt.setInt(3, authid);
            else
                stmnt.setNull(3, java.sql.Types.NULL);
            if (null != param.omistaja)
                stmnt.setString(4, param.omistaja);
            else
                stmnt.setNull(4, java.sql.Types.NULL);
            stmnt.setString(5, param.polku);
            int tulos = stmnt.executeUpdate();
            try (ResultSet rs = stmnt.getGeneratedKeys()) {
                if (rs.next()) {
                    int caseid = rs.getInt(1);
                    stmnt.close();
                    return caseid;
                }
            } catch (SQLException s) {
                s.printStackTrace();
            }
            stmnt.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return -1;
    }
}
