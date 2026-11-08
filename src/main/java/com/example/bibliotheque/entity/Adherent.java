package com.example.bibliotheque.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.bibliotheque.exception.EmpruntImpossibleException;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Adherent extends Personne {
    public static final int MAX_EMPRUNTS_EN_COURS = 3;

    @Column(nullable = false, unique = true)
    private String numeroCarte;

    @Column(nullable = false)
    private LocalDate dateInscription;

    @OneToMany(mappedBy = "adherent", cascade = CascadeType.PERSIST)
    private List<Emprunt> emprunts = new ArrayList<>();

    public Adherent(String nom, String prenom, String email,
                    String numeroCarte, LocalDate dateInscription) {
        super(nom, prenom, email);
        this.numeroCarte = numeroCarte;
        this.dateInscription = dateInscription;
    }

    public long nbEmpruntsEnCours() {
        return emprunts.stream()
                .filter(Emprunt::estEnCours)
                .count();
    }

    public Emprunt emprunter(Exemplaire ex, Bibliothecaire b) {
        return emprunter(ex, b, LocalDate.now());
    }

    public Emprunt emprunter(Exemplaire ex, Bibliothecaire b, LocalDate dateEmprunt) {
        if (ex == null || b == null) {
            throw new IllegalArgumentException("L'exemplaire et le bibliothécaire sont obligatoires");
        }
        if (nbEmpruntsEnCours() >= MAX_EMPRUNTS_EN_COURS) {
            throw new EmpruntImpossibleException(
                    "L'adhérent " + numeroCarte + " a déjà " + MAX_EMPRUNTS_EN_COURS
                    + " emprunts en cours");
        }
        if (!ex.estDisponible()) {
            throw new EmpruntImpossibleException(
                    "L'exemplaire " + ex.getCodeBarre() + " est déjà emprunté");
        }

        Emprunt e = new Emprunt(this, ex, b, dateEmprunt);
        this.emprunts.add(e);
        ex.getEmprunts().add(e);
        b.getEmpruntsEnregistres().add(e);
        return e;
    }
}
