# JDBC - Datenbankzugriff mit Java

## Entwicklungumgebung

Bestehender Ubuntu-Server mit Docker inkl. docker-compose

```yml
version: '2'

services:

# Databases
  postgres:
    image: bitnami/postgresql:9.6.10
    container_name: postgres
    restart: unless-stopped
    environment:
      POSTGRESQL_PASSWORD: "123"
    ports:
      - 5432:5432
    volumes:
      - dbdocker_postgres:/bitnami/postgresql
  mysql:
    image: mysql
    command: mysqld --default-authentication-plugin=mysql_native_password
    container_name: mysql
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: "123"
      MYSQL_ROOT_HOST: "%"
    ports:
      - 3306:3306
    volumes:
      - dbdocker_mysql:/var/lib/mysql

  mongodb:
    image: 'bitnami/mongodb:3.6.8'
    container_name: mongodb
    restart: unless-stopped
    environment:
      MONGO_INITDB_ROOT_USERNAME: "root"
      MONGO_INITDB_ROOT_PASSWORD: "123"
      ALLOW_EMPTY_PASSWORD: "no"
    ports:
      - 27017:27017

# Apache
  apache:
    image: thecodingmachine/php:7.2-v2-apache-node10
    container_name: apache
    restart: unless-stopped
    ports:
      - 80:80
    environment:
            PHP_EXTENSION_PGSQL: 1
            PHP_EXTENSION_XDEBUG: 1
            PHP_EXTENSION_MONGODB: 1
            PHP_EXTENSION_MONGO: 1
            APACHE_EXTENSION_AUTOINDEX: 1
    volumes:
      - ./www:/var/www/html

# Frontends
  adminer:
      image: adminer:4.8.0
      container_name: adminer
      restart: unless-stopped
      ports:
        - 8080:8080
      environment:
        ADMINER_DESIGN: "hever"
        ADMINER_PLUGINS: "edit-calendar edit-foreign edit-textarea"
        ADMINER_DEFAULT_SERVER: "mysql"
        ADMINER_SERVER: "mysql"
        ADMINER_USERNAME: "root"
        ADMINER_PASSWORD: "123"

  mongo-express:
      image: mongo-express:0.49
      container_name: mexpress
      restart: unless-stopped
      ports:
        - 8081:8081
      environment:
        ME_CONFIG_MONGODB_SERVER: "mongodb"
        ME_CONFIG_MONGODB_ADMINUSERNAME: "root"
        ME_CONFIG_MONGODB_ADMINPASSWORD: "123"
        ME_CONFIG_MONGODB_PORT: "27017"
      depends_on:
        - mongodb


  phpmyadmin:
    image: bitnami/phpmyadmin:5.1.0
    container_name: phpmyadmin
    environment:
      DATABASE_HOST: "mysql"
      PHPMYADMIN_ALLOW_ARBITRARY_SERVER: "true"
      DATABASE_PORT_NUMBER: "3306"
    restart: unless-stopped
    ports:
      - 8082:8080

  mysql-cron-backup:
    image: fradelg/mysql-cron-backup
    container_name: mysql-backup
    depends_on:
      - mysql
    volumes:
      - ./mysqlBackup:/backup
    environment:
      - MYSQL_HOST=mysql
      - MYSQL_USER=root
      - MYSQL_PASS=123
      - MAX_BACKUPS=15
      - INIT_BACKUP=0
      # backup every 10 minutes
      - CRON_TIME=*/10 * * * *
    restart: unless-stopped

volumes:
  dbdocker_postgres:
  dbdocker_mysql:
```

## JDBC Intro

JDBC steht für Java Database Connectivity und stellt eine Datenbankschnittstelle für ein Java Programm und eine Datenbank dar.

### Maven

