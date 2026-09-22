package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs_fichiers")
public class LogFichier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomFichier;

    @Column(nullable = false)
    private String statut; // Ex: "SUCCES", "ERREUR"

    private String message;

    private LocalDateTime dateOperation;

    public LogFichier() {}

    public LogFichier(String nomFichier, String statut, String message) {
        this.nomFichier = nomFichier;
        this.statut = statut;
        this.message = message;
        this.dateOperation = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public String getNomFichier() { return nomFichier; }
    public void setNomFichier(String nomFichier) { this.nomFichier = nomFichier; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getDateOperation() { return dateOperation; }
    public void setDateOperation(LocalDateTime dateOperation) { this.dateOperation = dateOperation; }
}