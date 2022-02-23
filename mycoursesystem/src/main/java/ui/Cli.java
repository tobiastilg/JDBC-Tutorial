package ui;

import dataaccess.DatabaseException;
import dataaccess.course.CourseRepository;
import dataaccess.student.StudentRepository;
import domain.Course;
import domain.CourseType;
import domain.InvalidValueException;
import domain.Student;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Commandline Interface
 */
public class Cli {

    private Scanner scan;
    private CourseRepository courseRepo;
    private StudentRepository studentRepo;

    public Cli(CourseRepository courseRepo, StudentRepository studentRepo) {
        this.scan = new Scanner(System.in);
        this.courseRepo = courseRepo;
        this.studentRepo = studentRepo;
    }

    public void start() {
        String input = "-";
        while (!input.equals("x")) {
            showMenue();
            input = scan.nextLine();

            switch (input) {
                case "1":
                    addCourse();
                    break;
                case "2":
                    showAllCourses();
                    break;
                case "3":
                    showCourseDetails();
                    break;
                case "4":
                    updateCourseDetails();
                    break;
                case "5":
                    deleteCourse();
                    break;
                case "6":
                    courseSearch();
                    break;
                case "7":
                    runningCourses();
                    break;
                case "10":
                    addStudent();
                    break;
                case "11":
                    showAllStudents();
                    break;
                case "12":
                    showStudentDetails();
                    break;
                case "13":
                    updateStudentDetails();
                    break;
                case "14":
                    deleteStudent();
                    break;
                case "15":
                    StudentSearchByName();
                    break;
                case "16":
                    StudentSearchByBirthyear();
                    break;
                case "17":
                    StudentSearchByBirthdateBetween();
                    break;
                case "x":
                    System.out.println("Auf Wiedersehen!");
                    break;
                default:
                    inputError();
            }
        }
        scan.close();
    }

    private void StudentSearchByBirthdateBetween() {
        System.out.println("Geben Sie die Daten ein! (YYYY-MM-DD)");
        Date date1, date2;
        try {
            System.out.println("Datum von");
            date1 = Date.valueOf(scan.nextLine());

            System.out.println("Datum bis");
            date2 = Date.valueOf(scan.nextLine());

            List<Student> studentList;
            studentList = studentRepo.findAllStudentsByBirthdateBetween(date1, date2);
            if (studentList.size() > 0) {
                for (Student student : studentList) {
                    System.out.println(student);
                }
            } else {
                System.out.println("Keine Studenten gefunden!");
            }
        } catch (IllegalArgumentException illegalArgumentException) { //parsen
            System.out.println("Eingabefehler: Bitte geben Sie das gültige Datumsformat an!");
        } catch (DatabaseException databaseException) {
            System.out.println("Datenbankfehler bei der Studentensuche: " + databaseException.getMessage());
        } catch (Exception exception) {
            System.out.println("Unbekannter Fehler bei der Studentensuche: " + exception.getMessage());
        }
    }

    private void StudentSearchByBirthyear() {
        String searchString = searchLoopString("Jahr");
        List<Student> studentList;
        try {
            Integer studentBirthyear = Integer.parseInt(searchString);
            studentList = studentRepo.findAllStudentsByBirthyear(studentBirthyear);
            if (studentList.size() > 0) {
                for (Student student : studentList) {
                    System.out.println(student);
                }
            } else {
                System.out.println("Keine Studenten gefunden!");
            }
        } catch (IllegalArgumentException illegalArgumentException) { //parsen
            System.out.println("Eingabefehler: " + illegalArgumentException.getMessage());
        } catch (DatabaseException databaseException) {
            System.out.println("Datenbankfehler bei der Studentensuche: " + databaseException.getMessage());
        } catch (Exception exception) {
            System.out.println("Unbekannter Fehler bei der Studentensuche: " + exception.getMessage());
        }
    }

    private void StudentSearchByName() {
        String searchString = searchLoopString("Namen");
        List<Student> studentList;
        try {
            studentList = studentRepo.findAllStudentsByName(searchString);
            if (studentList.size() > 0) {
                for (Student student : studentList) {
                    System.out.println(student);
                }
            } else {
                System.out.println("Keine Studenten gefunden!");
            }
        } catch (DatabaseException databaseException) {
            System.out.println("Datenbankfehler bei der Studentensuche: " + databaseException.getMessage());
        } catch (Exception exception) {
            System.out.println("Unbekannter Fehler bei der Studentensuche: " + exception.getMessage());
        }
    }

