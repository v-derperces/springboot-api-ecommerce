package fr.afpa.pompey.APIBoulangerie.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "categorie")
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCategorie")
    int idCategorie;

    @Column(name = "libCategorie")
    String libCategorie;
}
