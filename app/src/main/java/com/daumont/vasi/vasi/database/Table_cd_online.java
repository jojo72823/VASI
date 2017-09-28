package com.daumont.vasi.vasi.database;

import android.content.Context;
import android.util.Log;

import com.daumont.vasi.vasi.modele.CD;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Créé par JONATHAN DAUMONT le 29/05/2017.
 */

public class Table_cd_online extends Back4App {


    /**
     * Constructeur
     ********************************************************************************/
    public Table_cd_online(Context context) {
        super(context);
    }

    /**
     * Méthodes
     ************************************************************************************/
    public long add_cd(CD cd) {
        ParseObject parse_code = new ParseObject("table_cd");
        parse_code.put("id_cd", cd.getId_cd());
        parse_code.put("nom_album", cd.getNom_album());
        parse_code.put("nom_artist", cd.getNom_artist());
        parse_code.put("id_proprio", cd.getId_proprio());
        parse_code.put("image", cd.getImage());
        parse_code.put("qr_code", this.get_qrcode());

        parse_code.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e != null) {
                    Log.d("ONLINE", "add_cd()");
                } else {
                    Log.d("ONLINE : ", "ça marche");

                }
            }
        });
        return 0;
    }

    public CD get_cd(String id_cd) {

        ParseQuery query = ParseQuery.getQuery("table_cd");
        query.whereEqualTo("id_cd", Integer.parseInt(id_cd));
        CD cd = null;
        try {
            ParseObject parseObject = query.getFirst();
            //String nom_album, String nom_artist, String id_proprio)

            cd = new CD(
                    (int) parseObject.get("id_cd"),
                    parseObject.get("nom_album").toString(),
                    parseObject.get("nom_artist").toString(),
                    (int) parseObject.get("id_proprio"),
                    parseObject.get("image").toString(),
                    (int) parseObject.get("qr_code")
            );
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème pour get_cd() : " + e);
        }


        return cd;
    }

    public CD get_cd(int qr_code) {

        ParseQuery query = ParseQuery.getQuery("table_cd");
        query.whereEqualTo("qr_code", qr_code);
        CD cd = null;
        try {
            ParseObject parseObject = query.getFirst();
            //String nom_album, String nom_artist, String id_proprio)

            cd = new CD(
                    (int) parseObject.get("id_cd"),
                    parseObject.get("nom_album").toString(),
                    parseObject.get("nom_artist").toString(),
                    (int) parseObject.get("id_proprio"),
                    parseObject.get("image").toString(),
                    (int) parseObject.get("qr_code")
            );
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème pour get_cd() : " + e);
        }


        return cd;
    }

    public boolean check_cd(int id_cd) {

        ParseQuery query = ParseQuery.getQuery("table_cd");
        query.whereEqualTo("id_cd", id_cd);
        boolean etat = false;
        try {
            ParseObject parseObject = query.getFirst();
            if (parseObject == null) {
                etat = false;
            } else {
                etat = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return etat;
    }


//TODO findInBackground

    public ArrayList<CD> list_cd_utilistaeur(int id_user) {
        ArrayList<CD> list_cd = new ArrayList<CD>();
        ParseQuery query = ParseQuery.getQuery("table_cd");
        query.whereEqualTo("id_proprio", id_user);
        List<ParseObject> parseObject = null;
        CD cd = null;
        try {
            parseObject = query.find();

            for (int i = 0; i < parseObject.size(); i++) {

                cd = new CD(
                        (int) parseObject.get(i).get("id_cd"),
                        parseObject.get(i).get("nom_album").toString(),
                        parseObject.get(i).get("nom_artist").toString(),
                        (int) parseObject.get(i).get("id_proprio"),
                        parseObject.get(i).get("image").toString(),
                        (int) parseObject.get(i).get("qr_code")
                );
                list_cd.add(cd);


            }


        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème pour list_cd() : " + e);
        }


        return list_cd;
    }

    public ArrayList<CD> list_cd() {
        ArrayList<CD> list_cd = new ArrayList<CD>();
        ParseQuery query = ParseQuery.getQuery("table_cd");
        List<ParseObject> parseObject = null;
        CD cd = null;
        try {
            parseObject = query.find();

            for (int i = 0; i < parseObject.size(); i++) {

                cd = new CD(
                        (int) parseObject.get(i).get("id_cd"),
                        parseObject.get(i).get("nom_album").toString(),
                        parseObject.get(i).get("nom_artist").toString(),
                        (int) parseObject.get(i).get("id_proprio"),
                        parseObject.get(i).get("image").toString(),
                        (int) parseObject.get(i).get("qr_code")
                );
                list_cd.add(cd);


            }


        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème pour list_cd() : " + e);
        }


        return list_cd;
    }

    public int get_qrcode() {
        int qrcode = 0;
        ParseQuery query = ParseQuery.getQuery("table_cd");
        List<ParseObject> parseObject = null;
        CD cd = null;
        try {
            parseObject = query.find();

            if (parseObject.size() == 0) {
                qrcode = 100;
            } else {
                qrcode = (int) parseObject.get(parseObject.size() - 1).get("qr_code") + 1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return qrcode;
    }

    public boolean delete_cd(int id_cd) {
        ParseQuery query = ParseQuery.getQuery("table_cd");
        query.whereEqualTo("id_cd", id_cd);
        boolean etat = false;
        try {
            ParseObject parseObject = query.getFirst();
            if (parseObject == null) {
                etat = false;
            } else {
                parseObject.delete();
                etat = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return etat;
    }


}
