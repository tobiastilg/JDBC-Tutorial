import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcDemo {

    public static void main(String[] args) {
        System.out.println("JDBC Demo!");
        //INSERT INTO `student` (`name`, `email`) VALUES ('Mathias Rudig', 'mathias@icloud.com');
        //SELECT * FROM `student` WHERE `name` LIKE '%Tobias%';

        selectALlDemo();
    }

    public static void selectALlDemo() {
        System.out.println("Select DEMO mit JDBC");
        String sqlSelectAllPersons = "SELECT * FROM `student`";

        String connectionUrl = "jdbc:mysql://10.77.0.110:3306/jdbcdemo";
        String user = "root";
        String pwd = "123";

        try(Connection conn = DriverManager.getConnection(connectionUrl, user, pwd)) {
            System.out.println("Verbindung zur DB hergestellt!");
        } catch(SQLException e)  {
            System.out.println("Fehler bei Aufbau der Verbindung zur DB: " + e.getMessage());
        }
    }
}
