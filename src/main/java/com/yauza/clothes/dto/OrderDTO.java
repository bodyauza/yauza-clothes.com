package com.yauza.clothes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class OrderDTO {

    private List<Long> id;

    private List<Integer> quantity;

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

    public OrderDTO(List<Long> id, List<Integer> quantity, String FIO, String email, String tel, String post, String street, String home,
                    String country, String city, String region, String index) {
        this.id = id;
        this.quantity = quantity;
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
    }

}
