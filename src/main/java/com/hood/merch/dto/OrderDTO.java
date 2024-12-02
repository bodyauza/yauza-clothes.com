package com.hood.merch.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class OrderDTO {

    private String FIO;

    private String email;

    private String tel;

    private String post;

    private String street;

    private String home;

    private String country;

    private String city;

    private String region;

    private String index;

    private ArrayList<ProductDTO> ordered_items;

    public OrderDTO(ArrayList<ProductDTO> ordered_items, String FIO, String email, String tel, String post, String street, String home,
                    String country, String city, String region, String index) {
        this.FIO = FIO;
        this.email = email;
        this.tel = tel;
        this.post = post;
        this.street = street;
        this.home = home;
        this.country = country;
        this.city = city;
        this.region = region;
        this.index = index;
        this.ordered_items = ordered_items;
    }

}
