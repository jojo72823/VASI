package com.daumont.vasi.vasi.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.daumont.vasi.vasi.modele.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * JONATHAN DAUMONT
 * Permet de visualiser les détails utilisateur
 * SES INFOS
 * SES CD
 * LES CD EMRPUNTES
 */
public class Activity_details_utilisateur extends AppCompatActivity {

    /**
     * Declaration variables
     */
    //Elements graphiques
    private View view_content;
    private ListView listView_details;
    private View view;
    private LinearLayout linearLayout_info;
    private TextView textView_nom, textView_prenom;
    private CollapsingToolbarLayout toolbar_layout;
    private FloatingActionButton fab;

    //tableaux & lists
    private HashMap<String, String> map_ses_cd, map_cd_emprunter;
    private ArrayList<HashMap<String, String>> listItem_cd_emprunter = new ArrayList<>();
    private ArrayList<HashMap<String, String>> listItem_ses_cd = new ArrayList<>();
    private ArrayList<CD> list_ses_cd;
    private ArrayList<CD> list_cd_emprunter;

    //Base de données
    private Table_cd_online table_cd_online;
    private Table_user_online table_user_online;
    private Table_emprunt table_emprunt;

    //Autres
    private User user;
    private int id_user;
    private String string_id_user, string_id_user_select;
    private boolean add_cd = false;
    private Activity activity;

