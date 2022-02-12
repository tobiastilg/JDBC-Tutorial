package util;

/**
 * Hilfsklasse - übernimmt Objekt-Nullprüfungen
 */
public class Assert {

    public static void notNull(Object o) {
        if (o == null) throw new IllegalArgumentException("Reference must not be null!");
    }
}
