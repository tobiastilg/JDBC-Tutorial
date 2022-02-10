import dataaccess.MySqlDatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        String connectionUrl = "jdbc:mysql://10.77.0.110:3306/kurssystem";
        String user = "root";
        String pwd = "123";

        //um Verbindungsaufbau-Exceptions kümmern
        try {
            Connection myConnection = MySqlDatabaseConnection.getConnection(connectionUrl, user, pwd);
            System.out.println("Datenbankverbindung aufgebaut!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
