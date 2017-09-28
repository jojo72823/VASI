package com.daumont.vasi.vasi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.daumont.vasi.vasi.R;
import com.daumont.vasi.vasi.deezer.GsonRequest;
import com.daumont.vasi.vasi.deezer.List_Album;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * PAR JONATHAN DAUMONT
 * Permet de visualiser les résultats de recherches deezer
 * montre les albums de l'artiste recherche
 */
public class Activity_rechercher_album extends AppCompatActivity {

    /**
     * Declaration variables
     **/
    //Elements graphiquess
    private ListView list_view;
    private Context context;
    private Toolbar toolbar;
    //tableaux & lists
    private HashMap<String, String> map;
    private ArrayList<HashMap<String, String>> listItem = new ArrayList<>();
    private List_Album list_album;
    //JSON
    private RequestQueue queue;
    private GsonRequest<List_Album> gsonRequest;
    //Autres
    private String string_id_artist;
    private String nom_artist,string_id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Recuperation de l'interface
        setContentView(R.layout.activity_rechercher_album);
        list_view = (ListView) findViewById(R.id.listView_album);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //Initialization
        context = this.getBaseContext();
        list_album = new List_Album();

        //Recuperation parametres
        Bundle objetbunble = this.getIntent().getExtras();
        if (objetbunble != null) {
            string_id_artist = objetbunble.getString("id_artist");
            string_id_user =  objetbunble.getString("id_user");
            nom_artist = objetbunble.getString("nom_artist");
            toolbar.setTitle("Albums de "+ nom_artist);

            //JSON recuperation album de l'artiste
            queue = Volley.newRequestQueue(context);
            String url = "https://api.deezer.com/artist/" + string_id_artist + "/albums";
            gsonRequest = new GsonRequest<List_Album>(
                    url, List_Album.class, null, new Response.Listener<List_Album>() {
                @Override
                public void onResponse(final List_Album response) {
                    list_album = response;
                    //Génération de la listview
                    for (int i = 0; i < list_album.getData().size(); i++) {
                        map = new HashMap<>();
                        map.put("id_album", list_album.getData().get(i).getId());
                        map.put("name_album", list_album.getData().get(i).getTitle());
                        map.put("cover", list_album.getData().get(i).getCover());
                        listItem.add(map);
                    }

                    SimpleAdapter simpleAdapter = new SimpleAdapter(context,
                            listItem, R.layout.cell_cards, new String[]{"name_album"}, new int[]{R.id.title}) {
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            ImageView image = (ImageView) view.findViewById(R.id.image);
                            Picasso.with(image.getContext()).load(list_album.getData().get(position).getCoverBig()).centerCrop().fit().into(image);

                            return view;
                        }
                    };

                    list_view.setAdapter(simpleAdapter);

                    //LISTENER sur la listView
                    list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        public void onItemClick(AdapterView<?> a, View v, int position,
                                                long id) {

                            HashMap<String, String> map = (HashMap<String, String>) list_view
                                    .getItemAtPosition(position);

                            Intent intent = new Intent(Activity_rechercher_album.this, Activity_ajouter_cd.class);
                            Bundle objetbunble = new Bundle();
                            objetbunble.putString("id_album",  map.get("id_album"));
                            objetbunble.putString("id_artist", string_id_artist);
                            objetbunble.putString("nom_artist",nom_artist);
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
    }

    /**
     * GESTION TOUCHE RETOUR
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Activity_rechercher_album.this, Activity_rechercher_artiste.class);
        Bundle objetbunble = new Bundle();
        objetbunble.putString("id_user", string_id_user);
        intent.putExtras(objetbunble);
        startActivity(intent);
        overridePendingTransition(R.anim.pull_in_return, R.anim.push_out_return);
        finish();
    }
}
