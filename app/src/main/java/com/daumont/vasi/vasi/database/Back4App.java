package com.daumont.vasi.vasi.database;

import android.content.Context;
import android.util.Log;

import com.parse.Parse;

/**
 * Created by jojo- on 16/08/2017.
 */

public class Back4App {


    /**
     * Connexion of database online
     * @param context context of activity
     */
    public Back4App(Context context){
        try {
            Parse.initialize(new Parse.Configuration.Builder(context)
                    .applicationId("GK27aSEIy0CPD1BOfDAi5atbrCtdsQSMRrsNvfnK")
                    .clientKey("RKGHvJDLKqD7wKpvwi7g42DejP828lNCqZnBSrwa")
                    .server("https://parseapi.back4app.com")
                    .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
