package com.hood.merch.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderedItem {

    private int id;

    private int quantity;

    public OrderedItem(int id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }
}
