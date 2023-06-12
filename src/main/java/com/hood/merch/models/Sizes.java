package com.hood.merch.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Sizes {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column
    private String size;

    public Sizes() {

    }

    public Sizes(String size) {
        this.size = size;
    }

}
