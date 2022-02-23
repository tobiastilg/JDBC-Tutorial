package dataaccess.student;

import dataaccess.BaseRepository;
import domain.Student;

import java.util.List;
import java.sql.Date;

public interface StudentRepository extends BaseRepository<Student, Long> {
    List<Student> findAllStudentsByName(String name);
    List<Student> findAllStudentsByBirthyear(int year);
    List<Student> findAllStudentsByBirthdateBetween(Date date1, Date date2);
}
