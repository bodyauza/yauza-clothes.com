package com.yauza.clothes.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the {@link DifferentSizesController}
 */
@SpringBootTest
@AutoConfigureMockMvc
public class DifferentSizesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {

    }

    @Test
    public void oversize() throws Exception {
        mockMvc.perform(get("/oversize/{0}", "2")
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void sizeUpdate() throws Exception {
        mockMvc.perform(get("/size-update/{0}", "2")
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
