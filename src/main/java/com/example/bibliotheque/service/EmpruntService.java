package com.example.bibliotheque.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bibliotheque.entity.Adherent;
import com.example.bibliotheque.entity.Bibliothecaire;
import com.example.bibliotheque.entity.Emprunt;
import com.example.bibliotheque.entity.Exemplaire;
import com.example.bibliotheque.entity.StatutEmprunt;
import com.example.bibliotheque.repository.AdherentRepository;
import com.example.bibliotheque.repository.BibliothecaireRepository;
import com.example.bibliotheque.repository.EmpruntRepository;
import com.example.bibliotheque.repository.ExemplaireRepository;

@Service
@Transactional
public class EmpruntService {
    private final AdherentRepository adherentRepository;
    private final ExemplaireRepository exemplaireRepository;
    private final BibliothecaireRepository bibliothecaireRepository;
    private final EmpruntRepository empruntRepository;

    public EmpruntService(AdherentRepository adherentRepository,
                          ExemplaireRepository exemplaireRepository,
                          BibliothecaireRepository bibliothecaireRepository,
                          EmpruntRepository empruntRepository) {
        this.adherentRepository = adherentRepository;
        this.exemplaireRepository = exemplaireRepository;
        this.bibliothecaireRepository = bibliothecaireRepository;
        this.empruntRepository = empruntRepository;
    }

    public Emprunt emprunter(Long adherentId, Long exemplaireId, Long bibliothecaireId) {
        Adherent adherent = adherentRepository.findById(adherentId)
                .orElseThrow(() -> new IllegalArgumentException("Adhérent introuvable : " + adherentId));
        Exemplaire exemplaire = exemplaireRepository.findById(exemplaireId)
                .orElseThrow(() -> new IllegalArgumentException("Exemplaire introuvable : " + exemplaireId));
        Bibliothecaire bibliothecaire = bibliothecaireRepository.findById(bibliothecaireId)
                .orElseThrow(() -> new IllegalArgumentException("Bibliothécaire introuvable : " + bibliothecaireId));

        Emprunt emprunt = adherent.emprunter(exemplaire, bibliothecaire);
        return empruntRepository.save(emprunt);
    }

    public void rendre(Long empruntId) {
        Emprunt emprunt = empruntRepository.findById(empruntId)
                .orElseThrow(() -> new IllegalArgumentException("Emprunt introuvable : " + empruntId));
        emprunt.rendre(LocalDate.now());
    }

    public int actualiserRetards() {
        List<Emprunt> enCours = empruntRepository.findByStatut(StatutEmprunt.EN_COURS);
        LocalDate aujourdhui = LocalDate.now();
        int nb = 0;
        for (Emprunt e : enCours) {
            if (e.estEnRetard(aujourdhui)) {
                e.actualiserStatut(aujourdhui);
                nb++;
            }
        }
        return nb;
    }
}
