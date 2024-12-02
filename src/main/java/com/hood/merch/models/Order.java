package com.hood.merch.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String products, FIO, email, tel, post, address, status;
    private int total_price;

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
