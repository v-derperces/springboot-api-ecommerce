package fr.afpa.pompey.APIBoulangerie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "produit")
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produit")
    int idProduit;

    @Column(name = "lib_produit")
    @NotBlank(message = "Le libellé du produit doit être renseigné.")
    @Size(max = 50, message = "Le libellé du produit ne doit pas dépasser 50 caractères.")
    String libProduit;
}