    private void deleteStudent() {
        try {
            System.out.println("Welchen Studenten möchten Sie löschen? Bitte ID eingeben: ");
            Long studentIdToDelete = Long.parseLong(scan.nextLine());
            boolean wurdeGeloescht = studentRepo.deleteById(studentIdToDelete);
            if (wurdeGeloescht) {
                System.out.println("Student mit ID " + studentIdToDelete + " gelöscht!");
            } else {
                System.out.println("Student mit der gegebenen ID nicht in der Datenbank!");
            }
        } catch (IllegalArgumentException illegalArgumentException) { //wegen parseLong
            System.out.println("Eingabefehler: " + illegalArgumentException.getMessage());
        } catch (DatabaseException databaseException) {
            System.out.println("Datenbankfehler beim Löschen: " + databaseException.getMessage());
        } catch (Exception exception) {
            System.out.println("Unbekannter Fehler beim Löschen eines Studenten: " + exception.getMessage());
        }
    }

    private void updateStudentDetails() {
        try {
            System.out.println("Für welche Student-ID möchten Sie die Studentendetails ändern?");
            Long studentId = Long.parseLong(scan.nextLine());
            Optional<Student> studentOptional = studentRepo.getById(studentId);
            if (studentOptional.isEmpty()) {
                System.out.println("Student mit der gegebenen ID nicht in der Datenbank!");
            } else {
                Student student = studentOptional.get();
                System.out.println("Änderungen für folgenden Studenten: ");
                System.out.println(student);

                String firstname, lastname, birthdate;

                System.out.println("\nBitte neue Studentendaten angeben (Enter falls keine Änderung gewünscht ist): ");

                System.out.println("Vorname: ");
                firstname = scan.nextLine();

                System.out.println("Nachname: ");
                lastname = scan.nextLine();

                System.out.println("Geburtsdatum (YYYY-MM-DD): ");
                birthdate = scan.nextLine();

                //UI Validierungen jeweils über ternären Operator, somit ist equals-Prüfung überall möglich (wird dann geparst)
                Optional<Student> studentOptionalUpdated = studentRepo.update(
                        new Student(student.getId(),
                                firstname.equals("") ? student.getFirstname() : firstname,
                                lastname.equals("") ? student.getLastname() : lastname,
                                birthdate.equals("") ? student.getBirthdate() : Date.valueOf(birthdate)
                        )
                );

                studentOptionalUpdated.ifPresentOrElse( //funktionale Alternative zu if-else
                        (c)-> System.out.println("Student aktualisiert: " + c),
                        ()-> System.out.println("Student konnte nicht aktualisiert werden!")
                );
            }

        } catch (IllegalArgumentException illegalArgumentException) {
            System.out.println("Eingabefehler: " + illegalArgumentException.getMessage());
        } catch (InvalidValueException invalidValueException) {
            System.out.println("Studentendaten nicht korrekt angegeben: " + invalidValueException.getMessage());
        } catch (DatabaseException databaseException) {
            System.out.println("Datenbankfehler beim Aktualisieren: " + databaseException.getMessage());
        } catch (Exception exception) {
            System.out.println("Unbekannter Fehler beim Update eines Studenten: " + exception.getMessage());
        }
    }

