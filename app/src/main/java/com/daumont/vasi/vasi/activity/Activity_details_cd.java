package com.daumont.vasi.vasi.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.daumont.vasi.vasi.Methodes;
import com.daumont.vasi.vasi.R;
import com.daumont.vasi.vasi.database.Table_cd_online;
import com.daumont.vasi.vasi.database.Table_user_online;
import com.daumont.vasi.vasi.deezer.GsonRequest;
import com.daumont.vasi.vasi.deezer.List_Album;
import com.daumont.vasi.vasi.deezer.List_title;
import com.daumont.vasi.vasi.modele.Album;
import com.daumont.vasi.vasi.modele.CD;
import com.daumont.vasi.vasi.modele.Title;
import com.daumont.vasi.vasi.modele.User;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * JONATHAN DAUMONT
 * Permet de visualiser les détails de l'album
 */
public class Activity_details_cd extends AppCompatActivity {

    /**
     * Declaration variables
     **/
    //Elements graphiques
    private android.support.v7.widget.Toolbar toolbar;
    private ImageView imageView;
    private TextView textView_nom_album, textView_nom_artist, textView_proprietaire, textView_etat_emprunt;
    private CollapsingToolbarLayout toolbar_layout;
    private ListView listView_titres;
    private FloatingActionButton fab;
    private LinearLayout layout_infos;
    private LinearLayout layout;

    //tableaux & lists
    private List_Album list_album;
    private HashMap<String, String> map_titres;
    private ArrayList<HashMap<String, String>> listItem_titres = new ArrayList<>();
    private ArrayList<Title> list_titres;
    private ArrayList<String> list_preview;
    private ArrayList<View> liste_view;

    //Base de donnees
    private Table_user_online table_user_online;
    private Table_cd_online table_cd_online;

    //JSON
    private RequestQueue queue, queue_titres;
    private GsonRequest<Album> gsonRequest_album;
    private GsonRequest<List_title> gsonRequest_titles;
    //AUTRES
    private String id_cd;
    private Context context;
    private User proprietaire;
    private CD mon_cd;
    private String string_id_album, string_id_artist, string_nom_artist, string_id_user;
    private String url_gson, url_gson_titres, url_image;
    private MediaPlayer mediaPlayer;
    private Activity activity;
    private boolean etat_media_fab;
    private int position_old;

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

