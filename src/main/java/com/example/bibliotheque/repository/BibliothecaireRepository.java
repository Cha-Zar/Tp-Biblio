package com.example.bibliotheque.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bibliotheque.entity.Bibliothecaire;

public interface BibliothecaireRepository extends JpaRepository<Bibliothecaire, Long> {
    Optional<Bibliothecaire> findByMatricule(String matricule);
}
