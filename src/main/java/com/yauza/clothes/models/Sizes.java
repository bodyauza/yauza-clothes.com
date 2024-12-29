package com.yauza.clothes.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Sizes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(mappedBy = "size", cascade = CascadeType.ALL)
    private List<Product> products;

    @Column
    private String size;

    public Sizes() {

    }

    public Sizes(String size) {
        this.size = size;
    }

}