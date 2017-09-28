package com.daumont.vasi.vasi.deezer;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

/**
 * Créé par JONATHAN DAUMONT le 01/06/2017.
 */

public class Deezer_request {

    private List_Artist list_artist;
    private Context context;

    public Deezer_request(Context context) {

        this.context = context;
    }


    public List_Artist getArtist(String nom_artist) {

        RequestQueue queue;
        list_artist = new List_Artist();
        queue = Volley.newRequestQueue(this.context);
        String url = "http://api.deezer.com/search/artist?q=" + nom_artist;


        GsonRequest<List_Artist> gsonRequest = new GsonRequest<List_Artist>(
                url, List_Artist.class, null, new Response.Listener<List_Artist>() {
            @Override
            public void onResponse(final List_Artist response) {


                list_artist = response;


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(gsonRequest);
        return list_artist;
    }
}