    private void showStudentDetails() {
        System.out.println("Für welchen Studenten möchten Sie die Studentendetails anzeigen?");
        try {
            Long studentId = Long.parseLong(scan.nextLine());
            Optional<Student> studentOptional = studentRepo.getById(studentId);
            if (studentOptional.isPresent()) {
                System.out.println(studentOptional.get());
            } else {
                System.out.println("Student mit der ID " + studentId + " nicht gefunden!");
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            System.out.println("Eingabefehler: " + illegalArgumentException.getMessage());
        } catch (DatabaseException databaseException) {
            System.out.println("Datenbankfehler bei Student-Detailanzeige: " + databaseException.getMessage());
        } catch (Exception exception) { //falls eine andere Exception auftritt (NumberFormatException durch parsen zB)
            System.out.println("Unbekannter Fehler bei Anzeige eines Kurse: " + exception.getMessage());
        }
    }

    private void showAllStudents() {
        List<Student> list = null;
        try {
            list = studentRepo.getAll();
            if (list.size() > 0) {
                for (Student student: list) {
                    System.out.println(student);
                }
            } else {
                System.out.println("Studentenliste leer!");
            }
        } catch (DatabaseException databaseException) {
            System.out.println("Datenbankfehler bei Anzeige aller Studenten: " + databaseException.getMessage());
        } catch (Exception exception) {
            System.out.println("Unbekannter Fehler bei Anzeige aller Studenten: " + exception.getMessage());
        }
    }

    private void addStudent() {
        String firstname, lastname;
        Date birthdate;

        try {
            System.out.println("Bitte alle Studentendaten abgeben:");
            System.out.println("Vorname: ");
            firstname = scan.nextLine();
            if (firstname.equals("")) throw new IllegalArgumentException("Eingabe darf nicht leer sein!");

            System.out.println("Nachname: ");
            lastname = scan.nextLine();
            if (lastname.equals("")) throw new IllegalArgumentException("Eingabe darf nicht leer sein!");

            System.out.println("Geburtsdatum (YYYY-MM-DD): ");
            birthdate = Date.valueOf(scan.nextLine());

            Optional<Student> optionalStudent = studentRepo.insert( //ORM
                    new Student(firstname, lastname, birthdate)
            );

            if (optionalStudent.isPresent()) {
                System.out.println("Student angelegt: " + optionalStudent.get());
            } else {
                System.out.println("Student konnte nicht angelegt werden!");
            }

        } catch (IllegalArgumentException illegalArgumentException) {
            System.out.println("Eingabefehler: " + illegalArgumentException.getMessage());
        } catch (InvalidValueException invalidValueException) {
            System.out.println("Studentendaten nicht korrekt angegeben: " + invalidValueException.getMessage());
        } catch (DatabaseException databaseException) {
            System.out.println("Datenbankfehler beim Einfügen: " + databaseException.getMessage());
        } catch (Exception exception) {
            System.out.println("Unbekannter Fehler beim Einfügen eines Studenten: " + exception.getMessage());
        }
    }

    private void runningCourses() {
        System.out.println("Aktuell laufende Kurse");
        List<Course> courseList;
        try {
            courseList = courseRepo.findAllRunningCourses();
            if (courseList.size() > 0) {
                for (Course course : courseList) {
                    System.out.println(course);
                }
            } else {
                System.out.println("Keine Kurse gefunden!");
            }
        } catch (DatabaseException databaseException) {
            System.out.println("Datenbankfehler bei Kurs-Anzeige für laufende Kurse: " + databaseException.getMessage());
        } catch (Exception exception) {
            System.out.println("Unbekannter Fehler bei Kurs-Anzeige für laufende Kurse: " + exception.getMessage());
        }
    }

    private void courseSearch() {
        String searchString = searchLoopString("Suchbegriff");
        List<Course> courseList;
        try {
            courseList = courseRepo.findAllCoursesByNameOrDescription(searchString);
            if (courseList.size() > 0) {
                for (Course course : courseList) {
                    System.out.println(course);
                }
            } else {
                System.out.println("Keine Kurse gefunden!");
            }
        } catch (DatabaseException databaseException) {
            System.out.println("Datenbankfehler bei der Kurssuche: " + databaseException.getMessage());
        } catch (Exception exception) {
            System.out.println("Unbekannter Fehler bei der Kurssuche: " + exception.getMessage());
        }
    }

    private void deleteCourse() {
        try {
            System.out.println("Welchen Kurs möchten Sie löschen? Bitte ID eingeben: ");
            Long courseIdToDelete = Long.parseLong(scan.nextLine());
            boolean wurdeGeloescht = courseRepo.deleteById(courseIdToDelete);
            if (wurdeGeloescht) {
                System.out.println("Kurs mit ID " + courseIdToDelete + " gelöscht!");
            } else {
                System.out.println("Kurs mit der gegebenen ID nicht in der Datenbank!");
            }
        } catch (IllegalArgumentException illegalArgumentException) { //wegen parseLong
            System.out.println("Eingabefehler: " + illegalArgumentException.getMessage());
        } catch (DatabaseException databaseException) {
            System.out.println("Datenbankfehler beim Löschen: " + databaseException.getMessage());
        } catch (Exception exception) {
            System.out.println("Unbekannter Fehler beim Löschen eines Kurses: " + exception.getMessage());
        }
    }

    private void updateCourseDetails() {
        try {
            System.out.println("Für welche Kurs-ID möchten Sie die Kursdetails ändern?");
            Long courseId = Long.parseLong(scan.nextLine());
            Optional<Course> courseOptional = courseRepo.getById(courseId);
            if (courseOptional.isEmpty()) {
                System.out.println("Kurs mit der gegebenen ID nicht in der Datenbank!");
            } else {
                Course course = courseOptional.get();
                System.out.println("Änderungen für folgenden Kurs: ");
                System.out.println(course);

                String name, description, hours, dateFrom, dateTo, courseType;

                System.out.println("\nBitte neue Kursdaten angeben (Enter falls keine Änderung gewünscht ist): ");

                System.out.println("Name: ");
                name = scan.nextLine();

                System.out.println("Beschreibung: ");
                description = scan.nextLine();

                System.out.println("Stundenanzahl: ");
                hours = scan.nextLine();

                System.out.println("Startdatum (YYYY-MM-DD): ");
                dateFrom = scan.nextLine();

                System.out.println("Enddatum (YYYY-MM-DD): ");
                dateTo = scan.nextLine();

                System.out.println("Kurstyp (ZA/BF/FF/OE): ");
                courseType = scan.nextLine();

                //UI Validierungen jeweils über ternären Operator, somit ist equals-Prüfung überall möglich (wird dann geparst)
                Optional<Course> courseOptionalUpdated = courseRepo.update(
                        new Course(course.getId(),
                                name.equals("") ? course.getName() : name,
                                description.equals("") ? course.getDescription() : description,
                                hours.equals("") ? course.getHours() : Integer.parseInt(hours),
                                dateFrom.equals("") ? course.getBeginDate() : Date.valueOf(dateFrom),
                                dateTo.equals("") ? course.getEndDate() : Date.valueOf(dateTo),
                                courseType.equals("") ? course.getCourseType() : CourseType.valueOf(courseType)
                        )
                );

                courseOptionalUpdated.ifPresentOrElse( //funktionale Alternative zu if-else
                        (c)-> System.out.println("Kurs aktualisiert: " + c),
                        ()-> System.out.println("Kurs konnte nicht aktualisiert werden!")
                );
            }

        } catch (IllegalArgumentException illegalArgumentException) {
            System.out.println("Eingabefehler: " + illegalArgumentException.getMessage());
        } catch (InvalidValueException invalidValueException) {
            System.out.println("Kursdaten nicht korrekt angegeben: " + invalidValueException.getMessage());
        } catch (DatabaseException databaseException) {
            System.out.println("Datenbankfehler beim Aktualisieren: " + databaseException.getMessage());
        } catch (Exception exception) {
            System.out.println("Unbekannter Fehler beim Update eines Kurses: " + exception.getMessage());
        }
    }

    private void addCourse() {
        String name, description;
        int hours;
        Date dateFrom, dateTo;
        CourseType courseType;

        try {
            System.out.println("Bitte alle Kursdaten abgeben:");
            System.out.println("Name: ");
            name = scan.nextLine();
            //UI Validierung ("clientseitige Validierung" - könnte auch alles Domänenklasse übernehmen)
            if (name.equals("")) throw new IllegalArgumentException("Eingabe darf nicht leer sein!");

            System.out.println("Beschreibung: ");
            description = scan.nextLine();
            if (description.equals("")) throw new IllegalArgumentException("Eingabe darf nicht leer sein!");

            System.out.println("Stundenanzahl: ");
            hours = Integer.parseInt(scan.nextLine());

            System.out.println("Startdatum (YYYY-MM-DD): ");
            dateFrom = Date.valueOf(scan.nextLine());

            System.out.println("Enddatum (YYYY-MM-DD): ");
            dateTo = Date.valueOf(scan.nextLine());

            System.out.println("Kurstyp (ZA/BF/FF/OE): ");
            courseType = CourseType.valueOf(scan.nextLine());

            Optional<Course> optionalCourse = courseRepo.insert( //ORM
                    new Course(name, description, hours, dateFrom, dateTo, courseType)
            );

            if (optionalCourse.isPresent()) {
                System.out.println("Kurs angelegt: " + optionalCourse.get());
            } else {
                System.out.println("Kurs konnte nicht angelegt werden!");
            }

        } catch (IllegalArgumentException illegalArgumentException) {
            System.out.println("Eingabefehler: " + illegalArgumentException.getMessage());
        } catch (InvalidValueException invalidValueException) {
            System.out.println("Kursdaten nicht korrekt angegeben: " + invalidValueException.getMessage());
        } catch (DatabaseException databaseException) {
            System.out.println("Datenbankfehler beim Einfügen: " + databaseException.getMessage());
        } catch (Exception exception) {
            System.out.println("Unbekannter Fehler beim Einfügen eines Kurses: " + exception.getMessage());
        }
    }

    private void showCourseDetails() {
        System.out.println("Für welchen Kurs möchten Sie die Kursdetails anzeigen?");
        try {
            Long courseId = Long.parseLong(scan.nextLine());
            Optional<Course> courseOptional = courseRepo.getById(courseId);
            if (courseOptional.isPresent()) {
                System.out.println(courseOptional.get());
            } else {
                System.out.println("Kurs mit der ID " + courseId + " nicht gefunden!");
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            System.out.println("Eingabefehler: " + illegalArgumentException.getMessage());
        } catch (DatabaseException databaseException) {
            System.out.println("Datenbankfehler bei Kurs-Detailanzeige: " + databaseException.getMessage());
        } catch (Exception exception) { //falls eine andere Exception auftritt (NumberFormatException durch parsen zB)
            System.out.println("Unbekannter Fehler bei Anzeige eines Kurse: " + exception.getMessage());
        }
    }

    private void showAllCourses() {
        //es wird nur mehr mit Kursobjekten gearbeitet (ORM) - keine Abhängigkeit - man weiß hier nichts von einer Datenbank
        List<Course> list = null;
        try {
            list = courseRepo.getAll();
            if (list.size() > 0) {
                for (Course course: list) {
                    System.out.println(course);
                }
            } else {
                System.out.println("Kursliste leer!");
            }
        } catch (DatabaseException databaseException) {
            System.out.println("Datenbankfehler bei Anzeige aller Kurse: " + databaseException.getMessage());
        } catch (Exception exception) { //falls eine andere Exception auftritt
            System.out.println("Unbekannter Fehler bei Anzeige aller Kurse: " + exception.getMessage());
        }
    }

    private String searchLoopString(String title) {
        System.out.println("Geben Sie ein " + title + " an!");
        String searchString = null;
        boolean emptySearch = true;
        while (emptySearch) {
            searchString = scan.nextLine();
            if (searchString.equals("")) {
                System.out.println("Bitte geben Sie ein Suchbegriff an!");
            } else {
                emptySearch = false;
            }
        }
        return searchString;
    }

    private void showMenue() {
        System.out.println("\n------------------- KURSMANAGEMENT -------------------");
        System.out.println("(1) Kurs eingeben \t (2) Alle Kurse anzeigen \t (3) Kursdetails anzeigen");
        System.out.println("(4) Kursdetails ändern \t (5) Kurs löschen \t (6) Kurssuche");
        System.out.println("(7) Laufende Kurse anzeigen");
        System.out.println("\n------------------- STUDENTENMANAGEMENT -------------------");
        System.out.println("(10) Student eingeben \t (11) Alle Studenten anzeigen \t (12) Studentendetails anzeigen");
        System.out.println("(13) Studentendetails ändern \t (14) Student löschen \t (15) Studentensuche nach Name");
        System.out.println("(16) Studentensuche nach Geburtsjahr \t (17) Studentensuche nach Geburtsdatum");
        System.out.println("\n(x) ENDE");
    }

    private void inputError() {
        System.out.println("Bitte nur die Zahlen der Menüauswahl eingeben!");
    }
}
