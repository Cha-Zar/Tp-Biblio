package com.example.bibliotheque.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Livre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 13)
    private String isbn;

    @Column(nullable = false)
    private String titre;

    private int anneePublication;

    private int nbPages;

    @ManyToMany
    @JoinTable(name = "livre_auteur",
            joinColumns = @JoinColumn(name = "livre_id"),
            inverseJoinColumns = @JoinColumn(name = "auteur_id"))
    private Set<Auteur> auteurs = new HashSet<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    @OneToMany(mappedBy = "livre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Exemplaire> exemplaires = new ArrayList<>();

    public Livre(String isbn, String titre, int anneePublication, int nbPages, Categorie categorie) {
        this.isbn = isbn;
        this.titre = titre;
        this.anneePublication = anneePublication;
        this.nbPages = nbPages;
        changerCategorie(categorie);
    }

    public void ajouterAuteur(Auteur a) {
        auteurs.add(a);
        a.getLivres().add(this);
    }

    public void retirerAuteur(Auteur a) {
        auteurs.remove(a);
        a.getLivres().remove(this);
    }

    public void changerCategorie(Categorie nouvelle) {
        if (this.categorie != null) {
            this.categorie.getLivres().remove(this);
        }
        this.categorie = nouvelle;
        if (nouvelle != null) {
            nouvelle.getLivres().add(this);
        }
    }

    public Exemplaire ajouterExemplaire(String codeBarre, EtatExemplaire etat) {
        Exemplaire ex = new Exemplaire(codeBarre, etat);
        ajouterExemplaire(ex);
        return ex;
    }

    public void ajouterExemplaire(Exemplaire ex) {
        exemplaires.add(ex);
        ex.setLivre(this);
    }

    public void retirerExemplaire(Exemplaire ex) {
        exemplaires.remove(ex);
        ex.setLivre(null);
    }

    @PrePersist
    @PreUpdate
    private void verifierAuMoinsUnAuteur() {
        if (auteurs.isEmpty()) {
            throw new IllegalStateException("Le livre " + isbn + " doit avoir au moins un auteur");
        }
    }
}
