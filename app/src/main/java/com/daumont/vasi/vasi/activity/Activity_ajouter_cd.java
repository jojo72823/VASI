package com.daumont.vasi.vasi.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.daumont.vasi.vasi.R;
import com.daumont.vasi.vasi.database.Table_cd_online;
import com.daumont.vasi.vasi.database.Table_user_online;
import com.daumont.vasi.vasi.deezer.GsonRequest;
import com.daumont.vasi.vasi.deezer.List_title;
import com.daumont.vasi.vasi.modele.Album;
import com.daumont.vasi.vasi.modele.CD;
import com.daumont.vasi.vasi.modele.Emprunt;
import com.daumont.vasi.vasi.modele.Title;
import com.daumont.vasi.vasi.modele.User;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * JONATHAN DAUMONT
 * Permet d'ajouter des CD grace a l'API DEEZER
 */
public class Activity_ajouter_cd extends AppCompatActivity {

    /**
     * Declaration variables
     **/
    //Elements graphiques
    private Context context;
    private View view_content;
    private Button button_ajouter_cd;
    private ImageView imageView_cd;
    private AppBarLayout app_bar;
    private CollapsingToolbarLayout toolbar_layout;
    private ListView listView_titres;
    private FloatingActionButton fab;
    //tableaux & lists
    private HashMap<String, String> map_titres;
    private ArrayList<HashMap<String, String>> listItem_titres = new ArrayList<>();
    private ArrayList<Title> list_titres;
    private ArrayList<String> list_preview;
    //base de données
    private Table_cd_online table_cd_online;
    private Table_user_online table_user_online;
    //JSON
    private RequestQueue queue, queue_titres;
    private GsonRequest<Album> gsonRequest_album;
    private GsonRequest<List_title> gsonRequest_titles;
    //autres
    private CD mon_cd;
    private String string_id_album, string_id_artist, string_nom_artist, string_id_user, url_gson, url_gson_titres, url_image;
    private User user;
    private MediaPlayer mediaPlayer;

    /**
     * Création de l'activite
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Recuperation des elements visuels
        setContentView(R.layout.activity_ajouter_cd);
        view_content = findViewById(R.id.content_ajouter_cd);
        button_ajouter_cd = (Button) findViewById(R.id.button_ajouter_cd);
        app_bar = (AppBarLayout) findViewById(R.id.app_bar);
        imageView_cd = (ImageView) app_bar.findViewById(R.id.imageView_cd);
        toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        listView_titres = (ListView) view_content.findViewById(R.id.listView_titres);
        fab = (FloatingActionButton) findViewById(R.id.fab_lecture);
        listView_titres.setNestedScrollingEnabled(true);

        //Recuperation parametres
        Bundle objetbunble = this.getIntent().getExtras();
        if (objetbunble != null) {
            string_id_album = objetbunble.getString("id_album");
            string_id_artist = objetbunble.getString("id_artist");
            string_nom_artist = objetbunble.getString("nom_artist");
            string_id_user = objetbunble.getString("id_user");
        }

        //Initialisation bd
        table_cd_online = new Table_cd_online(this);
        table_user_online = new Table_user_online(this);

        //Initialisation variables
        list_titres = new ArrayList<>();
        context = this;
        user = table_user_online.get_user(Integer.parseInt(string_id_user));
        list_preview = new ArrayList<String>();
        mediaPlayer = new MediaPlayer();

        //Initialisation JSON
        //recuperation de l'album
        queue = Volley.newRequestQueue(context);
        url_gson = "https://api.deezer.com/album/" + string_id_album;
        gsonRequest_album = new GsonRequest<Album>(
                url_gson, Album.class, null, new Response.Listener<Album>() {
            @Override
            public void onResponse(final Album response) {
                try {
                    Picasso.with(imageView_cd.getContext()).load(response.getCoverBig()).centerCrop().fit().into(imageView_cd);
                    imageView_cd.setColorFilter(Color.parseColor("#7F000000"));
                    url_image = response.getCoverSmall();
                    mon_cd = new CD(Integer.parseInt(response.getId()), response.getTitle(), response.getArtist().getName(), Integer.parseInt(string_id_user), response.getCoverMedium());
                    toolbar_layout.setTitle(mon_cd.getNom_artist() + " - " + mon_cd.getNom_album());
                } catch (Exception e) {
                    retour();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(gsonRequest_album);
        //recuperation des titres de l'album
        queue_titres = Volley.newRequestQueue(context);
        url_gson_titres = "https://api.deezer.com/album/" + string_id_album + "/tracks";
        gsonRequest_titles = new GsonRequest<>(
                url_gson_titres, List_title.class, null, new Response.Listener<List_title>() {
            @Override
            public void onResponse(final List_title response) {
                for (int i = 0; i < response.getData().size(); i++) {
                    map_titres = new HashMap<>();
                    map_titres.put("id", "" + response.getData().get(i).getId());
                    map_titres.put("info", "" + response.getData().get(i).getTitle());
                    listItem_titres.add(map_titres);
                    list_preview.add(response.getData().get(i).getPreview());
                }
                SimpleAdapter mSchedule = new SimpleAdapter(context,
                        listItem_titres, R.layout.layout_titre, new String[]{"info"}, new int[]{R.id.textView_info_titre}) {
                    public View getView(int position, View convertView, ViewGroup parent) {

                        HashMap<String, String> map = (HashMap<String, String>) listView_titres
                                .getItemAtPosition(position);
                        View view = super.getView(position, convertView, parent);
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

                        Snackbar.make(v, "Non disponible", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        //TODO MEDIA AUDIO
                        try {
                            mediaPlayer.setDataSource(response.getData().get(position).getPreview());

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


        //Listeners
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Snackbar.make(view, "Non disponible", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                try {
                    String preview = list_preview.get(0);

                    mediaPlayer.setDataSource(preview);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
                //TODO MEDIA AUDIO
            }
        });

        button_ajouter_cd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmation();
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

    /**
     * Fonction pour retourner à l'activité précédente
     */
    public void retour() {
        Intent intent = new Intent(Activity_ajouter_cd.this, Activity_rechercher_album.class);
        Bundle objetbunble = new Bundle();
        objetbunble.putString("id_artist", string_id_artist);
        objetbunble.putString("nom_artist", string_nom_artist);
        objetbunble.putString("id_user", string_id_user);
        intent.putExtras(objetbunble);
        startActivity(intent);
        overridePendingTransition(R.anim.pull_in_return, R.anim.push_out_return);
        finish();
    }

    /**
     * Afficher un dialog pour afficher une information
     * @param message
     */
    private void info_dialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_ajouter_cd.this);
        builder.setCancelable(false);
        builder.setMessage(message)
                .setPositiveButton("Fermer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        table_cd_online.add_cd(mon_cd);
                        Intent intent = null;
                        if(user.getType().equals("admin")){
                            intent = new Intent(Activity_ajouter_cd.this, Activity_administrateur.class);
                        }else{
                            intent = new Intent(Activity_ajouter_cd.this, Activity_utilisateur.class);
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

    /**
     * Afficher un dialog pour confirmer l'action
     */
    public void confirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_ajouter_cd.this);
        builder.setCancelable(false);
        builder.setMessage("Voulez-vous ajouter le CD à la CDThèque ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        info_dialog("CD ajouté à la CDThèque");
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
