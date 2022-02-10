package dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Datenbankverbindung mittels Singleton Pattern
 */
public class MySqlDatabaseConnection {

    private static Connection con = null;

    /**
     * Privater Konstruktor (kein new Object() möglich)
     */
    private MySqlDatabaseConnection() {
    }

    /**
     * Wird von außen aufgerufen und stellt die Verbindung zur Datenbank her inkl. Überprüfung ob sie bereits existiert
     * @param url
     * @param user
     * @param pwd
     * @return Database connection
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static Connection getConnection(String url, String user, String pwd) throws ClassNotFoundException, SQLException {
        if (con != null) {
            return con;
        } else {
            Class.forName("com.mysql.cj.jdbc.Driver"); //überprüfen oder Diver Klasse existiert - wirft ClassNotFoundException
            con = DriverManager.getConnection(url, user, pwd); //wirft SQLException
            return con;
        }
    }
}
