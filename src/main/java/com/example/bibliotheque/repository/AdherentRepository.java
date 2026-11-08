package com.example.bibliotheque.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bibliotheque.entity.Adherent;

public interface AdherentRepository extends JpaRepository<Adherent, Long> {
    Optional<Adherent> findByNumeroCarte(String numeroCarte);
}