    /**
     * Listener sur le menu onglet
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            BottomNavigationView navigation;
            switch (item.getItemId()) {
                case R.id.navigation_info_utilisteur:
                    listView_details.setAdapter(null);
                    load_info_utilisateur();
                    add_cd = false;
                    return true;
                case R.id.navigation_list_ses_cd:
                    listView_details.setAdapter(null);
                    load_ses_cd();
                    add_cd = true;
                    return true;
                case R.id.navigation_list_cd_emprunter:
                    listView_details.setAdapter(null);
                    load_cd_emprunter();
                    add_cd = true;
                    return true;
            }
            return false;
        }

    };

    /**
     * Créer le menu en haut à droite
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_suppression, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        //Recuperation des elements visuels
        setContentView(R.layout.activity_details_utilisateur);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        view_content = findViewById(R.id.content);
        listView_details = (ListView) findViewById(R.id.listView_details);
        toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_admin);
        linearLayout_info = (LinearLayout) findViewById(R.id.linearLayout_info);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        textView_nom = (TextView) findViewById(R.id.textView_nom);
        textView_prenom = (TextView) findViewById(R.id.textView_prenom);
        toolbar.inflateMenu(R.menu.menu_suppression);

        //Récupération des paramètres
        Bundle objetbunble = this.getIntent().getExtras();
        if (objetbunble != null) {
            string_id_user_select = objetbunble.getString("id_user_select");
            string_id_user = objetbunble.getString("id_user");
        }

        if (!Methodes.internet_diponible(activity)) {
            Intent intent = new Intent(activity, Activity_lancement.class);
            startActivity(intent);
            finish();
        }else{
            //Initilisation bdd
            table_user_online = new Table_user_online(this);
            table_cd_online = new Table_cd_online(this);
            table_emprunt = new Table_emprunt(this);
            //Initialisation variables
            listView_details.setNestedScrollingEnabled(true);
            linearLayout_info.setNestedScrollingEnabled(true);
            list_ses_cd = new ArrayList<>();
            list_cd_emprunter = new ArrayList<>();
            User user = table_user_online.get_user(Integer.parseInt(string_id_user_select));
            toolbar_layout.setTitle("Profil de " + user.getIdentifiant());
            list_ses_cd = table_cd_online.list_cd_utilistaeur(Integer.parseInt(string_id_user_select));
            textView_nom.setText("Nom : " + user.getNom());
            textView_prenom.setText("Prénom : " + user.getPrenom());
            list_cd_emprunter = table_emprunt.list_cd_emprunter(Integer.parseInt(string_id_user));

            for (int i = 0; i < list_ses_cd.size(); i++) {
                map_ses_cd = new HashMap<>();
                map_ses_cd.put("id_cd", "" + list_ses_cd.get(i).getId_cd());
                map_ses_cd.put("info", list_ses_cd.get(i).getNom_album() + " " + list_ses_cd.get(i).getNom_artist());//champ id
                map_ses_cd.put("image", "" + list_ses_cd.get(i).getImage());

                listItem_ses_cd.add(map_ses_cd);
            }

            for (int i = 0; i < list_cd_emprunter.size(); i++) {
                map_cd_emprunter = new HashMap<>();
                map_cd_emprunter.put("id_cd", "" + list_cd_emprunter.get(i).getId_cd());
                map_cd_emprunter.put("info", list_cd_emprunter.get(i).getNom_album() + " " + list_cd_emprunter.get(i).getNom_artist());//champ id
                map_cd_emprunter.put("image", "" + list_cd_emprunter.get(i).getImage());
                listItem_cd_emprunter.add(map_cd_emprunter);
            }

            load_info_utilisateur();
        }

        toolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_suppression:
                        if (!Methodes.internet_diponible(activity)) {
                            Intent intent = new Intent(activity, Activity_lancement.class);
                            startActivity(intent);
                            finish();
                        }else{
                            /**ON DEMANDE CONFIRMATION*****************************************/
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setCancelable(false);
                            builder.setMessage("Etes-vous sûr de vouloir supprimer l'utilisateur")
                                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            if(string_id_user.equals(string_id_user_select)){
                                                Toast.makeText(activity, "Vous ne pouvez supprimer votre compte utilisateur", Toast.LENGTH_LONG).show();
                                            }else{
                                                Toast.makeText(activity, "Suppression de l'utilisateur", Toast.LENGTH_SHORT).show();
                                                table_user_online.delete_user(Integer.parseInt(string_id_user_select));
                                                retour();
                                            }


                                        }
                                    })
                                    .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    });
                            builder.create();
                            builder.show();
                        }

                        return true;


                    default:
                        return false;
                }
            }
        });




        //LISTENERS
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Methodes.internet_diponible(activity)) {
                    Intent intent = new Intent(activity, Activity_lancement.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(Activity_details_utilisateur.this, "Actuellement non disponible", Toast.LENGTH_SHORT).show();

                }

            }
        });


    }


    /**
     * INITIALISATION vue avec ses CD
     */
    public void load_ses_cd() {
        fab.setImageResource(R.drawable.icon_disk_add);
        linearLayout_info.setVisibility(View.GONE);
        listView_details.setVisibility(View.VISIBLE);
        SimpleAdapter mSchedule = new SimpleAdapter(this.getBaseContext(),
                listItem_ses_cd, R.layout.layout_cd, new String[]{"info"}, new int[]{R.id.textView_info_cd}) {
            public View getView(int position, View convertView, ViewGroup parent) {

                HashMap<String, String> map = (HashMap<String, String>) listView_details
                        .getItemAtPosition(position);
                final String url_image = map.get("image");

                View view = super.getView(position, convertView, parent);
                ImageView image_view_cd = (ImageView) view.findViewById(R.id.image_view_cd);
                Picasso.with(image_view_cd.getContext()).load(url_image).centerCrop().fit().into(image_view_cd);
                return view;
            }
        };
        listView_details.setAdapter(mSchedule);
    }

    /**
     * Initilisation avec les infos utilisateurs
     */
    public void load_info_utilisateur() {
        fab.setImageResource(R.drawable.icon_edit);
        //TODO charger info utilisteur
        linearLayout_info.setVisibility(View.VISIBLE);
        // layout_info.setVisibility(View.VISIBLE);
        listView_details.setVisibility(View.GONE);
        //textView_nom_utilisateur.setText("ah le dessert");
    }

    /**
     * Initialisation avec les cd empruntés
     */
    public void load_cd_emprunter() {

        fab.setImageResource(R.drawable.icon_disk_add);
        linearLayout_info.setVisibility(View.GONE);
        listView_details.setVisibility(View.VISIBLE);
        SimpleAdapter mSchedule = new SimpleAdapter(this.getBaseContext(),
                listItem_cd_emprunter, R.layout.layout_cd, new String[]{"info"}, new int[]{R.id.textView_info_cd}) {
            public View getView(int position, View convertView, ViewGroup parent) {

                HashMap<String, String> map = (HashMap<String, String>) listView_details
                        .getItemAtPosition(position);
                final String url_image = map.get("image");

                View view = super.getView(position, convertView, parent);
                ImageView image_view_cd = (ImageView) view.findViewById(R.id.image_view_cd);
                Picasso.with(image_view_cd.getContext()).load(url_image).centerCrop().fit().into(image_view_cd);
                return view;
            }
        };
        listView_details.setAdapter(mSchedule);
    }

    /**
     * Gestion touche retour
     */
    @Override
    public void onBackPressed() {
        retour();
    }

    public void retour() {
        if (!Methodes.internet_diponible(activity)) {
            Intent intent = new Intent(activity, Activity_lancement.class);
            startActivity(intent);
            finish();
        }else{
            Intent intent = null;
            User user = table_user_online.get_user(Integer.parseInt(string_id_user));
            if (user.getType().equals("admin")) {
                intent = new Intent(activity, Activity_administrateur.class);
            } else {
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

}
