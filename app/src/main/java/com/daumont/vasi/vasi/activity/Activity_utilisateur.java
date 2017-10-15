package com.daumont.vasi.vasi.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * JONATHAN DAUMONT
 * ACTIVITER POUR UN UTILISATEUR CLASSQIEU
 */
public class Activity_utilisateur extends AppCompatActivity {

    /**
     * ATTRIBUTS
     */
    //elements visuels
    private ListView listView, listView_mes_cd;
    private LinearLayout linearLayout_menu;
    private Toolbar toolbar;
    private FloatingActionButton fab_user;
    private Button button_emprunter_un_cd, button_rendre_cd, button_ajouter_cd, button_voir_demande_emprunt;
    private ProgressDialog mProgressDialog;
    private AlertDialog alertDialog;
    //listes et tableaux
    private HashMap<String, String> map_cd_user, map_cd_theque, map_demande_emprunt;
    private ArrayList<HashMap<String, String>> listItem_cd_theque, listItem_demande_emprunt, listItem_cd_user;
    private ArrayList<CD> list_cd_theque, list_cd_utilisateur;
    private ArrayList<Emprunt> list_demande_emprunt;
    //bdd
    private Table_cd_online table_cd_online;
    private Table_user_online table_user_online;
    private Table_emprunt table_emprunt;
    //autres
    private boolean position_infos;
    private User user;
    private String string_id_user;
    private Activity activity;
    private String etat_notif;
    private int position_vue;
    private CD cd;
    private boolean etat_changement;
    private String qrcode;


    /**
     * Listener pour le menu situé en bas de page
     */

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_profil:
                    listView.setAdapter(null);
                    position_infos = true;
                    fab_user.setImageResource(R.drawable.alarm);
                    load_navigation_menu();
                    return true;

