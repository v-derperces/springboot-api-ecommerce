package fr.afpa.pompey.APIBoulangerie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "categorie")
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categorie")
    int idCategorie;

    @Column(name = "lib_categorie")
    @NotBlank(message = "Le libellé de la catégorie doit être renseigné.")
    @Size(max = 30, message = "La catégorie ne doit pas dépasser 30 caractères.")
    String libCategorie;

    @ManyToMany
    List<Produit> listeProduit;
}
