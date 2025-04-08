package fr.afpa.pompey.APIBoulangerie.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CategorieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getCategories() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].libCategorie", is("Viennoiserie")));
    }

    @Test
    void getCategorie() throws Exception {
        mockMvc.perform(get("/categorie/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("libCategorie", is("Pain")));
    }

    @Test
    void updateCategorie() throws Exception {
        mockMvc.perform(put("/categorie/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"libCategorie\": \"Catégorie modifiée\"}"))
                .andExpect(status().isOk());
    }

    /*@Test
    void createCategorie() throws Exception {
        mockMvc.perform(post("/categorie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"libCategorie\": \"Nouvelle catégorie\"}"))
                .andExpect(status().isOk());
    }*/

    @Test
    void deleteCategorie() throws Exception {
        mockMvc.perform(delete("/categorie/4")).andExpect(status().isOk());
    }

}