                case R.id.navigation_cd_empruntes:
                    listView.setAdapter(null);
                    position_infos = false;
                    fab_user.setImageResource(R.drawable.icon_disk_add);
                    load_navigation_cd_empruntesr();
                    return true;
                case R.id.navigation_cd_theque:
                    listView.setAdapter(null);
                    position_infos = false;
                    fab_user.setImageResource(R.drawable.icon_disk_add);
                    load_navigation_cd_theque();
                    return true;
            }
            return false;
        }

    };

    /**
     * Ininitalisation du menu situé en haut à droite
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profil, menu);
        return true;
    }

    /**
     * Listener sur le menu situé en haut à droite
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_modifier_mot_de_passe:
                // TODO Something when menu item selected

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
        setContentView(R.layout.activity_utilisateur);
        listView = (ListView) findViewById(R.id.listView_user);
        listView.setNestedScrollingEnabled(true);//Pour activer le défilement vertical
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        listView_mes_cd = (ListView) findViewById(R.id.listView_user);
        linearLayout_menu = (LinearLayout) findViewById(R.id.linearLayout_menu);
        fab_user = (FloatingActionButton) findViewById(R.id.fab_user);
        button_emprunter_un_cd = (Button) findViewById(R.id.button_emprunter_un_cd);
        button_rendre_cd = (Button) findViewById(R.id.button_rendre_cd);
        button_ajouter_cd = (Button) findViewById(R.id.button_ajouter_cd);
        button_voir_demande_emprunt = (Button) findViewById(R.id.button_voir_demande_emprunt);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        linearLayout_menu.setNestedScrollingEnabled(true);
        etat_changement = false;
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setTitle("Veuillez patienter");
        mProgressDialog.setMessage("Connexion en cours...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(false);


        //Recuperation parametres
        Bundle objetbunble = this.getIntent().getExtras();
        if (objetbunble != null) {
            string_id_user = objetbunble.getString("id_user");
            if (objetbunble.getString("notification") != null) {
                etat_notif = objetbunble.getString("notification");
            } else {
                etat_notif = "rien";
            }
        }

        if (Methodes.internet_diponible(activity)) {
            //Connexion bdd
            table_user_online = new Table_user_online(this);
            table_cd_online = new Table_cd_online(this);
            table_emprunt = new Table_emprunt(this);

            //Initialisation variables
            position_infos = true;
            toolbar.inflateMenu(R.menu.menu_profil);
            list_demande_emprunt = new ArrayList<>();
            listItem_cd_theque = new ArrayList<>();
            listItem_demande_emprunt = new ArrayList<>();
            listItem_cd_user = new ArrayList<>();


            //Appel du chargement
            new Chargement().execute();
        }


        // Listeners
        fab_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog.dismiss();
                if (Methodes.internet_diponible(activity)) {
                    if (position_infos) {
                        affichage_demande_emprunt();
                    } else {

                        Intent i = new Intent(Activity_utilisateur.this, Activity_rechercher_artiste.class);
                        Bundle objetbunble = new Bundle();
                        objetbunble.putString("id_user", string_id_user);
                        i.putExtras(objetbunble);
                        Activity_utilisateur.this.startActivity(i);
                        overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                    }
                }

            }
        });
        button_emprunter_un_cd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.dismiss();
                if (Methodes.internet_diponible(activity)) {
                    Intent i = new Intent(Activity_utilisateur.this, Activity_emprunter_cd.class);
                    Bundle objetbunble = new Bundle();
                    objetbunble.putString("id_user", string_id_user);
                    i.putExtras(objetbunble);
                    Activity_utilisateur.this.startActivity(i);
                    overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                    finish();
                }

            }
        });


        button_rendre_cd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.dismiss();
                if (Methodes.internet_diponible(activity)) {
                    IntentIntegrator integrator = new IntentIntegrator(activity);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                    integrator.setPrompt("Veuillez scanner l'album");
                    integrator.setCameraId(0);
                    integrator.setBeepEnabled(false);
                    integrator.setBarcodeImageEnabled(false);
                    integrator.initiateScan();
                }

            }
        });
        button_ajouter_cd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.dismiss();
                if (Methodes.internet_diponible(activity)) {
                    Intent i = new Intent(Activity_utilisateur.this, Activity_rechercher_artiste.class);
                    Bundle objetbunble = new Bundle();
                    objetbunble.putString("id_user", string_id_user);
                    i.putExtras(objetbunble);
                    Activity_utilisateur.this.startActivity(i);
                    overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                }

            }
        });

        button_voir_demande_emprunt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                affichage_demande_emprunt();
            }
        });

        toolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.action_modifier_mot_de_passe:
                            mProgressDialog.dismiss();
                            if (Methodes.internet_diponible(activity)) {
                                Intent i = new Intent(Activity_utilisateur.this, Activity_modifier_profil.class);
                                Bundle objetbunble = new Bundle();
                                objetbunble.putString("id_user", string_id_user);
                                i.putExtras(objetbunble);
                                Activity_utilisateur.this.startActivity(i);
                                overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                            }
                            return true;

                        case R.id.action_a_propos:
                            LayoutInflater factory = LayoutInflater.from(activity);
                            final View alertDialogView = factory.inflate(R.layout.dialog_a_propos, null);
                            AlertDialog.Builder adb = new AlertDialog.Builder(activity);

                            //GET INTERFACE
                            Button button_close = (Button) alertDialogView.findViewById(R.id.button_close);

                            adb.setView(alertDialogView);
                            final AlertDialog alertDialog = adb.show();

                            button_close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                }
                            });

                            return true;





                        default:
                            return false;
                    }


            }
        });


        //Listener sur la listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> a, View v, int position,
                                    long id) {
                mProgressDialog.dismiss();
                if (Methodes.internet_diponible(activity)) {
                    HashMap<String, String> map = (HashMap<String, String>) listView
                            .getItemAtPosition(position);
                    if (!map.get("id").equals("null")) {
                        if (position_vue == 1 || position_vue == 2) {
                            Intent i = new Intent(Activity_utilisateur.this, Activity_details_cd.class);
                            Bundle objetbunble = new Bundle();
                            objetbunble.putString("id_cd", map.get("id"));
                            objetbunble.putString("qr_code", map.get("qr_code"));
                            objetbunble.putString("id_user", "" + string_id_user);

                            i.putExtras(objetbunble);
                            startActivity(i);
                            overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                            finish();
                        }
                    }
                }


            }

        });


    }

    /**
     * Charge les informations sur le profil utilisateur
     */
    public void load_navigation_menu() {
        listView.setVisibility(View.GONE);
        linearLayout_menu.setVisibility(View.VISIBLE);
        position_vue = 0;

    }

    /**
     * Charge la list des CD emprunter
     */
    public void load_navigation_cd_empruntesr() {
        position_vue = 1;
        listView.setVisibility(View.VISIBLE);
        linearLayout_menu.setVisibility(View.GONE);

        SimpleAdapter mSchedule = new SimpleAdapter(this.getBaseContext(),
                listItem_cd_user, R.layout.layout_cd, new String[]{"info"}, new int[]{R.id.textView_info_cd}) {
            public View getView(int position, View convertView, ViewGroup parent) {

                HashMap<String, String> map = (HashMap<String, String>) listView
                        .getItemAtPosition(position);
                final String url_image = map.get("image");

                View view = super.getView(position, convertView, parent);
                ImageView image_view_cd = (ImageView) view.findViewById(R.id.image_view_cd);
                Picasso.with(image_view_cd.getContext()).load(url_image).centerCrop().fit().into(image_view_cd);
                return view;
            }
        };
        listView.setAdapter(mSchedule);
    }


    /**
     * Charge la list de la CDTheque
     */
    public void load_navigation_cd_theque() {
        position_vue = 2;
        listView.setVisibility(View.VISIBLE);
        linearLayout_menu.setVisibility(View.GONE);

        SimpleAdapter mSchedule = new SimpleAdapter(this.getBaseContext(),
                listItem_cd_theque, R.layout.layout_cd, new String[]{"info"}, new int[]{R.id.textView_info_cd}) {
            public View getView(int position, View convertView, ViewGroup parent) {

                HashMap<String, String> map = (HashMap<String, String>) listView
                        .getItemAtPosition(position);
                final String url_image = map.get("image");

                View view = super.getView(position, convertView, parent);
                ImageView image_view_cd = (ImageView) view.findViewById(R.id.image_view_cd);
                Picasso.with(image_view_cd.getContext()).load(url_image).centerCrop().fit().into(image_view_cd);
                return view;
            }
        };
        listView.setAdapter(mSchedule);
    }


    /**
     * Gestion du bouton retour
     */
    @Override
    public void onBackPressed() {
        retour();
    }

    public void retour() {
        mProgressDialog.dismiss();
        if (Methodes.internet_diponible(activity)) {
            /**ON DEMANDE CONFIRMATION*****************************************/
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_utilisateur.this);
            builder.setMessage("Vous allez être déconnecté. Voulez-vous continuer ?")
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            Intent intent = new Intent(Activity_utilisateur.this, ActivityLogin.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.pull_in_return, R.anim.push_out_return);
                            finish();
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

                if (Methodes.internet_diponible(activity)) {
                    new RendreCd().execute();
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void affichage_demande_emprunt() {
        if (Methodes.internet_diponible(activity)) {
            int taille = table_emprunt.demande_emprunt_size(Integer.parseInt(string_id_user));

            if (list_demande_emprunt.size() != taille) {
                //On vide le contenu
                list_demande_emprunt.clear();
                listItem_demande_emprunt.clear();
                //on va mettre à jour la list de demande d'emprunt
                list_demande_emprunt = table_emprunt.demande_emprunt(Integer.parseInt(string_id_user));
                for (int i = 0; i < list_demande_emprunt.size(); i++) {
                    map_demande_emprunt = new HashMap<>();
                    map_demande_emprunt.put("id", "" + list_demande_emprunt.get(i).getId_emprunt());
                    User user_demandeur = table_user_online.get_user(list_demande_emprunt.get(i).getId_emprunteur());
                    CD cd_tmp = table_cd_online.get_cd(list_demande_emprunt.get(i).getQr_code());
                    map_demande_emprunt.put("titre", cd_tmp.getNom_artist() + " - " + cd_tmp.getNom_album());
                    map_demande_emprunt.put("demandeur", "Demande envoyée par " + user_demandeur.getIdentifiant());
                    map_demande_emprunt.put("image", cd_tmp.getImage());
                    listItem_demande_emprunt.add(map_demande_emprunt);
                }
            }

            if (list_demande_emprunt.size() == 0) {
                info_dialog("Aucune demande");
            } else {
                LayoutInflater factory = LayoutInflater.from(Activity_utilisateur.this);
                final View alertDialogView = factory.inflate(R.layout.dialog_demande_emprunt, null);
                AlertDialog.Builder adb = new AlertDialog.Builder(Activity_utilisateur.this);
                Button button_close = (Button) alertDialogView.findViewById(R.id.button_close);
                final ListView listView_emprunt = (ListView) alertDialogView.findViewById(R.id.listView_emprunt);
                adb.setView(alertDialogView);
                listView_emprunt.setAdapter(null);
                SimpleAdapter mSchedule = new SimpleAdapter(this.getBaseContext(),
                        listItem_demande_emprunt, R.layout.layout_cd_demande_emprunt, new String[]{"titre"}, new int[]{R.id.textView_info_cd}) {
                    public View getView(int position, View convertView, ViewGroup parent) {

                        HashMap<String, String> map = (HashMap<String, String>) listView_emprunt
                                .getItemAtPosition(position);
                        final String url_image = map.get("image");

                        View view = super.getView(position, convertView, parent);
                        ImageView image_view_cd = (ImageView) view.findViewById(R.id.image_view_cd);
                        TextView textView_demandeur = (TextView) view.findViewById(R.id.textView_demandeur);
                        textView_demandeur.setText(map.get("demandeur"));
                        Picasso.with(image_view_cd.getContext()).load(url_image).centerCrop().fit().into(image_view_cd);
                        return view;
                    }
                };
                listView_emprunt.setAdapter(mSchedule);

                listView_emprunt.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> a, View v, int position,
                                            long id) {
                        HashMap<String, String> map = (HashMap<String, String>) listView_emprunt
                                .getItemAtPosition(position);

                        String id_emprunt = map.get("id");
                        confirmation(id_emprunt, alertDialog);

                    }

                });


                TextView textTitre;
                textTitre = (TextView) alertDialogView.findViewById(R.id.textTitre);
                textTitre.setText("Demande de prêt");
                alertDialog = adb.show();
                button_close.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }

        }


    }

    public void confirmation(final String p_id_emprunt, final AlertDialog alertDialog) {
        if (Methodes.internet_diponible(activity)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(Activity_utilisateur.this);
            builder.setMessage("Voulez vous validez l'emprunt ?")
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            Emprunt emprunt_tmp = table_emprunt.get_emprunt(Integer.parseInt(p_id_emprunt));
                            emprunt_tmp.setEtat_emprunt("preter");
                            table_emprunt.changer_etat_emprunt(emprunt_tmp);

                            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_utilisateur.this);
                            builder.setMessage("Emprunt accepté.\nN'oubliez pas de donner le CD.")
                                    .setPositiveButton("Fermer", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            alertDialog.dismiss();
                                        }
                                    });
                            builder.create();
                            builder.show();


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

    private void info_dialog(String message) {
        if (Methodes.internet_diponible(activity)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_utilisateur.this);
            builder.setCancelable(false);
            builder.setMessage(message)
                    .setPositiveButton("Fermer", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            builder.create();
            builder.show();
        }

    }

    private class Chargement extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (Methodes.internet_diponible(activity)) {
                mProgressDialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (Methodes.internet_diponible(activity)) {
                user = table_user_online.get_user(Integer.parseInt(string_id_user));
                list_cd_theque = table_cd_online.list_cd();
                list_cd_utilisateur = table_cd_online.list_cd_utilistaeur(Integer.parseInt(string_id_user));
                list_demande_emprunt = table_emprunt.demande_emprunt(Integer.parseInt(string_id_user));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (Methodes.internet_diponible(activity)) {
                mProgressDialog.hide();
                toolbar.setTitle("Bienvenue " + user.getIdentifiant());
                //Initialisation des listes
                if (list_cd_theque.size() > 0) {
                    for (int i = 0; i < list_cd_theque.size(); i++) {
                        map_cd_theque = new HashMap<>();
                        map_cd_theque.put("id", "" + list_cd_theque.get(i).getId_cd());
                        map_cd_theque.put("info", list_cd_theque.get(i).getNom_artist() + " - " + list_cd_theque.get(i).getNom_album());//champ id
                        map_cd_theque.put("image", list_cd_theque.get(i).getImage());//champ id
                        map_cd_theque.put("qr_code", "" + list_cd_theque.get(i).getQr_code());
                        listItem_cd_theque.add(map_cd_theque);
                    }
                }else {
                    map_cd_theque = new HashMap<>();
                    map_cd_theque.put("id", "null");
                    map_cd_theque.put("info", "Il n'y a aucun CD");//champ id
                    listItem_cd_theque.add(map_cd_theque);
                }


                if (list_cd_utilisateur.size() > 0) {
                    for (int i = 0; i < list_cd_utilisateur.size(); i++) {
                        map_cd_user = new HashMap<>();
                        map_cd_user.put("id", "" + list_cd_utilisateur.get(i).getId_cd());
                        map_cd_user.put("info", list_cd_utilisateur.get(i).getNom_artist() + " - " + list_cd_utilisateur.get(i).getNom_album());//champ id
                        map_cd_user.put("image", list_cd_utilisateur.get(i).getImage());//champ id
                        map_cd_user.put("qr_code", "" + list_cd_utilisateur.get(i).getQr_code());
                        listItem_cd_user.add(map_cd_user);
                    }
                }else {
                    map_cd_user = new HashMap<>();
                    map_cd_user.put("id", "null");
                    map_cd_user.put("info", "Il n'y a aucun CD");//champ id
                    listItem_cd_user.add(map_cd_user);
                }

                for (int i = 0; i < list_demande_emprunt.size(); i++) {
                    map_demande_emprunt = new HashMap<>();
                    map_demande_emprunt.put("id", "" + list_demande_emprunt.get(i).getId_emprunt());
                    User user_demandeur = table_user_online.get_user(list_demande_emprunt.get(i).getId_emprunteur());
                    CD cd_tmp = table_cd_online.get_cd(list_demande_emprunt.get(i).getQr_code());
                    map_demande_emprunt.put("titre", cd_tmp.getNom_artist() + " - " + cd_tmp.getNom_album());
                    map_demande_emprunt.put("demandeur", "Demande par " + user_demandeur.getIdentifiant());
                    map_demande_emprunt.put("image", cd_tmp.getImage());
                    listItem_demande_emprunt.add(map_demande_emprunt);
                }

                load_navigation_menu();//Chargement de la listView

                if (!etat_notif.equals("rien")) {

                    if (list_demande_emprunt.size() != 0) {
                        affichage_demande_emprunt();
                    }

                }
            }
        }

    }

    private class RendreCd extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (Methodes.internet_diponible(activity)) {

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
                    info_dialog("Veuillez rendre le CD \n" + cd.getNom_album() + " - " + cd.getNom_artist());
                }
            }else{
                retour();
                info_dialog("Aucune correspondace trouvée");
            }

        }

    }


}
