package com.daumont.vasi.vasi.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.daumont.vasi.vasi.Methodes;
import com.daumont.vasi.vasi.R;
import com.daumont.vasi.vasi.database.Table_user_online;
import com.daumont.vasi.vasi.modele.User;

/**
 * PAR JONATHAN DAUMONT
 * Permet d'ajouter des utilisateurs
 */
public class Activity_ajouter_utilisateur extends AppCompatActivity {

    /**
     * Declaration variables
     **/
    //Elements graphiques
    private EditText editText_nom, editText_password_1, editText_password_2, editText_identifiant, editText_prenom;
    private Button button_ajouter_utilisateur;
    private RadioButton radioButton_administrateur, radioButton_classique;
    //base de données
    private Table_user_online table_user_online;
    //autres
    private String type, string_id_user;
    private Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        //Recuperation de l'interface
        setContentView(R.layout.activity_ajouter_utilisateur);
        editText_identifiant = (EditText) findViewById(R.id.editText_identifiant);
        editText_prenom = (EditText) findViewById(R.id.editText_prenom);
        editText_nom = (EditText) findViewById(R.id.editText_nom);
        editText_password_1 = (EditText) findViewById(R.id.editText_password_1);
        editText_password_2 = (EditText) findViewById(R.id.editText_password_2);
        editText_identifiant = (EditText) findViewById(R.id.editText_identifiant);
        button_ajouter_utilisateur = (Button) findViewById(R.id.button_ajouter_utilisateur);
        radioButton_administrateur = (RadioButton) findViewById(R.id.radioButton_administrateur);
        radioButton_classique = (RadioButton) findViewById(R.id.radioButton_classique);
        radioButton_administrateur.setChecked(true);

        //Recuperation parametres
        Bundle objetbunble = this.getIntent().getExtras();
        if (objetbunble != null) {
            string_id_user = objetbunble.getString("id_user");
        }

        //connexion a la base de données
        table_user_online = new Table_user_online(this);

        //LISTENER
        button_ajouter_utilisateur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO TEST SI IDENTIFIANT DEJA UTILISER
                if (radioButton_administrateur.isChecked()) type = "admin";
                else type = "classique";

                if (editText_identifiant.length() < 5) {
                    Methodes.info_dialog("Le nom d'utilisateur ne peut avoir une taille inférieur à 4 caratères",activity);
                } else {
                    if (editText_password_1.getText().toString().equals("")
                            || editText_password_2.getText().toString().equals("")
                            || editText_prenom.getText().toString().equals("")
                            || editText_nom.getText().toString().equals("")
                            && (radioButton_administrateur.isChecked() || radioButton_classique.isChecked())
                            ) {
                        Methodes.info_dialog("Veuillez remplir tous les champs",activity);
                    } else {
                        if (editText_password_1.getText().toString().equals(editText_password_2.getText().toString())) {

                            if (table_user_online.utilisateur_present(editText_identifiant.getText().toString())) {
                                Methodes.info_dialog("Identifiant déjà utilisé",activity);
                            } else {
                                table_user_online.add_user(new User(editText_nom.getText().toString(), editText_prenom.getText().toString(), type, editText_identifiant.getText().toString(), editText_password_1.getText().toString()));
                                ajouter_utilisateur();
                            }
                        } else {
                            Methodes.info_dialog("Mot de passe non identique",activity);
                        }
                    }
                }
            }
        });

        radioButton_administrateur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton_classique.setChecked(false);
                radioButton_administrateur.setChecked(true);
            }
        });
        radioButton_classique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton_classique.setChecked(true);
                radioButton_administrateur.setChecked(false);
            }
        });


    }

    /**
     * Permet d'indiquer à l'utilisateur que l'utilisateur a ete ajoute
     * au bout de deux secondes il est redirigé vers la page
     * principal
     */
    private void ajouter_utilisateur() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_ajouter_utilisateur.this);
        builder.setMessage("Utilisateur ajouté")
                .setPositiveButton("Fermer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(Activity_ajouter_utilisateur.this, Activity_administrateur.class);
                        Bundle objetbunble = new Bundle();
                        objetbunble.putString("id_user", string_id_user);
                        i.putExtras(objetbunble);
                        startActivity(i);
                        Activity_ajouter_utilisateur.this.startActivity(i);
                        overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                        finish();
                    }
                });
        builder.create();
        builder.show();
    }

    /**
     * Gestion touche retour
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_ajouter_utilisateur.this);
        builder.setCancelable(false);
        builder.setMessage("Voulez vous abandonner la création du compte ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Activity_ajouter_utilisateur.this, Activity_administrateur.class);
                        Bundle objetbunble = new Bundle();
                        objetbunble.putString("id_user", string_id_user);
                        intent.putExtras(objetbunble);
                        startActivity(intent);
                        overridePendingTransition(R.anim.pull_in_return, R.anim.push_out_return);
                        finish();
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.create();
        builder.show();
    }


}
