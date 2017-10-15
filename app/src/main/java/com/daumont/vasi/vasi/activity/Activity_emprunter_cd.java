package com.daumont.vasi.vasi.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.daumont.vasi.vasi.Methodes;
import com.daumont.vasi.vasi.R;
import com.daumont.vasi.vasi.database.Table_cd_online;
import com.daumont.vasi.vasi.database.Table_emprunt;
import com.daumont.vasi.vasi.database.Table_user_online;
import com.daumont.vasi.vasi.modele.CD;
import com.daumont.vasi.vasi.modele.Emprunt;
import com.daumont.vasi.vasi.modele.User;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class Activity_emprunter_cd extends AppCompatActivity {

    /**
     * Declaration variables
     */
    //Elements graphiques
    public ListView listView;
    private Toolbar toolbar;
    private Button button_scanner_album;

    //tableaux & lists
    private HashMap<String, String> map_cd;
    private ArrayList<HashMap<String, String>> listItem_cd = new ArrayList<>();
    private ArrayList<CD> list_cd;

    //base de données
    private Table_cd_online table_cd_online;
    private Table_user_online table_user_online;
    private Table_emprunt table_emprunt;
    //autres
    private String string_id_user;
    private Activity activity;
    private String qr_code_album;
    private CD cd_emprunt;

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
        setContentView(R.layout.activity_emprunter_cd);
        listView = (ListView) findViewById(R.id.listView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Sélectionnez un album");
        button_scanner_album = (Button) findViewById(R.id.button_scanner_album);

        //Recuperation parametres
        Bundle objetbunble = this.getIntent().getExtras();
        if (objetbunble != null) {
            string_id_user = objetbunble.getString("id_user");
        }
        if (Methodes.internet_diponible(activity)) {
            //Initialisation bdd
            table_user_online = new Table_user_online(this);
            table_cd_online = new Table_cd_online(this);
            table_emprunt = new Table_emprunt(this);

            //Initialisation variables
            User user = table_user_online.get_user(Integer.parseInt(string_id_user));
            list_cd = table_cd_online.list_cd();


            //Génération de la listView

            for (int i = 0; i < list_cd.size(); i++) {
                if (list_cd.get(i).getId_proprio() != Integer.parseInt(string_id_user)) {
                    map_cd = new HashMap<>();
                    map_cd.put("id", "" + list_cd.get(i).getId_cd());
                    map_cd.put("qr_code", "" + list_cd.get(i).getQr_code());
                    map_cd.put("info", list_cd.get(i).getNom_artist() + "\n" + list_cd.get(i).getNom_album());//champ id
                    map_cd.put("image", list_cd.get(i).getImage());//champ id

                    listItem_cd.add(map_cd);
                }
            }
            if (listItem_cd.size() == 0 || list_cd.size() == 0) {
                map_cd = new HashMap<>();
                map_cd.put("info", "Aucun album disponible");
                map_cd.put("id", "" + (-1));
                map_cd.put("image", "vide");//champ id
                listItem_cd.add(map_cd);
            }


            SimpleAdapter mSchedule = new SimpleAdapter(this.getBaseContext(),
                    listItem_cd, R.layout.layout_cd, new String[]{"info"}, new int[]{R.id.textView_info_cd}) {
                public View getView(int position, View convertView, ViewGroup parent) {

                    HashMap<String, String> map = (HashMap<String, String>) listView
                            .getItemAtPosition(position);
                    final String url_image = map.get("image");
                    View view = super.getView(position, convertView, parent);
                    ImageView image_view_cd = (ImageView) view.findViewById(R.id.image_view_cd);
                    if (!url_image.equals("vide")) {
                        Picasso.with(image_view_cd.getContext()).load(url_image).centerCrop().fit().into(image_view_cd);
                    } else {
                        image_view_cd.setVisibility(View.GONE);
                    }

                    return view;
                }
            };
            listView.setAdapter(mSchedule);

            //Listener sur la listView
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> a, View v, int position,
                                        long id) {
                    HashMap<String, String> map = (HashMap<String, String>) listView
                            .getItemAtPosition(position);
                    if (!map.get("id").equals("-1")) {
                        Intent i = new Intent(Activity_emprunter_cd.this, Activity_emprunter_album_details.class);
                        Bundle objetbunble = new Bundle();
                        objetbunble.putString("id_cd", map.get("id"));
                        objetbunble.putString("id_user", "" + string_id_user);
                        objetbunble.putString("qr_code", "" + map.get("qr_code"));
                        i.putExtras(objetbunble);
                        Activity_emprunter_cd.this.startActivity(i);
                        overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                        finish();
                    }

                }

            });
        }




        //LISTENER
        button_scanner_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Methodes.internet_diponible(activity)) {
                    IntentIntegrator integrator = new IntentIntegrator(activity);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                    integrator.setPrompt("Veuillez scanner l'album.");
                    integrator.setCameraId(0);
                    integrator.setBeepEnabled(false);
                    integrator.setBarcodeImageEnabled(false);
                    integrator.initiateScan();
                }


            }
        });
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
                Toast.makeText(this, "Aucun QRCode trouvé", Toast.LENGTH_LONG).show();
            } else {
                if (Methodes.internet_diponible(activity)) {
                    qr_code_album = result.getContents();
                    Toast.makeText(this, "Numéro album : " + result.getContents(), Toast.LENGTH_LONG).show();
                    cd_emprunt = table_cd_online.get_cd(Integer.parseInt(qr_code_album));
                    if (Integer.parseInt(string_id_user) == (cd_emprunt.getId_proprio())) {
                        info_dialog("Ce cd vous appartient. Vous ne pouvez l'emprunter");
                    } else {
                        if (table_emprunt.album_emprunter(qr_code_album)) {
                            info_dialog("Ce CD est déjà emprunté");
                        } else {
                            confirmation();
                        }
                    }

                }


            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void retour() {
        if (Methodes.internet_diponible(activity)) {
            Intent intent = null;
            User user = table_user_online.get_user(Integer.parseInt(string_id_user));
            if (user.getType().equals("admin")) {
                intent = new Intent(Activity_emprunter_cd.this, Activity_administrateur.class);
            } else {
                intent = new Intent(Activity_emprunter_cd.this, Activity_utilisateur.class);
            }
            Bundle objetbunble = new Bundle();
            objetbunble.putString("id_user", "" + string_id_user);
            intent.putExtras(objetbunble);
            startActivity(intent);
            overridePendingTransition(R.anim.pull_in_return, R.anim.push_out_return);
            finish();
        }

    }

    private void info_dialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_emprunter_cd.this);
        builder.setMessage(message)
                .setPositiveButton("Fermer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        retour();

                    }
                });
        builder.create();
        builder.show();
    }


    public void confirmation() {
        if (Methodes.internet_diponible(activity)) {
            /**ON DEMANDE CONFIRMATION*****************************************/
            CD cd = table_cd_online.get_cd(cd_emprunt.getQr_code());
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_emprunter_cd.this);
            builder.setMessage("Voulez-vous envoyer une demande d'emprunt pour l'album " + cd.getNom_album() + "-" + cd.getNom_artist() + "?")
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            table_emprunt.add_emprunt(new Emprunt(cd_emprunt.getId_proprio(), Integer.parseInt(string_id_user), cd_emprunt.getQr_code(), "demande"));
                            info_dialog("Demande d'emprunt envoyée");
                        }
                    })
                    .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();

                        }
                    });
            builder.create();
            builder.show();
        }

    }

    /**
     * Gestion du bouton retour
     */
    @Override
    public void onBackPressed() {
        retour();
    }
}
