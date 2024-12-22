# yauza-clothes.com
Интернет-магазин отечественного бренда одежды.

## Screenshots

### Home Page

![Home Page](https://github.com/bodyauza/yauza-clothes.com/raw/master/home_pc.jpg)

### Oversize PC Page

![Oversize PC Page](https://github.com/bodyauza/yauza-clothes.com/raw/master/oversize_pc.jpg)

### Oversize Mobile Page

![Oversize Mobile Page](https://github.com/bodyauza/yauza-clothes.com/raw/master/oversize_mobile_site.jpg)

### Scarf PC Page

![Scarf PC Page](https://github.com/bodyauza/yauza-clothes.com/raw/master/scarf_pc.jpg)

### Scarf Mobile Page

![Scarf Mobile Page](https://github.com/bodyauza/yauza-clothes.com/raw/master/scarf_mobile_site.jpg)

### Cart Page

![Cart Page](https://github.com/bodyauza/yauza-clothes.com/raw/master/cart_pc.jpg)

### Contacts Page

![Contacts Page](https://github.com/bodyauza/yauza-clothes.com/raw/master/contacts_pc.jpg)

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
