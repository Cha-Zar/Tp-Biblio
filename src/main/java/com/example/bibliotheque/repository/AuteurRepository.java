package com.example.bibliotheque.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bibliotheque.entity.Auteur;

public interface AuteurRepository extends JpaRepository<Auteur, Long> {
}
