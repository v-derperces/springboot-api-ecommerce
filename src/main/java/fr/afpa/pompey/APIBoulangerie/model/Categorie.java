package fr.afpa.pompey.APIBoulangerie.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "categorie")
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categorie")
    int idCategorie;

    @Column(name = "lib_categorie")
    String libCategorie;
}
