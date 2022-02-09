import java.sql.*;

public class JdbcDemo {

    public static void main(String[] args) {
        System.out.println("JDBC Demo!");

        selectALlDemo();
        insertDemo();
        selectALlDemo();
    }

    public static void selectALlDemo() {
        System.out.println("\nSelect DEMO mit JDBC");

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

    public static void insertDemo() {
        System.out.println("\nInsert DEMO mit JDBC");

        String connectionUrl = "jdbc:mysql://10.77.0.110:3306/jdbcdemo";
        String user = "root";
        String pwd = "123";

        try(Connection conn = DriverManager.getConnection(connectionUrl, user, pwd)) {
            System.out.println("Verbindung zur DB hergestellt!");

            PreparedStatement preparedStatement = conn.prepareStatement(
                    "INSERT INTO `student` (`name`, `email`) VALUES (?, ?)"); //? wegen-SQL Injection

            try {
                preparedStatement.setString(1, "Clemens Kerber");
                preparedStatement.setString(2, "clemens@hotmail.com");
                int rowAffected = preparedStatement.executeUpdate(); //liefert die Anzahl der betroffenen Datensätze

                System.out.println(rowAffected + " Datensätze eingefügt");
            } catch (SQLException ex) {
                System.out.println("Fehler beim erstellen eines Datensatzes: " + ex.getMessage());
            }

        } catch(SQLException e)  {
            System.out.println("Fehler bei Aufbau der Verbindung zur DB: " + e.getMessage());
        }
    }
}
