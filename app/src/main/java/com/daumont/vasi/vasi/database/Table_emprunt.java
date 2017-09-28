package com.daumont.vasi.vasi.database;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.daumont.vasi.vasi.modele.CD;
import com.daumont.vasi.vasi.modele.Emprunt;
import com.daumont.vasi.vasi.modele.User;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Créé par JONATHAN DAUMONT le 29/05/2017.
 */

public class Table_emprunt extends Back4App{

    private Context context;

    /**
     * Constructeur
     ********************************************************************************/
    public Table_emprunt(Context context) {
        super(context);
        this.context = context;

    }
    /**
     * Méthodes
     ************************************************************************************/
    public long add_emprunt(Emprunt emprunt) {
        ParseObject parse_code = new ParseObject("table_emprunt");
        parse_code.put("id_emprunt",generate_id_emprunt());
        parse_code.put("id_proprietaire",emprunt.getId_proprietaire());
        parse_code.put("id_emprunteur",emprunt.getId_emprunteur());
        parse_code.put("qr_code",emprunt.getQr_code());
        parse_code.put("etat_emprunt",emprunt.getEtat_emprunt());

        parse_code.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e != null) {
                    Log.d("ONLINE", "add_cd()");
                }else{
                    Log.d("ONLINE : ","ça marche");

                }
            }
        });
        return 0;
    }

    public int generate_id_emprunt() {
        int qrcode=0;
        ParseQuery query = ParseQuery.getQuery("table_emprunt");
        List<ParseObject> parseObject = null;
        CD cd=null;
        try {
            parseObject = query.find();

            if(parseObject.size()==0){
                qrcode = 100;
            }else{
                qrcode =  (int)parseObject.get(parseObject.size()-1).get("id_emprunt")+1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return qrcode;
    }

    public ArrayList<Emprunt> demande_emprunt(int id_user){
        ArrayList<Emprunt> list_emprunt = new ArrayList<>();
        ParseQuery query = ParseQuery.getQuery("table_emprunt");
        query.whereEqualTo("id_proprietaire", id_user);
        List<ParseObject> parseObject = null;

        try {
            parseObject = query.find();
            for(int i =0; i< parseObject.size();i++){
                Emprunt emprunt = new Emprunt ((int) parseObject.get(i).get("id_emprunt"),
                        (int)parseObject.get(i).get("id_proprietaire"),
                        (int)parseObject.get(i).get("id_emprunteur"),
                        (int)parseObject.get(i).get("qr_code"),
                        parseObject.get(i).get("etat_emprunt").toString()
                );
                if(emprunt.getEtat_emprunt().equals("demande")){
                    list_emprunt.add(emprunt);
                }
            }


        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème pour get_user() : " + e);
        }


        return list_emprunt;
    }

    public ArrayList<CD> list_cd_emprunter(int id_user){
        ArrayList<CD> list_cd_emprunter = new ArrayList<>();
        ParseQuery query = ParseQuery.getQuery("table_emprunt");
        query.whereEqualTo("id_emprunteur", id_user);
        List<ParseObject> parseObject = null;

        try {
            parseObject = query.find();
            for(int i =0; i< parseObject.size();i++){
                Table_cd_online table_cd_online = new Table_cd_online(context);
                list_cd_emprunter.add(table_cd_online.get_cd((int)parseObject.get(i).get("qr_code")));

            }


        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème pour get_user() : " + e);
        }


        return list_cd_emprunter;
    }

    public int demande_emprunt_size(int id_user){
        int compteur=0;
        ParseQuery query = ParseQuery.getQuery("table_emprunt");
        query.whereEqualTo("id_proprietaire", id_user);
        List<ParseObject> parseObject = null;

        try {
            parseObject = query.find();
            for(int i =0; i< parseObject.size();i++){
                Emprunt emprunt = new Emprunt ((int) parseObject.get(i).get("id_emprunt"),
                        (int)parseObject.get(i).get("id_proprietaire"),
                        (int)parseObject.get(i).get("id_emprunteur"),
                        (int)parseObject.get(i).get("qr_code"),
                        parseObject.get(i).get("etat_emprunt").toString()
                );
                if(emprunt.getEtat_emprunt().equals("demande")){
                    compteur++;
                }
            }


        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème pour get_user() : " + e);
        }


        return compteur;
    }

    public Emprunt get_emprunt(int id_emprunt) {

        ParseQuery query = ParseQuery.getQuery("table_emprunt");
        query.whereEqualTo("id_emprunt", id_emprunt);
        ParseObject parseObject = null;
        Emprunt emprunt=null;
        try {
            parseObject = query.getFirst();
            emprunt = new Emprunt ((int) parseObject.get("id_emprunt"),
                    (int)parseObject.get("id_proprietaire"),
                    (int)parseObject.get("id_emprunteur"),
                    (int)parseObject.get("qr_code"),
                    parseObject.get("etat_emprunt").toString()
            );
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème pour get_user() : " + e);
        }


        return emprunt;
    }

    //Test si l'album est déjà emprunté
    public boolean album_emprunter(String qr_code){
        ParseQuery query = ParseQuery.getQuery("table_emprunt");
        query.whereEqualTo("qr_code", Integer.parseInt(qr_code));
        boolean etat=false;
        try {
            ParseObject parseObject = query.getFirst();
            if(parseObject==null){
                etat = false;
            }
            else{
                etat =true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème album_emprunter() : " + e);
        }
        return etat;
    }
    //Test si l'album est déjà emprunté est retourne l'identifiant de l'utilisateur
    public String album_emprunter_identifiant(String qr_code){
        ParseQuery query = ParseQuery.getQuery("table_emprunt");
        query.whereEqualTo("qr_code", Integer.parseInt(qr_code));
        String identifiant=null;

        try {
            ParseObject parseObject = query.getFirst();
            if(parseObject!=null){
                Table_user_online table_user_online = new Table_user_online(context);
                User user = table_user_online.get_user((int)parseObject.get("id_proprietaire"));
                identifiant = user.getIdentifiant();

            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème album_emprunter() : " + e);
        }
        return identifiant;
    }

    public boolean album_terminer_emprunt(String qr_code,String id_user){
        ParseQuery query = ParseQuery.getQuery("table_emprunt");
        query.whereEqualTo("qr_code", Integer.parseInt(qr_code));
        String identifiant=null;

        try {
            ParseObject parseObject = query.getFirst();
            if(parseObject!=null){
                //(int)parseObject.get("id_proprietaire") == Integer.parseInt(id_user)
                if((int)parseObject.get("id_emprunteur") == Integer.parseInt(id_user)){

                    Emprunt emprunt = new Emprunt ((int) parseObject.get("id_emprunt"),
                            (int)parseObject.get("id_proprietaire"),
                            (int)parseObject.get("id_emprunteur"),
                            (int)parseObject.get("qr_code"),
                            parseObject.get("etat_emprunt").toString());

                    emprunt.setEtat_emprunt("rendu");
                    Table_emprunt table_emprunt_tmp = new Table_emprunt(context);
                    table_emprunt_tmp.changer_etat_emprunt(emprunt);

                    return true;

                }else{
                    return false;
                }

            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème album_emprunter() : " + e);
            return false;
        }
        return false;
    }

    //test si l'album a déjà été demandé par l'utilisateur
    public boolean album_emprunter_utilisateur(String qr_code,String id_user){
        ParseQuery query = ParseQuery.getQuery("table_emprunt");
        query.whereEqualTo("qr_code", Integer.parseInt(qr_code));
        query.whereEqualTo("id_emprunteur", Integer.parseInt(id_user));
        boolean etat=false;
        try {
            ParseObject parseObject = query.getFirst();
            if(parseObject==null){
                etat = false;
            }
            else{
                etat =true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème album_emprunter() : " + e);
        }
        return etat;
    }

    public void changer_etat_emprunt(Emprunt emprunt){
        ParseQuery query = ParseQuery.getQuery("table_emprunt");
        query.whereEqualTo("id_emprunt", emprunt.getId_emprunt());
        int retour = -1;
        try {
            ParseObject parseObject = query.getFirst();
            if(parseObject==null){
                retour = -1;
            }
            else{
                parseObject.put("etat_emprunt",emprunt.getEtat_emprunt());
                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d(getClass().getSimpleName(), "Problème changer_etat_emprunt " + e);
                        }else{
                            Log.d(getClass().getSimpleName(), "changer_etat_emprunt ok" + e);
                        }
                    }
                });
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème changer_mot_de_passe() : " + e);
        }

    }

    public boolean delete_emprunt(int id_emprunt){
        ParseQuery query = ParseQuery.getQuery("table_emprunt");
        query.whereEqualTo("id_emprunt", id_emprunt);
        boolean etat=false;
        try {
            ParseObject parseObject = query.getFirst();
            if(parseObject==null){
                etat = false;
            }
            else{
                parseObject.delete();
                etat =true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return etat;
    }


}