Maven Dependency `mysql-connector-java` wird benötigt (https://mvnrepository.com/artifact/mysql/mysql-connector-java). 

Einbinden in `pom.xml`:

```xml
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <version>8.0.27</version>
</dependency>
```

### Verbindung herstellen

Über den `DriverManager` (java.sql.DriverManager) wird die Verbindung aufgebaut. Beim Arbeiten mit Datenbanken können häufig Exceptions auftreten, um die man sich zusätzlich kümmern muss.

Etwas ungewöhlich ist, dass innerhalb der try-catch "Bedingung" die Connection erstellt wird. Dies muss nicht gemacht werden, jedoch ersprat man sich dann den sehr wichtigen Schritt, die Datenbankverbindung wieder zu schließen (conn.close() geschieht automatisch).

```java
String connectionUrl = "jdbc:mysql://10.77.0.110:3306/jdbcdemo";
String user = "root";
String pwd = "123";

try(Connection conn = DriverManager.getConnection(connectionUrl, user, pwd)) {
    System.out.println("Verbindung zur DB hergestellt!");
} catch(SQLException e)  {
    System.out.println("Fehler bei Aufbau der Verbindung zur DB: " + e.getMessage());
}
```

Wird der Datenbankclient in IntelliJ mit der Datenbank verbunden, so funktioniert auch code completion bei SQL Statements.

### Daten abfragen

Über die aufgebaute Connenction in unserer try-catch Bedingung können nur Statements ausgeführt werden. Diese müssen (wie bei PDO) `prepared` und `executed` werden. Zurückgeliefert wird dann ein ResultSet, über das man iterieren kann. Somit lassen sich dann Daten auslesen.

Der try-catch Block hilf uns hier wieder um Fehler zu erkennen. Optional könnte man auch mehrere try-catch Blöcke ineinander erstellen, um Fehler genauer zu definieren.

```java
try(Connection conn = DriverManager.getConnection(connectionUrl, user, pwd)) {
    System.out.println("Verbindung zur DB hergestellt!");

    PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM `student`");
    ResultSet rs = preparedStatement.executeQuery();

    //next() liefert solange true, bis Datensätze existieren
    while (rs.next()) {
        int id = rs.getInt("id"); //holt mir die Daten Spalte id
        String name = rs.getString("name");
        String email = rs.getString("email");
        System.out.println("Student aus der DB: ID " + id + ", NAME " + name + ", EMAIL " + email);
    }

} catch(SQLException e)  {
    System.out.println("Fehler bei Aufbau der Verbindung zur DB: " + e.getMessage());
}
```

### Daten einfügen

Beim einfügen von Daten muss darauf geachtet werden, dass im SQL Statement selbst Platzhalter verwendet werden, die später über das PreparedStatement gesetzt werden. Sonst können SQL-Injections entstehen, die massive Probleme verursachen können. Ein Unterschied zum Select-Statement ist, dass die execute Methode nicht executeQuery() sonder `executeUpdate()` lautet, da sich etwas in der Datenbank ändert.

```java
try(Connection conn = DriverManager.getConnection(connectionUrl, user, pwd)) {
  System.out.println("Verbindung zur DB hergestellt!");

  PreparedStatement preparedStatement = conn.prepareStatement(
          "INSERT INTO `student` (`name`, `email`) VALUES (?, ?)"); //? wegen SQL-Injection

  try {
      preparedStatement.setString(1, "Martin Rieger"); //die Nummer steht für das jeweilige Fragezeichen
      preparedStatement.setString(2, "martin@outlook.com");
      int rowAffected = preparedStatement.executeUpdate(); //liefert die Anzahl der betroffenen Datensätze

      System.out.println(rowAffected + " Datensatz/Datensätze eingefügt");
  } catch (SQLException ex) {
      System.out.println("Fehler beim erstellen eines Datensatzes: " + ex.getMessage());
  }

} catch(SQLException e)  {
  System.out.println("Fehler bei Aufbau der Verbindung zur DB: " + e.getMessage());
}
```

### Daten ändern

Das Aktualisieren von Daten unterscheidet sich bis auf das SQL-Statement nicht von dem Aufbau eines Insert Befehls. Es sind die gleichen Dinge zu beachten.

```java
try(Connection conn = DriverManager.getConnection(connectionUrl, user, pwd)) {
  System.out.println("Verbindung zur DB hergestellt!");

  PreparedStatement preparedStatement = conn.prepareStatement(
          "UPDATE `student` SET `name` = ?, SET `email` = ? WHERE `student`.`id` = 5"); //? wegen-SQL Injection

  try {
      preparedStatement.setString(1, "Josef Reiter");
      preparedStatement.setString(2, "josef@outlook.com");
      int rowAffected = preparedStatement.executeUpdate(); //liefert die Anzahl der betroffenen Datensätze

      System.out.println(rowAffected + " Datensatz/Datensätze aktualisiert");
  } catch (SQLException ex) {
      System.out.println("Fehler beim updaten eines Datensatzes: " + ex.getMessage());
  }

} catch(SQLException e)  {
  System.out.println("Fehler bei Aufbau der Verbindung zur DB: " + e.getMessage());
}
```

### Daten löschen

Auch das Löschen von Datensätzen unterschiedet sich kaum vom Aktualisieren bestehender oder Einfügen neuer Daten, außgenommen dem Delete-Statement selbst.

```java
try(Connection conn = DriverManager.getConnection(connectionUrl, user, pwd)) {
  System.out.println("Verbindung zur DB hergestellt!");

  PreparedStatement preparedStatement = conn.prepareStatement(
          "DELETE FROM `student` WHERE `student`.`id` = ?");

  try {
      preparedStatement.setInt(1, 5);
      int rowAffected = preparedStatement.executeUpdate(); //liefert die Anzahl der betroffenen Datensätze

      System.out.println(rowAffected + " Datensatz/Datensätze aktualisiert");
  } catch (SQLException ex) {
      System.out.println("Fehler beim löschen eines Datensatzes: " + ex.getMessage());
  }

} catch(SQLException e)  {
  System.out.println("Fehler bei Aufbau der Verbindung zur DB: " + e.getMessage());
}
```

## DAO - Data Access Object

Das Data Access Object oder Datenzugriffsobjekt ist ein Entwurfsmuster, das dazu verwendet wird um möglicht einfach die angesprochene Datenquelle zu tauschen (https://de.wikipedia.org/wiki/Data_Access_Object).

## Kurssystem

Gesteuert wird die Applikation über ein Kommandozeilenmenü (CLI).

### Datenbankverbindung

Mithilfe des Singleton Patterns kann sichergestellt werden, dass immer nur eine Datenbankverbindung hergestellt wird. Umgesetzt wird es mit einem privaten Konstruktor und einer statischen Methode.

```java
public class MySqlDatabaseConnection {

    private static Connection con = null;

    private MySqlDatabaseConnection() {
    }

    public static Connection getConnection(String url, String user, String pwd) throws ClassNotFoundException, SQLException {
        if (con != null) {
            return con;
        } else {
            Class.forName("com.mysql.cj.jdbc.Driver"); //überprüfen oder Diver Klasse existiert - wirft ClassNotFoundException
            con = DriverManager.getConnection(url, user, pwd); //wirft SQLException
            return con;
        }
    }
}
```

### Domänenklasse

Um eine gute Erweiterbarkeit zu gewährleisten wird eine abstrakte Basisentitätsklasse erzeugt, von der jede Domänenklasse erben kann. Sie implementiert die ID der Entitäten. Zusätzlich werden in den jeweiligen Settern die Attribute/Datenfelder validiert bzw. auf Gültigkeit überprüft (Exception).

### DAO Pattern

Jedes DAO bzw. in unserem Fall `Repository` erbt von einen BaseRepository. Dieses Interface verwendet Generics (Generische Klassen), also müssen alle weiteren Repositories, die davon erben, Typinformationen für die "Platzhalter" angeben. Das Interface stellt dann Standarddatenbankoperationen (CRUD) mit den Typen bereit.

```java
/**
 * @param <T> steht für ein Entity
 * @param <I> steht für eine ID
 */
public interface BaseRepository<T,I> {
    Optional<T> insert(T entity);
    Optional<T> getById(I id);
    List<T> getAll();
    Optional<T> update(T entity);
    void deleteById(I id);
}
```

Ein auf die Domänenklasse zugeschnittenes DAO (zB MyCourseRepository) erbt von diesem BaseRepository und definiert die weiteren Zugriffsmethoden. Auf dieses wird von außen zugegriffen, damit man technologieunabhängig bleibt. Schlussendlich wird für das DAO eine Klasse erstellt, die dann die Methoden ausimplementiert (sowohl aus dem Base- als auch aus dem DomänenRepository).

![DAO-Pattern](/DAO-Pattern.PNG)

### DAO Impelmentierung

#### GetAll

Innerhalb der konkreten Repositorys werden dann die definierten Methoden aus den Inetrfaces ausimplementiert. Die Methode getAll() bekommen wir aus unserem BaseRepository.

Interessant ist hier das ORM das wir selbst durchführen. Die Liste, die wir an unsere CLI weitergeben, hält ausschließlich Objekte, wodurch keine Abhängigkeit zwischen Datenbank und Anzeige entsteht.

```java
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
        throw new DatabaseException("Database error occurred!");
    }
}
```

#### GetById

Um uns nur einen Datensatz auf Basis der ID aus der Datenbank zu holen, verwenden wir die Methode getById(). Ihr wird eine ID mitgegeben und über eine Hilfsmethode wird erstmal überprüft ob überhaupt ein Eintrag mit dieser ID existiert. Falls ja, wird dieser ausgelesen und die eigehenden Daten werden wieder als Objekt zurückgeliefert.

```java
/**
* @param id des Kurses der gesucht ist
* @return einen Kurs (falls vorhanden)
*/
@Override
public Optional<Course> getById(Long id) {
  Assert.notNull(id);
  if (countCoursesInDbWithId(id) == 0) { //überprüft, ob ein Eintrag mit dieser ID existiert 
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
```

#### Create

Beim enfügen muss auf die Validierung der Eingaben (UI-Validierung) geachtet werden. Diese wird bestenfalls in der Anzeige selber (CLI) implementiert. Über `Statement.RETURN_GENERATED_KEYS` kann im preparedStatement der Schlüssel des Datensatzes hinterlegt werden. So wird nach des Ausführen des Insert-Befehls, das Objekt wieder aus der Datenbank geholt und zurückgegeben und erhält somit dann seine ID.

```java
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
```

#### Update

Das Update funktioniert im Großen und Ganzen wie die Insert-Methode. Unsere CLI muss sich zwar zusätzlich darum kümmern ob eine Entity mit dieser ID vorhanden ist, jedoch muss sich nicht nicht die ID aus der Datenbank geholt werden, da ja bereits ein Datensatz existiert. Ansonsten ist der Vorgang aber sehr ähnlich.

```java
/**
     * Ändert einen Kurs im System ab
     * @param entity das geupdatet werden soll
     * @return den geupdateten Kurs
     */
    @Override
    public Optional<Course> update(Course entity) {
        Assert.notNull(entity); //muss diese nicht weiter geworfen werden - oder geht das automatisch?

        String sql = "UPDATE `courses` SET `name` = ?, `description` = ?, `hours` = ?, `begindate` = ?, `enddate` = ?, `coursetype` = ? " +
                "WHERE `courses`.`id` = ?";

        if (countCoursesInDbWithId(entity.getId()) == 0) {
            return Optional.empty();
        } else {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, entity.getName());
                preparedStatement.setString(2, entity.getDescription());
                preparedStatement.setInt(3, entity.getHours());
                preparedStatement.setDate(4, entity.getBeginDate());
                preparedStatement.setDate(5, entity.getEndDate());
                preparedStatement.setString(6, entity.getCourseType().toString());
                preparedStatement.setLong(7, entity.getId());

                int affectedRows = preparedStatement.executeUpdate(); //Statement ausführen
                if (affectedRows == 0) { //wenn keine Spalten betroffen sind (wenn Update nicht funktioniert hat)
                    return Optional.empty();
                } else {
                    return this.getById(entity.getId()); //geupdatetes Objekt holen
                }

            } catch (SQLException e) {
                throw new DatabaseException(e.getMessage());
            }
        }
    }
```

#### Delete

Die Löschoperation ist recht einfach zu implementieren, da nur darauf geachtet werden muss, ob der Datensatz existiert. Hier stellt sich jedoch die Frage, die man der UI mitteilen möchte, ob das Löschen auch funktioniert hat oder nicht.  

```java
/**
     * Löscht einen Kurs aus dem System
     * @param id des Kurses der gelöscht werden soll
     */
    @Override
    public boolean deleteById(Long id) {
        Assert.notNull(id);
        String sql = "DELETE FROM `courses` WHERE `id` = ?";

        try {
            if (countCoursesInDbWithId(id) == 1) {
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setLong(1, id);
                int affectedRows = preparedStatement.executeUpdate();
                //wäre es hier nicht besser, noch auf "affectedRows" zu überprüfen - und sonst (k)ein Optional.empty() zurückzugeben (kein, weil ja gelöscht)
                //Video 11, Minute 9 (Homogenität? boolean oder Optional<Course>)
                if (affectedRows != 0) { //wenn keine Spalten betroffen sind (wenn Update nicht funktioniert hat)
                    return true;
                }
            }
        } catch (SQLException sqlException) {
            throw new DatabaseException(sqlException.getMessage());
        }
        return false;
    }
```