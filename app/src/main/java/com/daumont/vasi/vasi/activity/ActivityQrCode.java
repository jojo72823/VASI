package com.daumont.vasi.vasi.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.daumont.vasi.vasi.Methodes;
import com.daumont.vasi.vasi.R;
import com.daumont.vasi.vasi.database.Table_cd_online;
import com.daumont.vasi.vasi.database.Table_emprunt;
import com.daumont.vasi.vasi.database.Table_user_online;
import com.daumont.vasi.vasi.modele.CD;
import com.daumont.vasi.vasi.modele.User;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Created by Jonathan Daumont on 30/09/2017.
 */

public class ActivityQrCode extends Activity{

    private Activity activity;
    private String qrcode;
    private ProgressDialog mProgressDialog;
    private Table_emprunt table_emprunt;
    private String string_id_user;
    private Table_cd_online table_cd_online;
    private CD cd;
    private boolean etat_changement;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        //Recuperation des elements visuels
        setContentView(R.layout.activity_qr_code);


        if (!Methodes.internet_diponible(activity)) {
            Intent intent = new Intent(activity, Activity_lancement.class);
            startActivity(intent);
            finish();
        } else {

            etat_changement = false;
            table_emprunt = new Table_emprunt(this);
            table_cd_online = new Table_cd_online(this);
            Bundle objetbunble = this.getIntent().getExtras();
            if (objetbunble != null) {
                string_id_user = objetbunble.getString("id_user");
            }
            IntentIntegrator integrator = new IntentIntegrator(activity);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt("Veuillez scanner l'album");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();


        }

    }

    /**
     * Methodes pour le QRCODE
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if (result != null) {
                if (result.getContents() == null) {

                    info_dialog("Aucun QRCode détecté");

                    retour();
                } else {
                    qrcode = result.getContents();
                    if (!Methodes.internet_diponible(activity)) {
                        Intent intent = new Intent(activity, Activity_lancement.class);
                        startActivity(intent);
                        finish();
                    } else {
                        new RendreCd().execute();
                    }

                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }

    }



    private class RendreCd extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!Methodes.internet_diponible(activity)) {

                Intent intent = new Intent(activity, Activity_lancement.class);
                startActivity(intent);
                finish();

            } else {
               mProgressDialog = new ProgressDialog(activity);
                mProgressDialog.setTitle("Veuillez patienter");
                mProgressDialog.setMessage("Connexion en cours...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (Methodes.internet_diponible(activity)) {
                if (table_emprunt.album_terminer_emprunt(qrcode, string_id_user)) {
                    //TODO améliorer le visuel du dialog
                    cd = table_cd_online.get_cd(qrcode);
                    etat_changement = true;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mProgressDialog.hide();

            if(etat_changement){
                if (Methodes.internet_diponible(activity)) {
                    Intent intent = new Intent(activity, Activity_utilisateur.class);
                    //On passe en parametre l'id utilisateur
                    Bundle objetbunble = new Bundle();
                    objetbunble.putString("id_user", "" + string_id_user);
                    objetbunble.putString("notification", "yes");
                    intent.putExtras(objetbunble);
                    startActivity(intent);
                    overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                    finish();
                   // info_dialog("Numéro album : " + qrcode + "\nCD rendu");
                    info_dialog("Veuillez rendre le CD " + cd.getNom_album() + " - " + cd.getNom_artist());
                }
            }else{
                retour();
                info_dialog("Aucune correspondace trouvée");
            }

        }

    }

    public void retour(){
        if (!Methodes.internet_diponible(activity)) {
            Intent intent = new Intent(activity, Activity_lancement.class);
            startActivity(intent);
            finish();
        }else{
            Table_user_online table_user_online = new Table_user_online(this);
            User user = table_user_online.get_user(Integer.parseInt(string_id_user));
            Intent intent = null;
            if(user.getType().equals("admin")){
                intent = new Intent(activity, Activity_administrateur.class);
            }else{
                intent = new Intent(activity, Activity_utilisateur.class);
            }
            Bundle objetbunble = new Bundle();
            objetbunble.putString("id_user", string_id_user);
            intent.putExtras(objetbunble);
            startActivity(intent);
            overridePendingTransition(R.anim.pull_in_return, R.anim.push_out_return);
            finish();
        }

    }

    /**
     * Gestion touche retour
     */
    @Override
    public void onBackPressed() {
        retour();
    }

    private void info_dialog(String message) {
        if (!Methodes.internet_diponible(activity)) {
            Intent intent = new Intent(activity, Activity_lancement.class);
            startActivity(intent);
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityQrCode.this);
            builder.setMessage(message)
                    .setPositiveButton("Fermer", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            builder.create();
            builder.show();
        }

    }

}
