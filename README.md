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

## ER-diagram of entities in the database:

   ```mermaid
   erDiagram
    ORDER {
        BIGINT id PK "AUTO_INCREMENT"
        VARCHAR fio
        VARCHAR email
        VARCHAR tel
        VARCHAR address
        DATETIME date
        VARCHAR post
        VARCHAR products
        VARCHAR status
        INT total_price
    }

    SIZES {
        BIGINT id PK
        VARCHAR size
    }

    PRODUCT {
        BIGINT id PK
        VARCHAR name
        VARCHAR img
        VARCHAR color
        INT price
        INT quantity
        INT in_stock
        BIGINT size_id FK
    }

    PRODUCT ||--o{ SIZES : "has"
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
   client sends a request with valid access and refresh tokens to obtain a new pair of tokens.

## Local development

For local development and testing, open the file `creating-tables.xml` and create a new user:

```sql
    CREATE USER 'user'@'localhost' IDENTIFIED BY 'password';
```

Duplicate the name and password in the `application.properties` file:

```properties
    spring.datasource.username=${MYSQL_USERNAME:user}
    spring.datasource.password=${MYSQL_PASSWORD:password}
```

Duplicate the name and password in the `docker-compose.yaml` file:

```yaml
    environment:
      MYSQL_USER: user
      MYSQL_PASSWORD: password
```

And run command:

```bash
    docker-compose up --build
```

When the program starts, migration occurs to the "yauza_clothes_db" database.

