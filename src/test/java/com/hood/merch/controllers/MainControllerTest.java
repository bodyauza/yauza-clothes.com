package com.hood.merch.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

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

    @BeforeEach
    public void setup() {

    }

    @Test
    public void pay() throws Exception {
        mockMvc.perform(get("/pay")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void creatingOrder() throws Exception {
        mockMvc.perform(get("/creating-order")
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void home() throws Exception {
        mockMvc.perform(get("/")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void addItem() throws Exception {
        mockMvc.perform(post("/add-item")
                        .param("img", "/oversize.png")
                        .param("id", "2")
                        .param("name", "OVERSIZE SAKURA")
                        .param("price", "1500")
                        .param("size", "XS")
                        .param("quantity", "1")
                        .param("color", "white")
                        .param("in_stock", "5")
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void removeItem() throws Exception {
        mockMvc.perform(post("/remove-item")
                        .param("id", "2")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void inCart() throws Exception {
        mockMvc.perform(post("/in-cart")
                        .param("img", "/oversize.png")
                        .param("id", "2")
                        .param("name", "OVERSIZE SAKURA")
                        .param("price", "1500")
                        .param("item_size", "XS")
                        .param("quantity", "1")
                        .param("color", "white")
                        .param("in_stock", "5")
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void newOrder() throws Exception {
        mockMvc.perform(post("/new-order")
                        .param("id", "2")
                        .param("quantity", "3")
                        .param("FIO", "Иванов Иван Иванович")
                        .param("email", "andreev@mail.ru")
                        .param("tel", "+71234567890")
                        .param("post", "Самовывоз")
                        .param("street", "ул.Пушкина")
                        .param("home", "1")
                        .param("country", "Российская Федерация")
                        .param("city", "Москва")
                        .param("region", "Москва")
                        .param("index", "111111")
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void cart() throws Exception {
        mockMvc.perform(get("/cart")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void scarf() throws Exception {
        mockMvc.perform(get("/scarf")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void contacts() throws Exception {
        mockMvc.perform(get("/contacts")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
