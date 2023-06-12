package com.hood.merch.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Version;

import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column
    private String name;
    @OneToMany
    @JoinColumn(name = "id")
    private List<Sizes> item_size;
    @Column
    private String img;
    @Column
    private String color;
    @Column
    private int price;
    @Column
    private int quantity;

    @Column
    @Version
    private int in_stock;


    public Product() {
    }

    public Product(int id, String name, int price, List<Sizes> item_size, int quantity, String img, String color, int in_stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.item_size = item_size;
        this.quantity = quantity;
        this.img = img;
        this.color = color;
        this.in_stock = in_stock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return name.equals(product.name) && item_size.equals(product.item_size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, item_size);
    }
}
