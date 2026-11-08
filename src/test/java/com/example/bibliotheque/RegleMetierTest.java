package com.example.bibliotheque;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.bibliotheque.entity.Adherent;
import com.example.bibliotheque.entity.Auteur;
import com.example.bibliotheque.entity.Bibliothecaire;
import com.example.bibliotheque.entity.Categorie;
import com.example.bibliotheque.entity.Emprunt;
import com.example.bibliotheque.entity.EtatExemplaire;
import com.example.bibliotheque.entity.Exemplaire;
import com.example.bibliotheque.entity.Livre;
import com.example.bibliotheque.entity.StatutEmprunt;
import com.example.bibliotheque.exception.EmpruntImpossibleException;

class RegleMetierTest {
    private static final LocalDate J0 = LocalDate.of(2026, 11, 1);

    private Adherent adherent;
    private Bibliothecaire biblio;
    private Livre livre;

    @BeforeEach
    void setUp() {
        adherent = new Adherent("Ben Ali", "Youssef", "y@mail.tn", "CARTE-1", J0.minusYears(1));
        biblio = new Bibliothecaire("Trabelsi", "Sana", "s@biblio.tn", "BIB-1");
        livre = new Livre("9782070360024", "L'Étranger", 1942, 186, new Categorie("Roman"));
        livre.ajouterAuteur(new Auteur("Camus", "Albert", "Française"));
        for (int i = 1; i <= 5; i++) {
            livre.ajouterExemplaire("EX-" + i, EtatExemplaire.BON);
        }
    }

    private Exemplaire ex(int i) {
        return livre.getExemplaires().get(i - 1);
    }

    @Test
    void unEmpruntCreeAUneDateDeRetourPrevueA14JoursEtLeStatutEnCours() {
        Emprunt e = adherent.emprunter(ex(1), biblio, J0);

        assertEquals(J0.plusDays(14), e.getDateRetourPrevue());
        assertEquals(StatutEmprunt.EN_COURS, e.getStatut());
        assertNull(e.getDateRetourEffective());

        assertTrue(adherent.getEmprunts().contains(e));
        assertTrue(ex(1).getEmprunts().contains(e));
        assertTrue(biblio.getEmpruntsEnregistres().contains(e));
    }

    @Test
    void leQuatriemeEmpruntEnCoursEstRefuse() {
        adherent.emprunter(ex(1), biblio, J0);
        adherent.emprunter(ex(2), biblio, J0);
        adherent.emprunter(ex(3), biblio, J0);

        assertThrows(EmpruntImpossibleException.class,
                () -> adherent.emprunter(ex(4), biblio, J0));
        assertEquals(3, adherent.getEmprunts().size());
    }

    @Test
    void rendreUnLivreLibereUnePlace() {
        Emprunt e1 = adherent.emprunter(ex(1), biblio, J0);
        adherent.emprunter(ex(2), biblio, J0);
        adherent.emprunter(ex(3), biblio, J0);

        e1.rendre(J0.plusDays(5));

        assertEquals(2, adherent.nbEmpruntsEnCours());
        adherent.emprunter(ex(4), biblio, J0.plusDays(5));
        assertEquals(3, adherent.nbEmpruntsEnCours());
    }

    @Test
    void unEmpruntEnRetardCompteToujoursCommeEnCours() {
        Emprunt e = adherent.emprunter(ex(1), biblio, J0);
        e.actualiserStatut(J0.plusDays(20));

        assertEquals(StatutEmprunt.EN_RETARD, e.getStatut());
        assertEquals(1, adherent.nbEmpruntsEnCours());
    }

    @Test
    void unExemplaireDejaEmprunteNePeutPasEtreEmprunteUneSecondeFois() {
        adherent.emprunter(ex(1), biblio, J0);
        Adherent autre = new Adherent("Gharbi", "Lina", "l@mail.tn", "CARTE-2", J0);

        assertFalse(ex(1).estDisponible());
        assertThrows(EmpruntImpossibleException.class,
                () -> autre.emprunter(ex(1), biblio, J0));
    }

    @Test
    void estEnRetard() {
        Emprunt e = adherent.emprunter(ex(1), biblio, J0);

        assertFalse(e.estEnRetard(J0.plusDays(14)), "le jour J+14 n'est pas encore en retard");
        assertTrue(e.estEnRetard(J0.plusDays(15)), "à J+15 sans retour : en retard");

        e.rendre(J0.plusDays(16));
        assertFalse(e.estEnRetard(J0.plusDays(30)), "un emprunt rendu n'est plus en retard");
    }

    @Test
    void unExemplairePeutEtreEmprunteDeNouveauApresRetour() {
        Emprunt e = adherent.emprunter(ex(1), biblio, J0);
        e.rendre(J0.plusDays(3));

        adherent.emprunter(ex(1), biblio, J0.plusDays(4));

        assertEquals(2, ex(1).getEmprunts().size());
    }
}
