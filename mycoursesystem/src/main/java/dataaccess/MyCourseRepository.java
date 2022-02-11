package dataaccess;

import domain.Course;
import domain.CourseType;

import java.util.List;
import java.sql.Date;

/**
 * Zugriffsinterface - unabhängig von der Technologie
 */
public interface MyCourseRepository extends BaseRepository<Course, Long> {
    List<Course> findAllCoursesByName(String name);
    List<Course> findAllCoursesByDescription(String description);
    List<Course> findAllCoursesByNameOrDescription(String searchText);
    List<Course> findAllCoursesByCourseType(CourseType courseType);
    List<Course> findAllCoursesByStartDate(Date startDate);
    List<Course> findAllRunningCourses();
}
