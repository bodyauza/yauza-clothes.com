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

## ER-диаграмма сущностей в базе данных:

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