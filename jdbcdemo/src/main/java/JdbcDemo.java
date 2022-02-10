import java.sql.*;

public class JdbcDemo {

    public static void main(String[] args) {
        System.out.println("JDBC Demo!");

        /*selectAllStudentsDemo();

        insertStudentDemo("Martin Rieger", "martin@outlook.com");
        selectAllStudentsDemo();

        updateStudentDemo(10, "Josef Reiter", "josef@outlook.com");
        selectAllStudentsDemo();

        deleteStudentDemo(10);
        selectAllStudentsDemo();

        findAllStudentsByNameLike("g");*/

        selectAllAddressesDemo();
        findAllAddressesByPostalCode("64");

        insertAddressDemo("Ringstraße", "15b", "Wien", "1010");
        selectAllAddressesDemo();

        updateAddressDemo(6, "Teststraße", "1", "Zams", "6511");
        selectAllAddressesDemo();

        deleteAddressDemo(6);
        selectAllAddressesDemo();
    }

    private static void findAllStudentsByNameLike(String searchName) {
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

    private static void selectAllStudentsDemo() {
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

    private static void insertStudentDemo(String name, String email) {
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

    private static void updateStudentDemo(int id, String newName, String newEmail) {
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

    private static void deleteStudentDemo(int studentId) {
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

                System.out.println(rowAffected + " Datensatz/Datensätze gelöscht");
            } catch (SQLException ex) {
                System.out.println("Fehler beim löschen eines Datensatzes: " + ex.getMessage());
            }

        } catch(SQLException e)  {
            System.out.println("Fehler bei Aufbau der Verbindung zur DB: " + e.getMessage());
        }
    }

    private static void selectAllAddressesDemo() {
        System.out.println("\nSelect DEMO mit JDBC");

        String connectionUrl = "jdbc:mysql://10.77.0.110:3306/jdbcdemo";
        String user = "root";
        String pwd = "123";

        try(Connection conn = DriverManager.getConnection(connectionUrl, user, pwd)) {
            System.out.println("Verbindung zur DB hergestellt!");

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM `address`");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String street = rs.getString("street");
                String houseNumber = rs.getString("house_number");
                String location = rs.getString("location");
                String plz = rs.getString("postal_code");
                System.out.println("Adresse aus der DB: ID " + id + ", Straße " + street + ", Hausnummer " + houseNumber +
                        ", Ort " + location + ", Postleitzahl " + plz);
            }

        } catch(SQLException e)  {
            System.out.println("Fehler bei Aufbau der Verbindung zur DB: " + e.getMessage());
        }
    }

    private static void findAllAddressesByPostalCode(String postalCode) {
        System.out.println("\nFind all by Name DEMO mit JDBC");

        String connectionUrl = "jdbc:mysql://10.77.0.110:3306/jdbcdemo";
        String user = "root";
        String pwd = "123";

        try(Connection conn = DriverManager.getConnection(connectionUrl, user, pwd)) {
            System.out.println("Verbindung zur DB hergestellt!");

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM `address` WHERE `address`.`postal_code` LIKE ?");
            preparedStatement.setString(1,"%"+postalCode+"%");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String street = rs.getString("street");
                String houseNumber = rs.getString("house_number");
                String location = rs.getString("location");
                String plz = rs.getString("postal_code");
                System.out.println("Adresse aus der DB: ID " + id + ", Straße " + street + ", Hausnummer " + houseNumber +
                        ", Ort " + location + ", Postleitzahl " + plz);
            }

        } catch(SQLException e)  {
            System.out.println("Fehler bei Aufbau der Verbindung zur DB: " + e.getMessage());
        }
    }

    private static void insertAddressDemo(String street, String houseNumber, String location, String plz) {
        System.out.println("\nInsert DEMO mit JDBC");

        String connectionUrl = "jdbc:mysql://10.77.0.110:3306/jdbcdemo";
        String user = "root";
        String pwd = "123";

        try(Connection conn = DriverManager.getConnection(connectionUrl, user, pwd)) {
            System.out.println("Verbindung zur DB hergestellt!");

            PreparedStatement preparedStatement = conn.prepareStatement(
                    "INSERT INTO `address` (`street`, `house_number`, `location`, `postal_code`) VALUES (?, ?, ?, ?)");

            try {
                preparedStatement.setString(1, street);
                preparedStatement.setString(2, houseNumber);
                preparedStatement.setString(3, location);
                preparedStatement.setString(4, plz);
                int rowAffected = preparedStatement.executeUpdate();

                System.out.println(rowAffected + " Datensatz/Datensätze eingefügt");
            } catch (SQLException ex) {
                System.out.println("Fehler beim erstellen eines Datensatzes: " + ex.getMessage());
            }

        } catch(SQLException e)  {
            System.out.println("Fehler bei Aufbau der Verbindung zur DB: " + e.getMessage());
        }
    }

    private static void updateAddressDemo(int id, String newStreet, String newHouseNumber, String newLocation, String newPlz) {
        System.out.println("\nInsert DEMO mit JDBC");

        String connectionUrl = "jdbc:mysql://10.77.0.110:3306/jdbcdemo";
        String user = "root";
        String pwd = "123";

        try(Connection conn = DriverManager.getConnection(connectionUrl, user, pwd)) {
            System.out.println("Verbindung zur DB hergestellt!");

            PreparedStatement preparedStatement = conn.prepareStatement(
                    "UPDATE `address` SET `street` = ?, `house_number` = ?, `location` = ?, `postal_code` = ? WHERE `address`.`id` = ?");

            try {
                preparedStatement.setString(1, newStreet);
                preparedStatement.setString(2, newHouseNumber);
                preparedStatement.setString(3, newLocation);
                preparedStatement.setString(4, newPlz);
                preparedStatement.setInt(5, id);
                int rowAffected = preparedStatement.executeUpdate();

                System.out.println(rowAffected + " Datensatz/Datensätze aktualisiert");
            } catch (SQLException ex) {
                System.out.println("Fehler beim erstellen eines Datensatzes: " + ex.getMessage());
            }

        } catch(SQLException e)  {
            System.out.println("Fehler bei Aufbau der Verbindung zur DB: " + e.getMessage());
        }
    }

    private static void deleteAddressDemo(int addressId) {
        System.out.println("\nDelete DEMO mit JDBC");

        String connectionUrl = "jdbc:mysql://10.77.0.110:3306/jdbcdemo";
        String user = "root";
        String pwd = "123";

        try(Connection conn = DriverManager.getConnection(connectionUrl, user, pwd)) {
            System.out.println("Verbindung zur DB hergestellt!");

            PreparedStatement preparedStatement = conn.prepareStatement(
                    "DELETE FROM `address` WHERE `address`.`id` = ?");

            try {
                preparedStatement.setInt(1, addressId);
                int rowAffected = preparedStatement.executeUpdate();

                System.out.println(rowAffected + " Datensatz/Datensätze gelöscht");
            } catch (SQLException ex) {
                System.out.println("Fehler beim löschen eines Datensatzes: " + ex.getMessage());
            }

        } catch(SQLException e)  {
            System.out.println("Fehler bei Aufbau der Verbindung zur DB: " + e.getMessage());
        }
    }
}
