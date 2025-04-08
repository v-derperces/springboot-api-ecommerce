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
class ProduitControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getProduits() throws Exception {
        mockMvc.perform(get("/produits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].libProduit", is("Macaron")));
    }

    @Test
    void getProduit() throws Exception {
        mockMvc.perform(get("/produit/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("libProduit", is("Macaron")));
    }

   /* @Test
    void updateProduit() throws Exception {
        mockMvc.perform(put("/produit/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"libProduit\": \"Catégorie modifiée\"}"))
                .andExpect(status().isOk());
    }*/

    /*@Test
    void createProduit() throws Exception {
        mockMvc.perform(post("/produit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"libProduit\": \"Nouvelle catégorie\"}"))
                .andExpect(status().isOk());
    }*/

    @Test
    void deleteProduit() throws Exception {
        mockMvc.perform(delete("/produit/4")).andExpect(status().isOk());
    }
}