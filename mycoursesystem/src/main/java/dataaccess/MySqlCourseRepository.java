package dataaccess;

import domain.Course;
import domain.CourseType;
import util.Assert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySqlCourseRepository implements MyCourseRepository {

    private Connection con;

    public MySqlCourseRepository() throws SQLException, ClassNotFoundException {
        this.con = MySqlDatabaseConnection.getConnection("jdbc:mysql://10.77.0.110:3306/kurssystem", "root", "123");
    }

    /**
     * Fügt einen Kurs dem System hinzu
     * @param entity
     * @return den eingefügten Kurs
     */
    @Override
    public Optional<Course> insert(Course entity) {
        Assert.notNull(entity);

        try {
            String sql = "INSERT INTO `courses` (`name`, `description`, `hours`, `begindate`, `enddate`, `coursetype`) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); //speichert auch den Schüssel ab
            preparedStatement.setString(1, entity.getName()); //ORM (von außen nach innen - in die Datenbank)
            preparedStatement.setString(2, entity.getDescription());
            preparedStatement.setInt(3, entity.getHours());
            preparedStatement.setDate(4, entity.getBeginDate());
            preparedStatement.setDate(5, entity.getEndDate());
            preparedStatement.setString(6, entity.getCourseType().toString());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) { //wenn keine Spalten betroffen sind (wenn Insert nicht funktioniert hat)
                return Optional.empty();
            }

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys(); //liefert den Schlüssel zurück
            if (generatedKeys.next()) { //wenn ein Key innerhalb prepareStatement existiert (dazu Statement.RETURN_GENERATED_KEYS)
                return this.getById(generatedKeys.getLong(1)); //holt sich das eben erstellte Objekt mit ID und gibt es zurück
            } else {
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    /**
     * @param id des Kurses der gesucht ist
     * @return einen Kurs (falls vorhanden)
     */
    @Override
    public Optional<Course> getById(Long id) {
        Assert.notNull(id);
        if (countCoursesInDbWithId(id) == 0) { //überprüft, ob ein Eintrag mit der ID existiert
            return Optional.empty();
        } else {
            try {
                String sql = "SELECT * FROM `courses` WHERE `id` = ?";
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setLong(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next(); //springt zum ersten Eintrag
                Course course = new Course( //ORM
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getInt("hours"),
                        resultSet.getDate("begindate"),
                        resultSet.getDate("enddate"),
                        CourseType.valueOf(resultSet.getString("coursetype"))
                );
                return Optional.of(course);
            } catch (SQLException e) {
                throw new DatabaseException(e.getMessage());
            }
        }
    }

    /**
     * Hilfsmethode
     * Liefert 1 zurück, wenn ein Eintrag mit dieser ID existiert - sonst 0
     * @param id
     * @return
     */
    private int countCoursesInDbWithId(Long id) {
        PreparedStatement preparedStatementCount = null;
        try {
            String countSql = "SELECT COUNT(*) FROM `courses` WHERE `id` = ?"; //"zählt", ob tatsächlich ein Eintrag mit dieser ID existiert
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

    /**
     * @return Liste mit allen Kursen
     */
    @Override
    public List<Course> getAll() {
        String sql = "SELECT * FROM `courses`";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<Course> courseList = new ArrayList<>();
            while (resultSet.next()) {
                courseList.add(new Course( //ORM findet hier statt
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getInt("hours"),
                        resultSet.getDate("begindate"),
                        resultSet.getDate("enddate"),
                        CourseType.valueOf(resultSet.getString("coursetype"))
                        )
                );
            }
            return courseList;
        } catch (SQLException e) {
            //da unchecked Exception muss sie nicht im Methodenkopf geworfen werden (kann aber)
            throw new DatabaseException("Database error occurred!");
        }
    }

    @Override
    public Optional<Course> update(Course entity) {
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public List<Course> findAllCoursesByName(String name) {
        return null;
    }

    @Override
    public List<Course> findAllCoursesByDescription(String description) {
        return null;
    }

    @Override
    public List<Course> findAllCoursesByNameOrDescription(String searchText) {
        return null;
    }

    @Override
    public List<Course> findAllCoursesByCourseType(CourseType courseType) {
        return null;
    }

    @Override
    public List<Course> findAllCoursesByStartDate(Date startDate) {
        return null;
    }

    @Override
    public List<Course> findAllRunningCourses() {
        return null;
    }
}
