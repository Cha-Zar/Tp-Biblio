package com.example.bibliotheque.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Exemplaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codeBarre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatExemplaire etat;

    @ManyToOne(optional = false)
    @JoinColumn(name = "livre_id", nullable = false)
    private Livre livre;

    @OneToMany(mappedBy = "exemplaire")
    private List<Emprunt> emprunts = new ArrayList<>();

    public Exemplaire(String codeBarre, EtatExemplaire etat) {
        this.codeBarre = codeBarre;
        this.etat = etat;
    }

    public boolean estDisponible() {
        return emprunts.stream().noneMatch(Emprunt::estEnCours);
    }
}
