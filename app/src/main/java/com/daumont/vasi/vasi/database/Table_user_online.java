package com.daumont.vasi.vasi.database;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.daumont.vasi.vasi.modele.CD;
import com.daumont.vasi.vasi.modele.User;
import com.parse.GetCallback;
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

public class Table_user_online extends Back4App{


    /**
     * Constructeur
     ********************************************************************************/
    public Table_user_online(Context context) {
      super(context);

    }

    /**
     * Méthodes
     ************************************************************************************/
    public long add_user(User user) {

        ParseObject parse_code = new ParseObject("table_user");
        parse_code.put("id_user", generate_id());
        parse_code.put("identifiant", user.getIdentifiant());
        parse_code.put("mot_de_passe", user.getMot_de_passe());
        parse_code.put("nom", user.getNom());
        parse_code.put("prenom", user.getPrenom());
        parse_code.put("type", user.getType());
        parse_code.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e != null) {
                    Log.d("ONLINE", "add_user()");
                } else {
                    Log.d("ONLINE : ", "ça marche");

                }
            }
        });
        return 0;
    }

    public User get_user(int id_user) {

        ParseQuery query = ParseQuery.getQuery("table_user");
        query.whereEqualTo("id_user", id_user);
        ParseObject parseObject = null;
        User user = null;
        try {
            parseObject = query.getFirst();
            user = new User((int) parseObject.get("id_user"),
                    parseObject.get("nom").toString(),
                    parseObject.get("prenom").toString(),
                    parseObject.get("type").toString(),
                    parseObject.get("identifiant").toString(),
                    parseObject.get("mot_de_passe").toString()
            );
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème pour get_user() : " + e);
        }


        return user;
    }

    public void changer_mot_de_passe(User user) {
        ParseQuery query = ParseQuery.getQuery("table_user");
        query.whereEqualTo("id_user", user.getId_user());
        int retour = -1;
        try {
            ParseObject parseObject = query.getFirst();
            if (parseObject == null) {
                retour = -1;
            } else {
                parseObject.put("mot_de_passe", user.getMot_de_passe());
                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d(getClass().getSimpleName(), "Problème update mdp " + e);
                        } else {
                            Log.d(getClass().getSimpleName(), "update mdp ok" + e);
                        }
                    }
                });
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème changer_mot_de_passe() : " + e);
        }

    }

    //Test si l'identifiant exsite
    public boolean utilisateur_present(String identifiant) {
        ParseQuery query = ParseQuery.getQuery("table_user");
        query.whereEqualTo("identifiant", identifiant);
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
            Log.d("ONLINE", "----------------------------------------------------------------------------------------------------------------Problème connexion_suer() : " + e);
        }
        return etat;

      /*  ParseQuery query = ParseQuery.getQuery("table_user");
        query.whereEqualTo("identifiant", identifiant);
        final boolean[] etat = new boolean[1];
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {

                if (e == null && object != null) {
                    etat[0] = true;
                } else {
                    etat[0] = false;
                }
            }
        });
        return etat[0];*/
    }

    public int connexion_user(String identifiant, String mot_de_passe) {

        ParseQuery query = ParseQuery.getQuery("table_user");
        query.whereEqualTo("identifiant", identifiant);
        query.whereEqualTo("mot_de_passe", mot_de_passe);
        int retour = -1;
        try {
            ParseObject parseObject = query.getFirst();
            if (parseObject == null) {
                retour = -1;
            } else {
                retour = (int) parseObject.get("id_user");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème connexion_suer() : " + e);
        }
        return retour;

    }

    public int generate_id() {
        int qrcode = 0;
        ParseQuery query = ParseQuery.getQuery("table_user");
        List<ParseObject> parseObject = null;
        CD cd = null;
        try {
            parseObject = query.find();

            if (parseObject.size() == 0) {
                qrcode = 100;
            } else {
                qrcode = (int) parseObject.get(parseObject.size() - 1).get("id_user") + 1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return qrcode;
    }

    public ArrayList<User> list_user() {
        ArrayList<User> list_user = new ArrayList<User>();
        ParseQuery query = ParseQuery.getQuery("table_user");
        List<ParseObject> parseObject = null;
        User user = null;
        try {
            parseObject = query.find();

            for (int i = 0; i < parseObject.size(); i++) {
                //int id, String nom, String prenom, int telephone, String adresse, String type, String identifiant, String mot_de_passe)
                user = new User((int) parseObject.get(i).get("id_user"), parseObject.get(i).get("nom").toString(), parseObject.get(i).get("prenom").toString(), parseObject.get(i).get("type").toString(), parseObject.get(i).get("identifiant").toString(), parseObject.get(i).get("mot_de_passe").toString());
                list_user.add(user);


            }


        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème pour list_code_user() : " + e);
        }


        return list_user;
    }


}
