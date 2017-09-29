package com.daumont.vasi.vasi.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daumont.vasi.vasi.R;
import com.daumont.vasi.vasi.database.Table_cd_online;
import com.daumont.vasi.vasi.database.Table_user_online;
import com.daumont.vasi.vasi.modele.Emprunt;
import com.daumont.vasi.vasi.modele.User;

public class Activity_modifier_profil extends AppCompatActivity {

    private TextView editText_ancien_mdp,editText_password_1,editText_password_2;
    private Button button_sauvegarder_profil;
    //bdd
    private Table_user_online table_user_online;

    //autres
    private String string_id_user;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Récupération des élements graphiques
        setContentView(R.layout.activity_modifier_profil);
        editText_ancien_mdp  =(EditText)findViewById(R.id.editText_ancien_mdp);
        editText_password_1  =(EditText)findViewById(R.id.editText_password_1);
        editText_password_2  =(EditText)findViewById(R.id.editText_password_2);
        button_sauvegarder_profil  =(Button)findViewById(R.id.button_sauvegarder_profil);

        //Recuperation parametres
        Bundle objetbunble = this.getIntent().getExtras();
        if (objetbunble != null) {
            string_id_user = objetbunble.getString("id_user");
        }

        //Initialisation bdd
        table_user_online = new Table_user_online(this);

        //Initialisation variables
        user =  table_user_online.get_user(Integer.parseInt(string_id_user));



        button_sauvegarder_profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_password_1.length() < 4) {
                    Toast.makeText(Activity_modifier_profil.this, "Le mot de passe ne peut avoir une taille inférieur à 4 caratères", Toast.LENGTH_SHORT).show();
                }else{

                    if (!editText_password_1.getText().toString().equals(editText_password_2.getText().toString())) {
                        Toast.makeText(Activity_modifier_profil.this, "Mot de passe non identique", Toast.LENGTH_SHORT).show();
                    } else {
                        if(editText_ancien_mdp.getText().toString().equals(user.getMot_de_passe())){
                            user.setMot_de_passe(editText_password_1.getText().toString());
                            table_user_online.changer_mot_de_passe(user);
                            info_dialog("Nouveau mot de passe sauvegardé");
                        }else{
                            Toast.makeText(Activity_modifier_profil.this, "L'ancien mot de passe est incorrect", Toast.LENGTH_SHORT).show();
                        }



                    }
                }
            }
        });
    }

    /**
     * Gestion du bouton retour
     */
    @Override
    public void onBackPressed() {
        retour();
    }

    public void retour() {
        /**ON DEMANDE CONFIRMATION*****************************************/
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_modifier_profil.this);
        builder.setMessage("Toute modification ne sera pas sauvegardée. Voulez-vous continuer ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = null;
                        if(user.getType().equals("admin")){
                            intent = new Intent(Activity_modifier_profil.this, Activity_administrateur.class);
                        }else{
                            intent = new Intent(Activity_modifier_profil.this, Activity_utilisateur.class);
                        }
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

    public void info_dialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_modifier_profil.this);
        builder.setCancelable(false);
        builder.setMessage(message)
                .setPositiveButton("Retour au menu principal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent intent = null;
                        if(user.getType().equals("admin")){
                            intent = new Intent(Activity_modifier_profil.this, Activity_administrateur.class);
                        }else{
                            intent = new Intent(Activity_modifier_profil.this, Activity_utilisateur.class);
                        }
                        Bundle objetbunble = new Bundle();
                        objetbunble.putString("id_user", string_id_user);
                        intent.putExtras(objetbunble);
                        startActivity(intent);
                        overridePendingTransition(R.anim.pull_in_return, R.anim.push_out_return);
                        finish();
                    }
                });
        builder.create();
        builder.show();


    }
}
