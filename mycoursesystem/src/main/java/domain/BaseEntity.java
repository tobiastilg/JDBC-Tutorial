package domain;

/**
 * Basisklasse aller Entitäten - alle haben eine ID
 */
public abstract class BaseEntity {

    private Long id;

    public BaseEntity(Long id) {
        setId(id);
    }

    public Long getId() {
        return this.id;
    }

    /**
     * Überprüfen, ob die ID null ist - wird dann über DAO als Insert-Statement erkannt
     * Ist eine ID vorhanden, dann handelt es sich um ein Update-Statement
     * @param id muss größer gleich 0 sein, sonst Exception
     */
    public void setId(Long id) {
        if (id == null || id >= 0) {
            this.id = id;
        } else {
            throw new InvalidValueException("Kurs-ID muss größer gleich 0 sein!");
        }
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "id=" + id +
                '}';
    }
}
