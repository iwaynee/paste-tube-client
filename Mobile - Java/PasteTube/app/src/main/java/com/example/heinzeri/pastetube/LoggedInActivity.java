package com.example.heinzeri.pastetube;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONArray;
import org.json.JSONException;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class LoggedInActivity extends AppCompatActivity {
    public static String TAG = "PasteTube";

    private PasteTube client;
    private Thread thread;
    private ClipboardManager clipboard;

    public String userid;
    private PasteHandler pasteHandler;

    public Bitmap bmp;

    public TextView text_userid;
    public ImageView image_qr_code;
    public ProgressBar progressBar;

    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

        // Create Handler
        handler = new Handler();

    }

    @Override
    protected void onResume(){
        super.onResume();

        setContentView(R.layout.activity_loged_in);

        // Get all handles
        text_userid = findViewById(R.id.text_userid);
        image_qr_code = findViewById(R.id.image_qr_code);
        progressBar = findViewById(R.id.progressBar);

        // Get UserID
        Intent intent = getIntent();
        final String message_userid = intent.getStringExtra(MainActivity.MESSAGE_USERID);

        if (userid == null) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    build_screen(message_userid);
                }
            });
            thread.start();
        } else {

            // Check if userid is valid!
            Boolean connected = client.Connect(userid);
            if (!connected){
                finish();
            }





            handler.post(
                    runnable = new Runnable(){
                        @Override
                        public void run() {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    pasteHandler.check();
                                }
                            }).start();

                            handler.postDelayed(runnable, 500);
                        }
                    }
            );


        }
    }

    private void build_screen(String message_userid){

        // Create Client
        client = new PasteTube(LoggedInActivity.this);

        // Check if there is a Userid
        if (message_userid == null) {
             userid = client.CreateUser();
            if (userid == null) {
                finish();
            }
        } else {
            userid = message_userid;
        }

        Boolean state = false;

        JSONArray devices = client.GetConnectedDevices(userid);
        if (devices != null) {

            // Get public IP

            try {
                URL whatismyip = new URL("http://checkip.amazonaws.com");
                BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
                String ip_public = in.readLine();

                for (int i = 0; i < devices.length(); i++) {
                    String ip = devices.getJSONObject(i).getString("ip");
                    Log.v(TAG, ip + " " + ip_public);
                    if (ip.equals(ip_public)) {
                        state = true;
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Boolean connected = true;
        Log.v(TAG, "Connected ?" + state.toString());
        if (!state){
            connected = client.Connect(userid);
        }
        if (!connected){
            finish();
        }



        // this is a small sample use of the QRCodeEncoder class from zxing
        try {
            // generate a 150x150 QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            BitMatrix bitMatrix = qrCodeWriter.encode(userid, BarcodeFormat.QR_CODE, 300, 300);

            if(bitMatrix != null) {
                int height = bitMatrix.getHeight();
                int width = bitMatrix.getWidth();
                final Bitmap bmp_temp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++){
                    for (int y = 0; y < height; y++){
                        bmp_temp.setPixel(x, y, bitMatrix.get(x,y) ? Color.BLACK : Color.WHITE);
                    }
                }

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        image_qr_code.setImageBitmap(bmp_temp);
                        text_userid.setText(userid);
                    }
                });
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }

        pasteHandler = new PasteHandler(userid, clipboard, client);

        handler.post(
                runnable = new Runnable(){
                    @Override
                    public void run() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                pasteHandler.check();
                            }
                        }).start();

                        handler.postDelayed(runnable, 500);
                    }
                }
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (runnable != null) {
            handler.removeCallbacks(runnable); //stop handler when activity not visible
        }
    }
    /*
    public void PasteHandler(String userid){




        while (true) {


            *
        }
    }
    */
}

