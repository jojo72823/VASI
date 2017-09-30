package com.daumont.vasi.vasi.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.daumont.vasi.vasi.Methodes;
import com.daumont.vasi.vasi.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * PAR JONATHAN DAUMONT
 * permet de rendre le cd
 * scanne le qrcode
 */
public class Activity_rendre_cd extends AppCompatActivity {

    /**
     * Declaration variables
     */
    //Elements graphiques
    private Button button_scanner;
    //Autres
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        //Recuperation des elements visuels
        setContentView(R.layout.activity_rendre_cd);
        button_scanner = (Button) findViewById(R.id.button_scanner);

        //Initialisation variables


        //LISTENER
        button_scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Methodes.internet_diponible(activity)) {
                    Intent intent = new Intent(activity, Activity_lancement.class);
                    startActivity(intent);
                    finish();
                }else{
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
                Toast.makeText(this, "erreur scan", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "scann : " + result.getContents(), Toast.LENGTH_LONG).show();
                //TODO rendre cd
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
