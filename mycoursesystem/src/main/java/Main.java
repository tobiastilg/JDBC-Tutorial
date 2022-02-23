import dataaccess.course.MySqlCourseRepository;
import dataaccess.student.MySqlStudentRepository;
import ui.Cli;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            Cli myCli = new Cli(new MySqlCourseRepository(), new MySqlStudentRepository()); //Dependency Injection des DAOs
            myCli.start();
        } catch (SQLException e) {
            System.out.println("Datenbankfehler: " + e.getMessage() + "\nSQL State: " + e.getSQLState());
        } catch (ClassNotFoundException e) {
            System.out.println("Datenbankfehler: " + e.getMessage());
        }
    }
}
