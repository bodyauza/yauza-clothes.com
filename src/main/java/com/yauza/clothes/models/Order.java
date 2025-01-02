package com.yauza.clothes.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Order {

    public Order() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String FIO;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String tel;
    @Column(nullable = false)
    private String post;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private String products;
    @Column(nullable = false)
    private String status;
    @Column(nullable = false)
    private int total_price;
    @Column(nullable = false)
    private Date date;

    public Order(String products, String FIO, String email, String tel, String post, String address, String status, int total_price, Date date) {
        this.products = products;
        this.FIO = FIO;
        this.email = email;
        this.tel = tel;
        this.post = post;
        this.address = address;
        this.status = status;
        this.total_price = total_price;
        this.date = date;
    }

}
