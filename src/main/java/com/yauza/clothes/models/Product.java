package com.yauza.clothes.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Version;

@Entity
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    @Column
    private String img;

    @Column
    private String color;

    @Column
    private int price;

    @Column
    private int quantity;

    @Column
    @Version // Предотвращает потерю изменений при конкурентном доступе к данным
    private int in_stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "size_id", nullable = false)
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