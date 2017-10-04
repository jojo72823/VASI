package com.daumont.vasi.vasi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import com.daumont.vasi.vasi.Methodes;
import com.daumont.vasi.vasi.R;
import com.daumont.vasi.vasi.database.Table_cd_online;
import com.daumont.vasi.vasi.database.Table_user_online;
import com.daumont.vasi.vasi.deezer.Deezer_request;
import com.daumont.vasi.vasi.deezer.GsonRequest;
import com.daumont.vasi.vasi.deezer.List_Artist;
import com.daumont.vasi.vasi.modele.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * JONATHAN DAUMONT
 * Recherche les artistes avec l'API Deezer
 */
public class Activity_rechercher_artiste extends AppCompatActivity {

    /**
     * Declaration variables
     */
    //Elements graphiques
    private ListView list_view;
    private Context context;
    private Toolbar toolbar;

    //tableaux & lists
    private HashMap<String, String> map;
    private List_Artist list_artist;

    //bdd
    private Table_user_online table_user_online;

    //JSON
    private Deezer_request deezer_request;
    private RequestQueue queue;
    private GsonRequest<List_Artist> gsonRequest;

    //Autres
    private String string_id_user;
    private User user;
    private Activity activity;
    /**
     * Création de l'activité
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        //Recuperation des elements visuels
        setContentView(R.layout.activity_rechercher_cd);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        list_view = (ListView) findViewById(R.id.listView_album);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.clearFocus();
        searchView.setQueryHint("Rechercher un artiste");
        searchView.requestFocusFromTouch();
        toolbar.setTitle("");


        //Recuperation parametres
        Bundle objetbunble = this.getIntent().getExtras();
        if (objetbunble != null) {
            string_id_user = objetbunble.getString("id_user");
        }

        if (!Methodes.internet_diponible(activity)) {
            Intent intent = new Intent(activity, Activity_lancement.class);
            startActivity(intent);
            finish();
        }else{
            //Initialisation bdd
            table_user_online = new Table_user_online(this);

            //Initialisation variables
            context = this;
            deezer_request = new Deezer_request(context);
            queue = Volley.newRequestQueue(this);
            user = table_user_online.get_user(Integer.parseInt(string_id_user));



            /**
             *Chargement des artistes
             */
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (!Methodes.internet_diponible(activity)) {
                        Intent intent = new Intent(activity, Activity_lancement.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(context, "Recherche de l'artiste " + query, Toast.LENGTH_LONG).show();

                        final ArrayList<HashMap<String, String>> listItem = new ArrayList<>();
                        RequestQueue queue;
                        list_artist = new List_Artist();
                        queue = Volley.newRequestQueue(context);
                        String url = "http://api.deezer.com/search/artist?q=" + query;
                        gsonRequest = new GsonRequest<>(
                                url, List_Artist.class, null, new Response.Listener<List_Artist>() {
                            @Override
                            public void onResponse(final List_Artist response) {


                                list_artist = response;

                                for (int i = 0; i < list_artist.getData().size(); i++) {
                                    map = new HashMap<>();
                                    map.put("id_artist", list_artist.getData().get(i).getId());
                                    map.put("nom_artist", list_artist.getData().get(i).getName());
                                    listItem.add(map);
                                }

                                SimpleAdapter simpleAdapter = new SimpleAdapter(context,
                                        listItem, R.layout.cell_cards, new String[]{"nom_artist"}, new int[]{R.id.title}) {
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        View view = super.getView(position, convertView, parent);
                                        ImageView image = (ImageView) view.findViewById(R.id.image);
                                        Picasso.with(image.getContext()).load(list_artist.getData().get(position).getPictureBig()).centerCrop().fit().into(image);
                                        return view;
                                    }
                                };

                                list_view.setAdapter(simpleAdapter);
                                list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    public void onItemClick(AdapterView<?> a, View v, int position,
                                                            long id) {
                                        HashMap<String, String> map = (HashMap<String, String>) list_view
                                                .getItemAtPosition(position);

                                        Intent intent = new Intent(Activity_rechercher_artiste.this, Activity_rechercher_album.class);
                                        Bundle objetbunble = new Bundle();
                                        objetbunble.putString("id_artist", map.get("id_artist"));
                                        objetbunble.putString("nom_artist", map.get("nom_artist"));
                                        objetbunble.putString("id_user", string_id_user);
                                        intent.putExtras(objetbunble);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                                    }
                                });
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });
                        queue.add(gsonRequest);
                    }


                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
            setSupportActionBar(toolbar);

        }

    }

    /**
     * Gestion touche retour
     */
    @Override
    public void onBackPressed() {
        if (!Methodes.internet_diponible(activity)) {
            Intent intent = new Intent(activity, Activity_lancement.class);
            startActivity(intent);
            finish();
        }else{
            Intent intent = null;
            if(user.getType().equals("admin")){
                intent = new Intent(Activity_rechercher_artiste.this, Activity_administrateur.class);
            }else{
                intent = new Intent(Activity_rechercher_artiste.this, Activity_utilisateur.class);
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
