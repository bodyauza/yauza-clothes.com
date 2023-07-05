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
    @Column
    private int price;
    @Column
    private int quantity;
    @Column
    private String img;
    @Column
    private String color;
    @Column
    @Version
    private int in_stock;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Sizes size;


    public Product() {
    }

    public Product(int id, String name, int price, int quantity, String img, String color, int in_stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.img = img;
        this.color = color;
        this.in_stock = in_stock;
    }
}
