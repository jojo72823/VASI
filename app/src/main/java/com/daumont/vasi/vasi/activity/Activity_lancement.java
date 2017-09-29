package com.daumont.vasi.vasi.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.daumont.vasi.vasi.Methodes;
import com.daumont.vasi.vasi.R;
import com.daumont.vasi.vasi.database.Table_cd_online;
import com.daumont.vasi.vasi.database.Table_emprunt;
import com.daumont.vasi.vasi.database.Table_user_online;
import com.daumont.vasi.vasi.modele.Emprunt;

/**
 * JONATHAN DAUMONT
 * PRESENTE L'APPPLICATION
 * ET
 * VERIFIE SI IL Y A UNE CONNEXION INTERNET DE DISPONIBLE
 */
public class Activity_lancement extends AppCompatActivity {

    //TODO COMMENTAIRE
    //TODO PROPOSITION
    //TODO MODE HORS LIGNE

    /**
     * Atributs
     */
    private Table_user_online table_user_online;
    private Table_cd_online table_cd_online;

    /**
     * Création de l'activité
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lancement);

        //TODO mode en ligne et local

        //On test si il y a une connexion internet de disponible
        if (Methodes.internet_diponible(this)) {
            //réveil de la base de donnéese BACK4APP
            table_user_online = new Table_user_online(this);
            table_cd_online = new Table_cd_online(this);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Activity_lancement.this, ActivityLogin.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                    finish();
                }
            }, 2000);
        } else {


            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_lancement.this);
            builder.setCancelable(false);
            builder.setMessage("Internet n'est pas activé\nVeuillez l'activer.")
                    .setPositiveButton("Fermer", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Activity_lancement.this, ActivityLogin.class);
                            startActivity(intent);
                            finish();
                        }
                    });
            builder.create();
            builder.show();


        }


    }




}
