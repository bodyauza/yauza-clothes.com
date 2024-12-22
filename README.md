# yauza-clothes.com
Интернет-магазин отечественного бренда одежды.

# Yauza Clothes

## Screenshots

### Cart Page
![Cart Page](https://github.com/bodyauza/yauza-clothes.com/raw/master/cart_pc.jpg)

### Contacts Page
![Contacts Page](https://github.com/bodyauza/yauza-clothes.com/raw/master/contacts_pc.jpg)

### Home Page
![Home Page](https://github.com/bodyauza/yauza-clothes.com/raw/master/home_pc.jpg)

### Oversize Mobile Site
![Oversize Mobile Site](https://github.com/bodyauza/yauza-clothes.com/raw/master/oversize_mobile_site.jpg)

### Oversize PC Site
![Oversize PC Site](https://github.com/bodyauza/yauza-clothes.com/raw/master/oversize_pc.jpg)

### Scarf Mobile Site
![Scarf Mobile Site](https://github.com/bodyauza/yauza-clothes.com/raw/master/scarf_mobile_site.jpg)

### Scarf PC Site
![Scarf PC Site](https://github.com/bodyauza/yauza-clothes.com/raw/master/scarf_pc.jpg)

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
