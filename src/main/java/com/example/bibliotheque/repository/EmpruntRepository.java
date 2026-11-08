package com.example.bibliotheque.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bibliotheque.entity.Adherent;
import com.example.bibliotheque.entity.Emprunt;
import com.example.bibliotheque.entity.StatutEmprunt;

public interface EmpruntRepository extends JpaRepository<Emprunt, Long> {
    List<Emprunt> findByStatut(StatutEmprunt statut);

    long countByAdherentAndStatutNot(Adherent adherent, StatutEmprunt statut);
}
