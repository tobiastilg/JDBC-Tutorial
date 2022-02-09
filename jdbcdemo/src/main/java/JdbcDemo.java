import java.sql.*;

public class JdbcDemo {

    public static void main(String[] args) {
        System.out.println("JDBC Demo!");
        //INSERT INTO `student` (`name`, `email`) VALUES ('Mathias Rudig', 'mathias@icloud.com');
        //SELECT * FROM `student` WHERE `name` LIKE '%Tobias%';

        selectALlDemo();
    }

    public static void selectALlDemo() {
        System.out.println("Select DEMO mit JDBC");

        String connectionUrl = "jdbc:mysql://10.77.0.110:3306/jdbcdemo";
        String user = "root";
        String pwd = "123";

        try(Connection conn = DriverManager.getConnection(connectionUrl, user, pwd)) {
            System.out.println("Verbindung zur DB hergestellt!");

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM `student`");
            ResultSet rs = preparedStatement.executeQuery();

            //next() liefert solange true, bis Datensätze existieren
            while (rs.next()) {
                int id = rs.getInt("id"); //holt mir die Daten der Spalte id
                String name = rs.getString("name");
                String email = rs.getString("email");
                System.out.println("Student aus der DB: ID " + id + ", NAME " + name + ", EMAIL " + email);
            }

        } catch(SQLException e)  {
            System.out.println("Fehler bei Aufbau der Verbindung zur DB: " + e.getMessage());
        }
    }
}
