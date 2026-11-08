package com.example.bibliotheque;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.bibliotheque.entity.Adherent;
import com.example.bibliotheque.entity.Auteur;
import com.example.bibliotheque.entity.Bibliothecaire;
import com.example.bibliotheque.entity.Categorie;
import com.example.bibliotheque.entity.EtatExemplaire;
import com.example.bibliotheque.entity.Livre;
import com.example.bibliotheque.repository.AdherentRepository;
import com.example.bibliotheque.repository.AuteurRepository;
import com.example.bibliotheque.repository.BibliothecaireRepository;
import com.example.bibliotheque.repository.CategorieRepository;
import com.example.bibliotheque.repository.LivreRepository;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {
    private final CategorieRepository categorieRepository;
    private final AuteurRepository auteurRepository;
    private final LivreRepository livreRepository;
    private final AdherentRepository adherentRepository;
    private final BibliothecaireRepository bibliothecaireRepository;

    public DataInitializer(CategorieRepository categorieRepository,
                           AuteurRepository auteurRepository,
                           LivreRepository livreRepository,
                           AdherentRepository adherentRepository,
                           BibliothecaireRepository bibliothecaireRepository) {
        this.categorieRepository = categorieRepository;
        this.auteurRepository = auteurRepository;
        this.livreRepository = livreRepository;
        this.adherentRepository = adherentRepository;
        this.bibliothecaireRepository = bibliothecaireRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Categorie roman = categorieRepository.save(new Categorie("Roman"));
        Categorie science = categorieRepository.save(new Categorie("Science"));

        Auteur camus = auteurRepository.save(new Auteur("Camus", "Albert", "Française"));
        Auteur hugo = auteurRepository.save(new Auteur("Hugo", "Victor", "Française"));
        Auteur hawking = auteurRepository.save(new Auteur("Hawking", "Stephen", "Britannique"));
        Auteur mlodinow = auteurRepository.save(new Auteur("Mlodinow", "Leonard", "Américaine"));

        Livre etranger = new Livre("9782070360024", "L'Étranger", 1942, 186, roman);
        etranger.ajouterAuteur(camus);
        etranger.ajouterExemplaire("EX-0001", EtatExemplaire.NEUF);
        etranger.ajouterExemplaire("EX-0002", EtatExemplaire.BON);

        Livre miserables = new Livre("9782253096337", "Les Misérables", 1862, 1900, roman);
        miserables.ajouterAuteur(hugo);
        miserables.ajouterExemplaire("EX-0003", EtatExemplaire.USE);

        Livre grandDessein = new Livre("9782738125736", "Y a-t-il un grand architecte dans l'Univers ?",
                2010, 238, science);
        grandDessein.ajouterAuteur(hawking);
        grandDessein.ajouterAuteur(mlodinow);
        grandDessein.ajouterExemplaire("EX-0004", EtatExemplaire.ABIME);

        livreRepository.save(etranger);
        livreRepository.save(miserables);
        livreRepository.save(grandDessein);

        Bibliothecaire biblio = bibliothecaireRepository.save(
                new Bibliothecaire("Trabelsi", "Sana", "sana.trabelsi@biblio.tn", "BIB-001"));
        Adherent adherent = new Adherent("Ben Ali", "Youssef", "youssef.benali@mail.tn",
                "CARTE-1001", LocalDate.now().minusMonths(6));

        adherent.emprunter(etranger.getExemplaires().get(0), biblio);
        adherentRepository.save(adherent);
    }
}
