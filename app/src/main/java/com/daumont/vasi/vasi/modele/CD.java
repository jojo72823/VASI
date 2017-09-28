package com.daumont.vasi.vasi.modele;

/**
 * Créé par JONATHAN DAUMONT le 01/06/2017.
 */

public class CD {

    private int id_cd;
    private int qr_code;
    private String nom_album;
    private String nom_artist;
    private int id_proprio;
    private String image;

    public CD(int id_cd,String nom_album, String nom_artist, int id_proprio,String image,int id_qr_code){
        this.id_cd = id_cd;
        this.nom_album = nom_album;
        this.nom_artist = nom_artist;
        this.id_proprio = id_proprio;
        this.image = image;
        this.qr_code = id_qr_code;
    }

    public CD(int id_cd,String nom_album, String nom_artist, int id_proprio,String image){
        this.id_cd = id_cd;
        this.nom_album = nom_album;
        this.nom_artist = nom_artist;
        this.id_proprio = id_proprio;
        this.image = image;
    }


    public CD(String nom_album, String nom_artist, int id_proprio,String image){
        this.nom_album = nom_album;
        this.nom_artist = nom_artist;
        this.id_proprio = id_proprio;
        this.image = image;
    }


    public int getId_cd() {
        return id_cd;
    }

    public void setId_cd(int id_cd) {
        this.id_cd = id_cd;
    }

    public String getNom_album() {
        return nom_album;
    }

    public void setNom_album(String nom_album) {
        this.nom_album = nom_album;
    }

    public String getNom_artist() {
        return nom_artist;
    }

    public void setNom_artist(String nom_artist) {
        this.nom_artist = nom_artist;
    }

    public int getId_proprio() {
        return id_proprio;
    }

    public void setId_proprio(int id_proprio) {
        this.id_proprio = id_proprio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public int getQr_code() {
        return qr_code;
    }

    public void setQr_code(int qr_code) {
        this.qr_code = qr_code;
    }
}
