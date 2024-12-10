package com.hood.merch.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the {@link MainController}
 */
@SpringBootTest
@AutoConfigureMockMvc
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

//    @BeforeEach
//    public void setup() {
//
//    }

    @Test
    public void addToCart() throws Exception {
        mockMvc.perform(post("/add-item")
                        .param("img", "\"oversize.png\"")
                        .param("id", "3")
                        .param("name", "OVERSIZE")
                        .param("price", "1400")
                        .param("size", "M")
                        .param("quantity", "1")
                        .param("color", "white")
                        .param("in_stock", "3"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void cartRemove() throws Exception {
        mockMvc.perform(post("/remove-item")
                        .param("attr_name", "item")
                        .with(user("user")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void inCart() throws Exception {
        mockMvc.perform(post("/in-cart")
                        .param("attr_name", "item")
                        .param("img", "\"oversize.png\"")
                        .param("id", "1")
                        .param("name", "OVERSIZE")
                        .param("price", "1400")
                        .param("item_size", "XS")
                        .param("quantity", "2")
                        .param("color", "white")
                        .param("in_stock", "3")
                        .with(user("user")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void newOrder() throws Exception {
        String orderDTO = """
                {
                    "FIO": "Ivanov Ivan Ivanovich",
                    "email": "ivan@mail.ru",
                    "tel": "81234567890",
                    "post": "Почта России 300 руб.",
                    "street": "ул.Пушкина",
                    "home": "10",
                    "country": "Russia",
                    "city": "Moscow",
                    "region": "Moscow",
                    "index": "111111",
                    "ordered_items": [
                      {
                        "id": 1,
                        "name": "OVERSIZE",
                        "price": 1400,
                        "size": "XS",
                        "quantity": 2,
                        "color": "white"
                      },
                      {
                        "id": 2,
                        "name": "OVERSIZE",
                        "price": 1400,
                        "size": "S",
                        "quantity": 1,
                        "color": "white"
                      }
                    ]
                }""";

        mockMvc.perform(post("/new-order")
                        .with(user("user"))
                        .content(orderDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void scarf() throws Exception {
        mockMvc.perform(get("/scarf")
                        .with(user("user")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void oversize() throws Exception {
        mockMvc.perform(get("/oversize")
                        .with(user("user")))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
