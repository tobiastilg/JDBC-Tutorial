package dataaccess.student;

import dataaccess.DatabaseException;
import dataaccess.MySqlDatabaseConnection;
import domain.Course;
import domain.CourseType;
import domain.Student;
import util.Assert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySqlStudentRepository implements StudentRepository{

    private Connection con;

    public MySqlStudentRepository() throws SQLException, ClassNotFoundException {
        this.con = MySqlDatabaseConnection.getConnection("jdbc:mysql://10.77.0.110:3306/kurssystem", "root", "123");
    }

    @Override
    public Optional<Student> insert(Student entity) throws IllegalArgumentException{
        Assert.notNull(entity);

        try {
            String sql = "INSERT INTO `students` (`firstname`, `lastname`, `birthdate`) VALUES (?, ? ,?)";
            PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, entity.getFirstname()); //ORM (von außen nach innen - in die Datenbank)
            preparedStatement.setString(2, entity.getLastname());
            preparedStatement.setDate(3, entity.getBirthdate());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                return Optional.empty();
            }

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys(); //liefert den Schlüssel zurück
            if (generatedKeys.next()) { //wenn ein Key innerhalb prepareStatement existiert (dazu Statement.RETURN_GENERATED_KEYS)
                return this.getById(generatedKeys.getLong(1)); //holt sich das eben erstellte Objekt mit ID und gibt es zurück
            } else {
                return Optional.empty();
            }

        } catch (SQLException sqlException) {
            throw new DatabaseException(sqlException.getMessage());
        }
    }

    @Override
    public Optional<Student> getById(Long id) throws IllegalArgumentException{
        Assert.notNull(id);

        if (countStudentsInDbWithId(id) == 0) {
            return Optional.empty();
        } else {
            try {
                String sql = "SELECT * FROM `students` WHERE `id` = ?";
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setLong(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                Student student = new Student(
                        resultSet.getLong("id"),
                        resultSet.getString("firstname"),
                        resultSet.getString("lastname"),
                        resultSet.getDate("birthdate")
                );
                return Optional.of(student);
            } catch (SQLException sqlException) {
                throw new DatabaseException(sqlException.getMessage());
            }
        }
    }

    /**
     * Hilfsmethode
     * Liefert 1 zurück, wenn ein Eintrag mit dieser ID existiert - sonst 0
     * @param id
     * @return
     */
    private int countStudentsInDbWithId(Long id) {
        PreparedStatement preparedStatementCount = null;
        try {
            String countSql = "SELECT COUNT(*) FROM `students` WHERE `id` = ?"; //"zählt", ob tatsächlich ein Eintrag mit dieser ID existiert
            preparedStatementCount = con.prepareStatement(countSql);
            preparedStatementCount.setLong(1, id);
            ResultSet resultSetCount = preparedStatementCount.executeQuery();
            resultSetCount.next(); //springt zum ersten Eintrag (es existiert nur ein Eintrag)
            int courseCount = resultSetCount.getInt(1);
            return courseCount; //entweder 0 oder 1
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public List<Student> getAll() {
        String sql = "SELECT * FROM `students`";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            return fillStudentsList(resultSet);
        } catch (SQLException e) {
            throw new DatabaseException("Database error occurred!");
        }
    }

    /**
     * Hilfsmethode um Studentenlisten über ein ResultSet zu füllen
     * @param resultSet
     * @return gefüllte Studentenlisten (sofern Einträge gefunden)
     */
    private List<Student> fillStudentsList(ResultSet resultSet) {
        try {
            ArrayList<Student> studentList = new ArrayList<>();
            while (resultSet.next()) {
                studentList.add(new Student(
                                resultSet.getLong("id"),
                                resultSet.getString("firstname"),
                                resultSet.getString("lastname"),
                                resultSet.getDate("birthdate")
                        )
                );
            }
            return studentList;
        } catch (SQLException sqlException) {
            throw new DatabaseException(sqlException.getMessage());
        }
    }

    @Override
    public Optional<Student> update(Student entity) throws IllegalArgumentException{
        Assert.notNull(entity);

        String sql = "UPDATE `students` SET `firstname` = ?, `lastname` = ?, `birthdate` = ? WHERE `students`.`id` = ?";

        if (countStudentsInDbWithId(entity.getId()) == 0) {
            return Optional.empty();
        } else {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, entity.getFirstname());
                preparedStatement.setString(2, entity.getLastname());
                preparedStatement.setDate(3, entity.getBirthdate());
                preparedStatement.setLong(4, entity.getId());

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows == 0) { //wenn keine Spalten betroffen sind (wenn Update nicht funktioniert hat (Datenbankfehler))
                    return Optional.empty();
                } else {
                    return this.getById(entity.getId()); //geupdatetes Objekt holen
                }

            } catch (SQLException e) {
                throw new DatabaseException(e.getMessage());
            }
        }
    }

    @Override
    public boolean deleteById(Long id) throws IllegalArgumentException{
        Assert.notNull(id);

        String sql = "DELETE FROM `students` WHERE `id` = ?";
        try {
            if (countStudentsInDbWithId(id) == 1) {
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setLong(1, id);
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows != 0) { //wenn keine Spalten betroffen sind (wenn Update nicht funktioniert hat)
                    return true;
                }
            }
        } catch (SQLException sqlException) {
            throw new DatabaseException(sqlException.getMessage());
        }
        return false;
    }

    @Override
    public List<Student> findAllStudentsByName(String name) {
        String sql = "SELECT * FROM `students` WHERE LOWER(`firstname`) LIKE LOWER(?) OR LOWER(`lastname`) LIKE LOWER(?)";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, "%"+name+"%");
            preparedStatement.setString(2, "%"+name+"%");
            ResultSet resultSet = preparedStatement.executeQuery();
            return fillStudentsList(resultSet);
        } catch (SQLException sqlException) {
            throw new DatabaseException(sqlException.getMessage());
        }
    }

    @Override
    public List<Student> findAllStudentsByBirthyear(int year) {
        String sql = "SELECT * FROM `students` WHERE YEAR(birthdate) = ?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, year);
            ResultSet resultSet = preparedStatement.executeQuery();
            return fillStudentsList(resultSet);
        } catch (SQLException sqlException) {
            throw new DatabaseException(sqlException.getMessage());
        }
    }

    @Override
    public List<Student> findAllStudentsByBirthdateBetween(Date date1, Date date2) {
        String sql = "SELECT * FROM students WHERE birthdate BETWEEN ? AND ?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setDate(1, date1);
            preparedStatement.setDate(2, date2);
            ResultSet resultSet = preparedStatement.executeQuery();
            return fillStudentsList(resultSet);
        } catch (SQLException sqlException) {
            throw new DatabaseException(sqlException.getMessage());
        }
    }
}
