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
    @Column
    private String FIO;
    @Column
    private String email;
    @Column
    private String tel;
    @Column
    private String post;
    @Column
    private String address;
    @Column
    private String products;
    @Column
    private String status;
    @Column
    private int total_price;
    @Column
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
