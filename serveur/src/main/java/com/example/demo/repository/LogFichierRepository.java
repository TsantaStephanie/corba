package com.example.demo.repository;

import com.example.demo.entity.LogFichier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogFichierRepository extends JpaRepository<LogFichier, Long> {
}