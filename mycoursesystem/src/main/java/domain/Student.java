package domain;

import java.sql.Date;

/**
 * Domänenklasse
 */
public class Student extends BaseEntity {

    private String firstname;
    private String lastname;
    private Date birthdate;

    /**
     * Für ID != null (Update)
     */
    public Student(Long id, String firstname, String lastname, Date birthdate) {
        super(id);
        setFirstname(firstname);
        setLastname(lastname);
        setBirthdate(birthdate);
    }

    /**
     * Für ID = null (Insert)
     */
    public Student(String firstname, String lastname, Date birthdate) {
        super(null);
        setFirstname(firstname);
        setLastname(lastname);
        setBirthdate(birthdate);
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        if (firstname != null && firstname.length() > 1) {
            this.firstname = firstname;
        } else {
            throw new InvalidValueException("Vorname muss mindestens 2 Zeichen lang sein!");
        }

    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        if (lastname != null && lastname.length() > 1) {
            this.lastname = lastname;
        } else {
            throw new InvalidValueException("Nachname muss mindestens 2 Zeichen lang sein!");
        }
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        if (birthdate != null) {
            this.birthdate = birthdate;
        } else {
            throw new InvalidValueException("Geburtsdatum darf nicht leer sein!");
        }
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + this.getId() + '\'' + //aus Mutterklasse holen
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", birthdate=" + birthdate +
                '}';
    }
}

