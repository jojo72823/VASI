package com.daumont.vasi.vasi.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.daumont.vasi.vasi.Methodes;
import com.daumont.vasi.vasi.R;
import com.daumont.vasi.vasi.database.Table_cd_online;
import com.daumont.vasi.vasi.database.Table_emprunt;
import com.daumont.vasi.vasi.database.Table_user_online;
import com.daumont.vasi.vasi.deezer.GsonRequest;
import com.daumont.vasi.vasi.deezer.List_Album;
import com.daumont.vasi.vasi.deezer.List_title;
import com.daumont.vasi.vasi.modele.Album;
import com.daumont.vasi.vasi.modele.CD;
import com.daumont.vasi.vasi.modele.Emprunt;
import com.daumont.vasi.vasi.modele.Title;
import com.daumont.vasi.vasi.modele.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class Activity_emprunter_album_details extends AppCompatActivity {

    /**
     * Declaration variables
     **/
    //Elements graphiques
    private android.support.v7.widget.Toolbar toolbar;
    private ImageView imageView;
    private TextView textView_nom_album,textView_nom_artist,textView_proprietaire,textView_etat_emprunt;
    private ListView listView_titres;
    private FloatingActionButton fab_emprunt;
    private LinearLayout layout_infos;
    private LinearLayout layout;

    //tableaux & lists
    private List_Album list_album;
    private HashMap<String, String> map_titres;
    private ArrayList<HashMap<String, String>> listItem_titres = new ArrayList<>();
    private ArrayList<Title> list_titres;
    //Base de donnees
    private Table_user_online table_user_online;
    private Table_cd_online table_cd_online;
    private Table_emprunt table_emprunt;

    //JSON
    private RequestQueue queue, queue_titres;
    private GsonRequest<Album> gsonRequest_album;
    private GsonRequest<List_title> gsonRequest_titles;
    //AUTRES
    private Context context;
    private User proprietaire;
    private CD mon_cd;
    private String string_id_album, string_id_artist,string_nom_artist,string_id_user,string_qr_code,string_id_cd;
    private String url_gson, url_gson_titres, url_image;
    private Activity activity;

    /**
     * Permet de naviguer entre les différents onglets grâce au menu du bas
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            BottomNavigationView navigation;

            //TOTO VIEWFLIPEER
            switch (item.getItemId()) {
                case R.id.navigation_information_cd:
                    listView_titres.setVisibility(View.GONE);
                    layout_infos.setVisibility(View.VISIBLE);

                    return true;
                case R.id.navigation_liste_titres:
                    listView_titres.setVisibility(View.VISIBLE);
                    layout_infos.setVisibility(View.GONE);
                    return true;
            }
            return false;
        }

    };


    /**
     * Création de l'activite
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        //Recuperation des elements visuels
        setContentView(R.layout.activity_emprunter_album_details);
        CollapsingToolbarLayout toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        AppBarLayout app_bar = (AppBarLayout) findViewById(R.id.app_bar);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        layout_infos = (LinearLayout)findViewById(R.id.layout_infos);
        textView_nom_album =(TextView)findViewById(R.id.textView_nom_album);
        textView_nom_artist =(TextView)findViewById(R.id.textView_nom_artist);
        textView_proprietaire =(TextView)findViewById(R.id.textView_proprietaire);
        textView_etat_emprunt =(TextView)findViewById(R.id.textView_etat_emprunt);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_details_album);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        listView_titres = (ListView)findViewById(R.id.listView_titres);
        listView_titres.setVisibility(View.GONE);
        listView_titres.setNestedScrollingEnabled(true);
        layout_infos.setNestedScrollingEnabled(true);
        imageView = (ImageView) findViewById(R.id.imageView_qrcode);
        fab_emprunt = (FloatingActionButton) findViewById(R.id.fab_emprunt);

        //Recuperation parametres
        Bundle objetbunble = this.getIntent().getExtras();
        if (objetbunble != null) {
            string_id_cd = objetbunble.getString("id_cd");
            string_id_user = objetbunble.getString("id_user");
            string_qr_code = objetbunble.getString("qr_code");

        }

        if (!Methodes.internet_diponible(activity)) {
            Intent intent = new Intent(activity, Activity_lancement.class);
            startActivity(intent);
            finish();
        }else{
            //Initialisation bdd
            table_cd_online = new Table_cd_online(this);
            table_user_online = new Table_user_online(this);
            table_emprunt = new Table_emprunt(this);
            //Initialisation variables
            context = this;
            list_titres = new ArrayList<>();


            //Initialisation JSON
            queue_titres = Volley.newRequestQueue(context);
            url_gson_titres = "https://api.deezer.com/album/" + string_id_cd + "/tracks";
            gsonRequest_titles = new GsonRequest<>(
                    url_gson_titres, List_title.class, null, new Response.Listener<List_title>() {
                @Override
                public void onResponse(final List_title response) {
                    for (int i = 0; i < response.getData().size(); i++) {
                        map_titres = new HashMap<>();
                        map_titres.put("id", "" + response.getData().get(i).getId());
                        map_titres.put("info", "" + response.getData().get(i).getTitle());
                        listItem_titres.add(map_titres);
                    }
                    SimpleAdapter mSchedule = new SimpleAdapter(context,
                            listItem_titres, R.layout.layout_titre, new String[]{"info"}, new int[]{R.id.textView_info_titre}) {
                        public View getView(int position, View convertView, ViewGroup parent) {

                            HashMap<String, String> map = (HashMap<String, String>) listView_titres
                                    .getItemAtPosition(position);
                            View view = super.getView(position, convertView, parent);
                            ImageView image_view_cd = (ImageView) view.findViewById(R.id.image_view_titre);
                            Picasso.with(image_view_cd.getContext()).load(url_image).centerCrop().fit().into(image_view_cd);
                            TextView textView_sous_titre = (TextView) view.findViewById(R.id.textView_info_sous_titre);
                            textView_sous_titre.setText("Par "+response.getData().get(0).getArtist().getName());
                            return view;
                        }
                    };
                    listView_titres.setAdapter(mSchedule);
                    listView_titres.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        public void onItemClick(AdapterView<?> a, View v, int position,
                                                long id) {
                            HashMap<String, String> map = (HashMap<String, String>) listView_titres
                                    .getItemAtPosition(position);
                            final String id_recup = map.get("id");
                            final String info = map.get("info");

                            Snackbar.make(v, "Non disponible", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            //TODO MEDIA AUDIO
                        }

                    });


                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            queue_titres.add(gsonRequest_titles);


            Bitmap bitmap = Methodes.generateQRBitmap(string_id_cd);
            imageView.setImageBitmap(bitmap);
            mon_cd = table_cd_online.get_cd(string_qr_code);
            proprietaire =  table_user_online.get_user(mon_cd.getId_proprio());
            textView_nom_album.setText("Nom album : "+mon_cd.getNom_album());
            textView_nom_artist.setText("Nom artiste : "+mon_cd.getNom_artist());
            textView_proprietaire.setText("Propriétaire : "+proprietaire.getIdentifiant());




            //RECHERCHER SI ALBUM DEJA EMPRUNTER
            if(table_emprunt.album_emprunter(string_qr_code)){
                info_dialog("Album actuellement emprunté");
                String identifiant = table_emprunt.album_emprunter_identifiant(string_qr_code);
                textView_etat_emprunt.setText("Emprunté par "+identifiant);
            }else{
                textView_etat_emprunt.setText("Pas emprunté");
            }

            //RECHERCHER EMPRUNT
            toolbar_layout.setTitle(mon_cd.getNom_artist() + " - " + mon_cd.getNom_album());
            ImageView imageView_cd = (ImageView) app_bar.findViewById(R.id.imageView_cd);
            Picasso.with(imageView_cd.getContext()).load(mon_cd.getImage()).centerCrop().fit().into(imageView_cd);
            imageView_cd.setColorFilter(Color.parseColor("#7F000000"));
        }



        //Listeners
        fab_emprunt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Methodes.internet_diponible(activity)) {
                    Intent intent = new Intent(activity, Activity_lancement.class);
                    startActivity(intent);
                    finish();
                }else{
                    //test si il n'appartient pas à l'utilisateur
                    if(table_emprunt.album_emprunter(string_qr_code)){
                        if(table_emprunt.album_emprunter_utilisateur(string_qr_code,string_id_user)){
                            info_dialog("Vous avez déjà envoyé une demande");
                        }else{
                            confirmation("Voulez-vous envoyer une demande d'emprunt pour l'album ?\nL'album est déjà emprunté vous serez sur fil d'attente.");
                        }

                    }else{
                        confirmation("Voulez-vous envoyer une demande d'emprunt pour l'album ?");
                    }
                }

            }
        });
    }

    /**
     * GESTION BOUTON RETOUR
     */
    @Override
    public void onBackPressed() {
        retour();
    }

    private void info_dialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_emprunter_album_details.this);
        builder.setMessage(message)
                .setPositiveButton("Fermer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create();
        builder.show();
    }

    public void confirmation(String message) {
        if (!Methodes.internet_diponible(activity)) {
            Intent intent = new Intent(activity, Activity_lancement.class);
            startActivity(intent);
            finish();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_emprunter_album_details.this);
            builder.setMessage(message)
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            table_emprunt.add_emprunt(new Emprunt(mon_cd.getId_proprio(),Integer.parseInt(string_id_user),mon_cd.getQr_code(),"demande"));
                            info_dialog("Demande d'emprunt envoyée");
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

    public void retour(){
        if (!Methodes.internet_diponible(activity)) {
            Intent intent = new Intent(activity, Activity_lancement.class);
            startActivity(intent);
            finish();
        }else{
            Intent i = new Intent(Activity_emprunter_album_details.this, Activity_emprunter_cd.class);
            Bundle objetbunble = new Bundle();
            objetbunble.putString("id_cd", string_id_cd);
            objetbunble.putString("id_user", "" + string_id_user);
            objetbunble.putString("qr_code", "" + string_qr_code);
            i.putExtras(objetbunble);
            startActivity(i);
            overridePendingTransition(R.anim.pull_in_return, R.anim.push_out_return);
            finish();
        }

    }


}
