package com.hood.merch.dto;

import com.hood.merch.models.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Cart {

    private int id;

    private String name;

    private String img;

    private String color;

    private int price;

    private String size;

    private int quantity;

    private int in_stock;


    public Cart() {
    }

    public Cart(int id, String name, int price, String size, int quantity, String img, String color, int in_stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.size = size;
        this.quantity = quantity;
        this.img = img;
        this.color = color;
        this.in_stock = in_stock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return name.equals(cart.name) && size.equals(cart.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, size);
    }
}
