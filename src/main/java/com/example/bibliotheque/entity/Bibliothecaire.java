package com.example.bibliotheque.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Bibliothecaire extends Personne {
    @Column(nullable = false, unique = true)
    private String matricule;

    @OneToMany(mappedBy = "bibliothecaire")
    private List<Emprunt> empruntsEnregistres = new ArrayList<>();

    public Bibliothecaire(String nom, String prenom, String email, String matricule) {
        super(nom, prenom, email);
        this.matricule = matricule;
    }
}
