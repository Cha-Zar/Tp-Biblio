package com.example.bibliotheque.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Emprunt {
    public static final int DUREE_JOURS = 14;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dateEmprunt;

    @Column(nullable = false)
    private LocalDate dateRetourPrevue;

    private LocalDate dateRetourEffective;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutEmprunt statut;

    @ManyToOne(optional = false)
    @JoinColumn(name = "adherent_id", nullable = false)
    private Adherent adherent;

    @ManyToOne(optional = false)
    @JoinColumn(name = "exemplaire_id", nullable = false)
    private Exemplaire exemplaire;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bibliothecaire_id", nullable = false)
    private Bibliothecaire bibliothecaire;

    public Emprunt(Adherent adherent, Exemplaire exemplaire, Bibliothecaire bibliothecaire,
                   LocalDate dateEmprunt) {
        this.adherent = adherent;
        this.exemplaire = exemplaire;
        this.bibliothecaire = bibliothecaire;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevue = dateEmprunt.plusDays(DUREE_JOURS);
        this.statut = StatutEmprunt.EN_COURS;
    }

    public boolean estEnRetard() {
        return estEnRetard(LocalDate.now());
    }

    public boolean estEnRetard(LocalDate aujourdhui) {
        return dateRetourEffective == null && aujourdhui.isAfter(dateRetourPrevue);
    }

    public boolean estEnCours() {
        return statut != StatutEmprunt.RENDU;
    }

    public void actualiserStatut(LocalDate aujourdhui) {
        if (estEnRetard(aujourdhui)) {
            statut = StatutEmprunt.EN_RETARD;
        }
    }

    public void rendre(LocalDate dateRetour) {
        if (statut == StatutEmprunt.RENDU) {
            throw new IllegalStateException("Cet emprunt a déjà été rendu");
        }
        this.dateRetourEffective = dateRetour;
        this.statut = StatutEmprunt.RENDU;
    }
}
