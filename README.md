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

## Local development

For local development and testing, open the file `creating-tables.xml` and create a new user:

```sql
    CREATE USER 'user'@'localhost' IDENTIFIED BY 'password';
```

Duplicate the name and password in the `application.properties` file:

```properties
    spring.datasource.username=...
    spring.datasource.password=...
```

Duplicate the name and password in the `docker-compose.yaml` file:

```yaml
    environment:
      MYSQL_USER: user
      MYSQL_PASSWORD: password
```

When the program starts, migration occurs to the "yauza_clothes_db" database.

## Technological stack
