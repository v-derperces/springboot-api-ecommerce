package fr.afpa.pompey.APIBoulangerie.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "produit")
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProduit")
    int idProduit;

    @Column(name = "libProduit")
    String libProduit;
}
