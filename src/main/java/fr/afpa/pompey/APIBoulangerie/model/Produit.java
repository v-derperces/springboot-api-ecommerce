package fr.afpa.pompey.APIBoulangerie.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

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

    @ManyToMany
    @NotNull(message = "La liste des catégories ne doit pas être vide.")
    @Size(min = 1, message = "Vous devez renseigner au moins une catégorie.")
    @Valid
    @JoinTable(
            name = "appartenir",
            joinColumns = @JoinColumn(name = "id_produit"),
            inverseJoinColumns = @JoinColumn(name = "id_categorie"))
    List<Categorie> listeCategories;
}