    /**
     * Listener sur le menu haut droite
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_suppression:
                // TODO Something when menu item selected
                Toast.makeText(this, "supprimer", Toast.LENGTH_SHORT).show();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Création de l'activite
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        //Recuperation des elements visuels
        setContentView(R.layout.activity_details_cd);
        CollapsingToolbarLayout toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        AppBarLayout app_bar = (AppBarLayout) findViewById(R.id.app_bar);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        layout_infos = (LinearLayout) findViewById(R.id.layout_infos);
        textView_nom_album = (TextView) findViewById(R.id.textView_nom_album);
        textView_nom_artist = (TextView) findViewById(R.id.textView_nom_artist);
        textView_proprietaire = (TextView) findViewById(R.id.textView_proprietaire);
        textView_etat_emprunt = (TextView) findViewById(R.id.textView_etat_emprunt);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_details_album);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        listView_titres = (ListView) findViewById(R.id.listView_titres);
        listView_titres.setVisibility(View.GONE);
        toolbar.inflateMenu(R.menu.menu_suppression);
        listView_titres.setNestedScrollingEnabled(true);
        layout_infos.setNestedScrollingEnabled(true);
        imageView = (ImageView) findViewById(R.id.imageView_qrcode);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        //Recuperation parametres
        Bundle objetbunble = this.getIntent().getExtras();
        if (objetbunble != null) {
            id_cd = objetbunble.getString("id_cd");
            string_id_user = objetbunble.getString("id_user");
        }
        //Initialisation bdd
        table_cd_online = new Table_cd_online(this);
        table_user_online = new Table_user_online(this);
        //Initialisation variables
        mediaPlayer = new MediaPlayer();
        context = this;
        list_titres = new ArrayList<>();
        list_preview = new ArrayList<>();
        etat_media_fab = false;
        liste_view = new ArrayList<>();
        position_old = 0;
        //Initialisation JSON
        queue_titres = Volley.newRequestQueue(context);
        url_gson_titres = "https://api.deezer.com/album/" + id_cd + "/tracks";
        gsonRequest_titles = new GsonRequest<>(
                url_gson_titres, List_title.class, null, new Response.Listener<List_title>() {
            @Override
            public void onResponse(final List_title response) {
                for (int i = 0; i < response.getData().size(); i++) {
                    map_titres = new HashMap<>();
                    map_titres.put("id", "" + response.getData().get(i).getId());
                    map_titres.put("info", "" + (i+1)+" - "+response.getData().get(i).getTitle());
                    listItem_titres.add(map_titres);
                    list_preview.add(response.getData().get(i).getPreview());
                }

                SimpleAdapter mSchedule = new SimpleAdapter(context,
                        listItem_titres, R.layout.layout_titre, new String[]{"info"}, new int[]{R.id.textView_info_titre}) {
                    public View getView(int position, View convertView, ViewGroup parent) {

                        HashMap<String, String> map = (HashMap<String, String>) listView_titres
                                .getItemAtPosition(position);
                        View view = super.getView(position, convertView, parent);
                        liste_view.add(view);
                        ImageView image_view_cd = (ImageView) view.findViewById(R.id.image_view_titre);
                        Picasso.with(image_view_cd.getContext()).load(url_image).centerCrop().fit().into(image_view_cd);
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

                        try {
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(list_preview.get(position));
                            mediaPlayer.prepare();

                            if (etat_media_fab == false) {
                                etat_media_fab = true;
                                fab.setImageDrawable(getResources().getDrawable(R.drawable.icon_pause));
                                mediaPlayer.start();
                                Toast.makeText(Activity_details_cd.this, "Lecture de "+info, Toast.LENGTH_SHORT).show();
                            }


                            if (position == position_old) {
                                etat_media_fab = false;
                                fab.setImageDrawable(getResources().getDrawable(R.drawable.icon_play));
                                mediaPlayer.stop();

                            }else{
                                etat_media_fab = true;
                                fab.setImageDrawable(getResources().getDrawable(R.drawable.icon_pause));
                                mediaPlayer.start();
                                Toast.makeText(Activity_details_cd.this, "Lecture de "+info, Toast.LENGTH_SHORT).show();
                            }




                            position_old = position;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.start();
                    }

                });


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue_titres.add(gsonRequest_titles);


        Bitmap bitmap = Methodes.generateQRBitmap(id_cd);
        imageView.setImageBitmap(bitmap);
        mon_cd = table_cd_online.get_cd(id_cd);
        proprietaire = table_user_online.get_user(mon_cd.getId_proprio());
        textView_nom_album.setText("Nom album : " + mon_cd.getNom_album());
        textView_nom_artist.setText("Nom artiste : " + mon_cd.getNom_artist());
        textView_proprietaire.setText("Propriétaire : " + proprietaire.getIdentifiant());
        textView_etat_emprunt.setText("Aucun renseignement sur l'emprunt");

        //TODO RECHERCHER EMPRUNT
        toolbar_layout.setTitle(mon_cd.getNom_artist() + " - " + mon_cd.getNom_album());
        ImageView imageView_cd = (ImageView) app_bar.findViewById(R.id.imageView_cd);
        Picasso.with(imageView_cd.getContext()).load(mon_cd.getImage()).centerCrop().fit().into(imageView_cd);
        imageView_cd.setColorFilter(Color.parseColor("#7F000000"));

        toolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_suppression:

                        /**ON DEMANDE CONFIRMATION*****************************************/
                        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_details_cd.this);
                        builder.setCancelable(false);
                        builder.setMessage("Etes-vous sûr de vouloir supprimer l'album de la CDthèque ?")
                                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (mon_cd.getId_proprio() == Integer.parseInt(string_id_user)) {
                                            table_cd_online.delete_cd(mon_cd.getId_cd());
                                            retour();
                                        } else {
                                            Methodes.info_dialog("Vous n'êtes pas le propriétaire vous ne pouvez pas le supprimer", activity);
                                        }


                                    }
                                })
                                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });
                        builder.create();
                        builder.show();
                        return true;


                    default:
                        return false;
                }
            }
        });

        //Listener sur FAB
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(list_preview.get(0));
                    mediaPlayer.prepare();
                    if (etat_media_fab == false) {
                        etat_media_fab = true;
                        fab.setImageDrawable(getResources().getDrawable(R.drawable.icon_pause));
                        mediaPlayer.start();
                    } else {
                        etat_media_fab = false;
                        fab.setImageDrawable(getResources().getDrawable(R.drawable.icon_play));
                        mediaPlayer.stop();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
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

    public void retour() {
        Intent intent = null;
        User user = table_user_online.get_user(Integer.parseInt(string_id_user));
        if (user.getType().equals("admin")) {
            intent = new Intent(Activity_details_cd.this, Activity_administrateur.class);
        } else {
            intent = new Intent(Activity_details_cd.this, Activity_utilisateur.class);
        }
        Bundle objetbunble = new Bundle();
        objetbunble.putString("id_user", string_id_user);
        intent.putExtras(objetbunble);
        startActivity(intent);
        overridePendingTransition(R.anim.pull_in_return, R.anim.push_out_return);
        mediaPlayer.stop();
        finish();
    }


}
