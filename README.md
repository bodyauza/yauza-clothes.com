# yauza-clothes.com
Интернет-магазин отечественного бренда одежды.

## Screenshots

### Home Page

![Home Page](https://github.com/bodyauza/yauza-clothes.com/raw/master/home_page.png)

### Oversize Page

![Oversize Page](https://github.com/bodyauza/yauza-clothes.com/raw/master/oversize_page.png)

### Cart Page

![Cart Page](https://github.com/bodyauza/yauza-clothes.com/raw/master/cart_page.png)

### Contacts Page

![Contacts Page](https://github.com/bodyauza/yauza-clothes.com/raw/master/contacts_page.png)

## Technological Stack

### Backend
- **Java**: 22
- **Spring**: Boot, Data, Security, Mail, MockMVC
- **JJWT API**
- **Lombok**: 1.18.30

### Build Tools
- **Maven**: 3.9.8

### Database
- **PostgreSQL**: 17.5
- **Liquibase**: 4.17.2

### Testing Tools
- **Postman**: 11.27.3

### Frontend
- **HTML5**, **CSS3**, **JavaScript**
- **Thymeleaf**: 3.1.3

### Other Tools
- **Amplicode**: 2025.1

## ER-diagram of entities in the database:

   ```mermaid
   erDiagram
    person {
        BIGSERIAL id PK
        VARCHAR login "UNIQUE"
        VARCHAR email "UNIQUE"
        VARCHAR password
        VARCHAR phone "UNIQUE"
        VARCHAR first_name
        VARCHAR last_name
    }

    roles {
        BIGINT person_id FK
        VARCHAR role
    }

    refresh_tokens {
        BIGSERIAL id PK
        VARCHAR token "UNIQUE"
        BIGINT user_id FK
        TIMESTAMP expiry_date
    }

    orders {
        BIGSERIAL id PK
        VARCHAR(255) fio
        VARCHAR(255) email
        VARCHAR(255) tel
        VARCHAR(255) address
        TIMESTAMP date
        VARCHAR(255) post
        VARCHAR(255) products
        VARCHAR(255) status
        INT total_price
    }

    sizes {
        BIGSERIAL id PK
        VARCHAR(50) size
    }

    product {
        BIGSERIAL id PK
        VARCHAR name
        VARCHAR img
        VARCHAR(50) color
        INT price
        INT quantity
        INT in_stock
        BIGINT size_id FK
    }

    person ||--o{ roles : "has"
    person ||--o{ refresh_tokens : "has"
    product }o--|| sizes : "references"
   ```

## Authentication Process

1. **Login and Password Request**  
   The client sends a request to the server with an object containing the user's login and password.

2. **Token Generation**  
   If the entered password is correct, the server generates access and refresh tokens and returns them to the client.

3. **Using the Access Token**  
   The client uses the received access token to interact with the API. All subsequent requests to protected routes must
   include this token in the authorization header.

4. **Access Token Renewal**  
   The access token has a validity period, usually 5 minutes. When the validity of this token expires, the client sends
   the refresh token to the server and receives a new access token. This process is repeated until the refresh token
   expires.

5. **Extending the Refresh Token**  
   The refresh token is issued for 30 days. Approximately 1-5 days before the expiration of the refresh token, the
   client sends a request with valid refresh token to obtain a new pair of tokens.

## Local development

### 1. Create a system variable `JAVA_HOME`, the value of the variable is the path to the JDK installation directory, for example `C:\Program Files\Java\jdk-XX`.

### 2. Add the new path `%JAVA_HOME%\bin` to the end of the value of the `Path` variable.

### 3. Download the `Apache Maven` binary distribution archive from [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi).

### 4. Extract the distribution archive in any directory. Use `unzip apache-maven-3.9.10-bin.zip` or `tar xzvf apache-maven-3.9.10-bin.tar.gz` depending on the archive.

### 5. Add the `bin` directory of the created directory `apache-maven-3.9.10` to the `Path` environment variable.

### 6. Confirm with `mvn -v` in a new shell. The result should look similar to:

```
Apache Maven 3.9.10 (5f519b97e944483d878815739f519b2eade0a91d)
Maven home: /opt/apache-maven-3.9.10
Java version: 1.8.0_45, vendor: Oracle Corporation
Java home: /Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre
Default locale: en_US, platform encoding: UTF-8
OS name: "mac os x", version: "10.8.5", arch: "x86_64", family: "mac"
```

### 7. Open the file `creating-tables.xml` and create a new user:

```sql
    CREATE ROLE "user" WITH LOGIN PASSWORD 'password';
```

Duplicate the name and password in the `application.properties` file:

```properties
    spring.datasource.username=
    spring.datasource.password=
```

### 8. Run the command in the root directory of the project:

```bash
    mvn spring-boot:run
```

When the program starts, migration occurs to the "yauza_clothes_db" database.

### 9. To run all test classes, run the command:

```bash
mvn test
```

To run all tests for a specified class, run the command:

```bash
mvn test -Dtest=ClassNameTest
```

If you need to run a specific method, use:

```bash
mvn test -Dtest=ClassNameTest#methodName
```
