package com.daumont.vasi.vasi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.daumont.vasi.vasi.activity.ActivityLogin;
import com.daumont.vasi.vasi.activity.Activity_administrateur;
import com.daumont.vasi.vasi.activity.Activity_lancement;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * Created by Jonathan Daumont on 09/06/2017.
 */

public class Methodes {

    /**
     * Permet de connaitre l'état de la connexion internet
     * @param activity
     * @return
     */
    public static boolean internet_diponible(Activity activity)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null)
        {
            NetworkInfo.State networkState = networkInfo.getState();
            if (networkState.compareTo(NetworkInfo.State.CONNECTED) == 0)
            {
                return true;
            }
            else{
                activity.startActivity(new Intent(activity, Activity_lancement.class));
                activity.finish();
                return false;
            }
        }else{
            activity.startActivity(new Intent(activity, Activity_lancement.class));
            activity.finish();
            return false;
        }

    }

    public static boolean internet_diponible_activity_start(Activity activity)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null)
        {
            NetworkInfo.State networkState = networkInfo.getState();
            if (networkState.compareTo(NetworkInfo.State.CONNECTED) == 0) //connecté à internet
            {
                return true;
            }
            else{
                return false;
            }
        }
        else return false;
    }

    /**
     * CREATION DU QRCODE
     * @param content
     * @return
     */
    public static Bitmap generateQRBitmap(String content){

        Bitmap bitmap = null;
        int width=250,height=250;

        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height);
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * Show a dialog to inform a user
     * @param message a
     * @param activity a
     */
    public static void info_dialog(String message,Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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


