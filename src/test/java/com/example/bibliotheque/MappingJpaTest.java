package com.example.bibliotheque;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.bibliotheque.entity.Adherent;
import com.example.bibliotheque.entity.Auteur;
import com.example.bibliotheque.entity.Bibliothecaire;
import com.example.bibliotheque.entity.Categorie;
import com.example.bibliotheque.entity.EtatExemplaire;
import com.example.bibliotheque.entity.Exemplaire;
import com.example.bibliotheque.entity.Livre;
import com.example.bibliotheque.entity.StatutEmprunt;
import com.example.bibliotheque.exception.EmpruntImpossibleException;
import com.example.bibliotheque.repository.EmpruntRepository;
import com.example.bibliotheque.repository.ExemplaireRepository;
import com.example.bibliotheque.repository.LivreRepository;

@DataJpaTest
class MappingJpaTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @Autowired
    private EmpruntRepository empruntRepository;

    private Livre creerLivre(String isbn, String... codesBarres) {
        Categorie roman = em.persist(new Categorie("Roman-" + isbn));
        Auteur camus = em.persist(new Auteur("Camus", "Albert", "Française"));
        Livre livre = new Livre(isbn, "L'Étranger", 1942, 186, roman);
        livre.ajouterAuteur(camus);
        for (String code : codesBarres) {
            livre.ajouterExemplaire(code, EtatExemplaire.BON);
        }
        return livreRepository.save(livre);
    }

    @Test
    void lesTablesDeLHeritageJoinedSontCreees() {
        Number nb = (Number) em.getEntityManager().createNativeQuery(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES "
                + "WHERE UPPER(TABLE_NAME) IN ('PERSONNE', 'ADHERENT', 'BIBLIOTHECAIRE')")
                .getSingleResult();
        assertThat(nb.intValue()).isEqualTo(3);
    }

    @Test
    void enregistrerUnLivreEnregistreSesExemplaires() {
        creerLivre("1111111111111", "A-1", "A-2");
        em.flush();
        em.clear();

        assertThat(exemplaireRepository.count()).isEqualTo(2);
    }

    @Test
    void supprimerUnLivreSupprimeSesExemplaires() {
        Long id = creerLivre("2222222222222", "B-1", "B-2", "B-3").getId();
        em.flush();
        em.clear();

        livreRepository.deleteById(id);
        em.flush();

        assertThat(exemplaireRepository.count()).isZero();
    }

    @Test
    void retirerUnExemplaireDeLaListeLeSupprime() {
        Livre livre = creerLivre("3333333333333", "C-1", "C-2");
        em.flush();

        livre.retirerExemplaire(livre.getExemplaires().get(0));
        em.flush();

        assertThat(exemplaireRepository.count()).isEqualTo(1);
    }

    @Test
    void unLivreSansAuteurEstRefuse() {
        Categorie c = em.persist(new Categorie("Science"));
        Livre sansAuteur = new Livre("4444444444444", "Anonyme", 2000, 100, c);

        assertThrows(Exception.class, () -> em.persistAndFlush(sansAuteur));
    }

    @Test
    void leQuatriemeEmpruntEstRefuseEtLeStatutEstStockeEnTexte() {
        Livre livre = creerLivre("5555555555555", "D-1", "D-2", "D-3", "D-4");
        Bibliothecaire b = em.persist(new Bibliothecaire("Trabelsi", "Sana", "s@biblio.tn", "BIB-1"));
        Adherent a = new Adherent("Ben Ali", "Youssef", "y@mail.tn", "CARTE-1", LocalDate.now());

        List<Exemplaire> ex = livre.getExemplaires();
        a.emprunter(ex.get(0), b);
        a.emprunter(ex.get(1), b);
        a.emprunter(ex.get(2), b);
        em.persist(a);
        em.flush();

        assertThrows(EmpruntImpossibleException.class, () -> a.emprunter(ex.get(3), b));
        assertThat(empruntRepository.count()).isEqualTo(3);
        assertThat(empruntRepository.countByAdherentAndStatutNot(a, StatutEmprunt.RENDU)).isEqualTo(3);

        Object statut = em.getEntityManager()
                .createNativeQuery("SELECT CAST(statut AS VARCHAR(20)) FROM emprunt FETCH FIRST 1 ROWS ONLY")
                .getSingleResult();
        assertThat(statut.toString()).isEqualTo("EN_COURS");
    }
}
