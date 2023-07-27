package com.hood.merch.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Sizes {

    @Id
    @Column(name = "product_id")
    private int id;

    @OneToOne(mappedBy = "size")
    private Product product;

    @Column
    private String size;

    public Sizes() {

    }

    public Sizes(String size) {
        this.size = size;
    }

}