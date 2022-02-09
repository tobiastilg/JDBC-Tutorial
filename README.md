# JDBC - Datenbankzugriff mit Java #

## Entwicklungumgebung ##

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

## JDBC Intro ##

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

### Verbindung herstellen ###

Über den `DriverManager` (java.sql.DriverManager) wird die Verbindung aufgebaut. Beim Arbeiten mit Datenbanken können häufig Exceptions auftreten, um die man sich zusätzlich kümmern muss.

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

## Datenbankverbindnung ##

## JDBC und DAO ##