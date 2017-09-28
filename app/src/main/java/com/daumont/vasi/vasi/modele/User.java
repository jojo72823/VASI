package com.daumont.vasi.vasi.modele;

/**
 * Créé par JONATHAN DAUMONT le 01/06/2017.
 */

public class User {

    private int id_user;
    private String nom;
    private String prenom;
    private String type;
    private String identifiant;
    private String mot_de_passe;

    public User(int id_user, String nom, String prenom, String type, String identifiant, String mot_de_passe) {
        this.id_user = id_user;
        this.nom = nom;
        this.prenom = prenom;
        this.type = type;
        this.identifiant = identifiant;
        this.mot_de_passe = mot_de_passe;
    }

    public User(String nom, String prenom, String type, String identifiant, String mot_de_passe) {
        this.nom = nom;
        this.prenom = prenom;
        this.type = type;
        this.identifiant = identifiant;
        this.mot_de_passe = mot_de_passe;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public String getMot_de_passe() {
        return mot_de_passe;
    }

    public void setMot_de_passe(String mot_de_passe) {
        this.mot_de_passe = mot_de_passe;
    }
}
