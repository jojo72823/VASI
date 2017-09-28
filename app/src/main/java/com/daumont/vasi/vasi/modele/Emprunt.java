package com.daumont.vasi.vasi.modele;

/**
 * Créé par JONATHAN DAUMONT le 01/06/2017.
 */

public class Emprunt {
    private int id_emprunt;
    private int id_proprietaire;
    private int id_emprunteur;
    private int qr_code;
    private String etat_emprunt;

    public Emprunt(int id_proprietaire,int id_emprunteur,int qr_code,String etat_emprunt){
        this.id_proprietaire = id_proprietaire;
        this.id_emprunteur = id_emprunteur;
        this.qr_code = qr_code;
        this.etat_emprunt = etat_emprunt;
    }

    public Emprunt(int id_emprunt,int id_proprietaire,int id_emprunteur,int qr_code,String etat_emprunt){
        this.id_emprunt = id_emprunt;
        this.id_proprietaire = id_proprietaire;
        this.id_emprunteur = id_emprunteur;
        this.qr_code = qr_code;
        this.etat_emprunt = etat_emprunt;
    }

    public int getId_emprunt() {
        return id_emprunt;
    }

    public void setId_emprunt(int id_emprunt) {
        this.id_emprunt = id_emprunt;
    }

    public int getId_proprietaire() {
        return id_proprietaire;
    }

    public void setId_proprietaire(int id_proprietaire) {
        this.id_proprietaire = id_proprietaire;
    }

    public int getId_emprunteur() {
        return id_emprunteur;
    }

    public void setId_emprunteur(int id_emprunteur) {
        this.id_emprunteur = id_emprunteur;
    }

    public int getQr_code() {
        return qr_code;
    }

    public void setQr_code(int qr_code) {
        this.qr_code = qr_code;
    }


    public String getEtat_emprunt() {
        return etat_emprunt;
    }

    public void setEtat_emprunt(String etat_emprunt) {
        this.etat_emprunt = etat_emprunt;
    }
}
