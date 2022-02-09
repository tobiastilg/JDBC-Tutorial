import java.sql.*;

public class JdbcDemo {

    public static void main(String[] args) {
        System.out.println("JDBC Demo!");

        selectALlDemo();

        /*insertDemo();
        selectALlDemo();*/

        updateStudentDemo();
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
                preparedStatement.setString(1, "Martin Rieger"); //die Nummer steht für das Fragezeichen, also 1. Fragezeichen
                preparedStatement.setString(2, "martin@outlook.com");
                int rowAffected = preparedStatement.executeUpdate(); //liefert die Anzahl der betroffenen Datensätze

                System.out.println(rowAffected + " Datensatz/Datensätze eingefügt");
            } catch (SQLException ex) {
                System.out.println("Fehler beim erstellen eines Datensatzes: " + ex.getMessage());
            }

        } catch(SQLException e)  {
            System.out.println("Fehler bei Aufbau der Verbindung zur DB: " + e.getMessage());
        }
    }

    public static void updateStudentDemo() {
        System.out.println("\nUpdate DEMO mit JDBC");

        String connectionUrl = "jdbc:mysql://10.77.0.110:3306/jdbcdemo";
        String user = "root";
        String pwd = "123";

        try(Connection conn = DriverManager.getConnection(connectionUrl, user, pwd)) {
            System.out.println("Verbindung zur DB hergestellt!");

            PreparedStatement preparedStatement = conn.prepareStatement(
                    "UPDATE `student` SET `name` = ?, `email` = ? WHERE `student`.`id` = 5"); //? wegen-SQL Injection

            try {
                preparedStatement.setString(1, "Josef Reiter");
                preparedStatement.setString(2, "josef@outlook.com");
                int rowAffected = preparedStatement.executeUpdate(); //liefert die Anzahl der betroffenen Datensätze

                System.out.println(rowAffected + " Datensatz/Datensätze aktualisiert");
            } catch (SQLException ex) {
                System.out.println("Fehler beim updaten eines Datensatzes: " + ex.getMessage());
            }

        } catch(SQLException e)  {
            System.out.println("Fehler bei Aufbau der Verbindung zur DB: " + e.getMessage());
        }
    }
}
