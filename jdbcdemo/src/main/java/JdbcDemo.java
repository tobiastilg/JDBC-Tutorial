import java.sql.*;

public class JdbcDemo {

    public static void main(String[] args) {
        System.out.println("JDBC Demo!");

        selectALlDemo();

        insertStudentDemo("Martin Rieger", "martin@outlook.com");
        selectALlDemo();

        updateStudentDemo(8, "Josef Reiter", "josef@outlook.com");
        selectALlDemo();

        deleteStudentDemo(8);
        selectALlDemo();

        findAllByNameLike("g");
    }

    private static void findAllByNameLike(String searchName) {
        System.out.println("\nFind all by Name DEMO mit JDBC");

        String connectionUrl = "jdbc:mysql://10.77.0.110:3306/jdbcdemo";
        String user = "root";
        String pwd = "123";

        try(Connection conn = DriverManager.getConnection(connectionUrl, user, pwd)) {
            System.out.println("Verbindung zur DB hergestellt!");

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM `student` WHERE `student`.`name` LIKE ?");
            preparedStatement.setString(1,"%"+searchName+"%");
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

    public static void insertStudentDemo(String name, String email) {
        System.out.println("\nInsert DEMO mit JDBC");

        String connectionUrl = "jdbc:mysql://10.77.0.110:3306/jdbcdemo";
        String user = "root";
        String pwd = "123";

        try(Connection conn = DriverManager.getConnection(connectionUrl, user, pwd)) {
            System.out.println("Verbindung zur DB hergestellt!");

            PreparedStatement preparedStatement = conn.prepareStatement(
                    "INSERT INTO `student` (`name`, `email`) VALUES (?, ?)"); //? wegen-SQL Injection

            try {
                preparedStatement.setString(1, name); //die Nummer steht für das Fragezeichen, also 1. Fragezeichen
                preparedStatement.setString(2, email);
                int rowAffected = preparedStatement.executeUpdate(); //liefert die Anzahl der betroffenen Datensätze

                System.out.println(rowAffected + " Datensatz/Datensätze eingefügt");
            } catch (SQLException ex) {
                System.out.println("Fehler beim erstellen eines Datensatzes: " + ex.getMessage());
            }

        } catch(SQLException e)  {
            System.out.println("Fehler bei Aufbau der Verbindung zur DB: " + e.getMessage());
        }
    }

    public static void updateStudentDemo(int id, String newName, String newEmail) {
        System.out.println("\nUpdate DEMO mit JDBC");

        String connectionUrl = "jdbc:mysql://10.77.0.110:3306/jdbcdemo";
        String user = "root";
        String pwd = "123";

        try(Connection conn = DriverManager.getConnection(connectionUrl, user, pwd)) {
            System.out.println("Verbindung zur DB hergestellt!");

            PreparedStatement preparedStatement = conn.prepareStatement(
                    "UPDATE `student` SET `name` = ?, `email` = ? WHERE `student`.`id` = ?"); //? wegen-SQL Injection

            try {
                preparedStatement.setString(1, newName);
                preparedStatement.setString(2, newEmail);
                preparedStatement.setInt(3, id);
                int rowAffected = preparedStatement.executeUpdate(); //liefert die Anzahl der betroffenen Datensätze

                System.out.println(rowAffected + " Datensatz/Datensätze aktualisiert");
            } catch (SQLException ex) {
                System.out.println("Fehler beim updaten eines Datensatzes: " + ex.getMessage());
            }

        } catch(SQLException e)  {
            System.out.println("Fehler bei Aufbau der Verbindung zur DB: " + e.getMessage());
        }
    }

    public static void deleteStudentDemo(int studentId) {
        System.out.println("\nDelete DEMO mit JDBC");

        String connectionUrl = "jdbc:mysql://10.77.0.110:3306/jdbcdemo";
        String user = "root";
        String pwd = "123";

        try(Connection conn = DriverManager.getConnection(connectionUrl, user, pwd)) {
            System.out.println("Verbindung zur DB hergestellt!");

            PreparedStatement preparedStatement = conn.prepareStatement(
                    "DELETE FROM `student` WHERE `student`.`id` = ?");

            try {
                preparedStatement.setInt(1, studentId);
                int rowAffected = preparedStatement.executeUpdate(); //liefert die Anzahl der betroffenen Datensätze

                System.out.println(rowAffected + " Datensatz/Datensätze aktualisiert");
            } catch (SQLException ex) {
                System.out.println("Fehler beim löschen eines Datensatzes: " + ex.getMessage());
            }

        } catch(SQLException e)  {
            System.out.println("Fehler bei Aufbau der Verbindung zur DB: " + e.getMessage());
        }
    }
}
