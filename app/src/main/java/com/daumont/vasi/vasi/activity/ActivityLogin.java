package com.daumont.vasi.vasi.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.daumont.vasi.vasi.Methodes;
import com.daumont.vasi.vasi.R;
import com.daumont.vasi.vasi.database.Table_user_online;
import com.daumont.vasi.vasi.modele.User;

/**
 * JONATHAN DAUMONT
 * PERMET DE VERIFIER SI L'IDENTIFIANT EXISTE
 */
public class ActivityLogin extends AppCompatActivity {

    /**
     * ATTRIBUTS
     */
    //elements visuels
    private Button btn_login;
    private EditText editText_login, editText_password;
    private ProgressDialog mProgressDialog;
    //BDD
    private Table_user_online table_user_online;

    //Autres
    private String login, password;
    private int nb_essai_mot_de_passe = 0;
    private int id_user;
    private Activity activity;
    private User user;


    /**
     * Création de l'activité
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        //Recuperation des elements visuels
        setContentView(R.layout.activity_login);
        btn_login = (Button) findViewById(R.id.btn_login);
        editText_login = (EditText) findViewById(R.id.editText_login);
        editText_password = (EditText) findViewById(R.id.editText_password);
        mProgressDialog = new ProgressDialog(this);

        //DATABASE
        table_user_online = new Table_user_online(this);


        //Listener sur bouton
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Methodes.internet_diponible(activity)) {
                    login = editText_login.getText().toString();
                    password = editText_password.getText().toString();
                    new Connexion().execute();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLogin.this);
                    builder.setCancelable(false);
                    builder.setMessage("Internet n'est pas activé\nVeuillez l'activer.")
                            .setPositiveButton("Fermer", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(ActivityLogin.this, Activity_lancement.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    builder.create();
                    builder.show();
                }


            }
        });

        editText_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    login = editText_login.getText().toString();
                    password = editText_password.getText().toString();
                    new Connexion().execute();
                    handled = true;
                }
                return handled;
            }
        });

    }


    private class Connexion extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog.setTitle("Veuillez patienter");
            mProgressDialog.setMessage("Connexion en cours...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {

            id_user = table_user_online.connexion_user(login, password);
            if (id_user != -1) {
                user = table_user_online.get_user(id_user);
            }



            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Recuperation de l'id ou non de l'utilisateur
            if (id_user != -1) {//si on recupere un id pour l'utilisateur
                mProgressDialog.hide();
                if (user.getType().equals("admin")) {//on charge une session admin
                    Intent intent = new Intent(activity, Activity_administrateur.class);
                    Bundle objetbunble = new Bundle();
                    //On passe en parametre l'id utilisateur
                    objetbunble.putString("id_user", "" + id_user);
                    objetbunble.putString("notification", "yes");
                    intent.putExtras(objetbunble);
                    startActivity(intent);
                    overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                    finish();
                } else {//on charge une session classique


                    Intent intent = new Intent(activity, Activity_utilisateur.class);
                    //On passe en parametre l'id utilisateur
                    Bundle objetbunble = new Bundle();
                    objetbunble.putString("id_user", "" + id_user);
                    objetbunble.putString("notification", "yes");
                    intent.putExtras(objetbunble);
                    startActivity(intent);
                    overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                    finish();
                }
            } else {//n'exisste pas on incremente le nombre d'essai
                mProgressDialog.hide();
                    Methodes.info_dialog("Le compte n'existe pas ou le mot de passe est incorrect", activity);



            }
        }
    }

}
