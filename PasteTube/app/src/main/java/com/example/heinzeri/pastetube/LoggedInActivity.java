package com.example.heinzeri.pastetube;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.widget.ImageView;
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

    public Bitmap bmp;

    private TextView text_userid;
    private ImageView image_qr_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loged_in);

        text_userid = findViewById(R.id.text_userid);
        image_qr_code = findViewById(R.id.image_qr_code);


        clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

        // Get USERID
        Intent intent = getIntent();
        final String message_userid = intent.getStringExtra(MainActivity.MESSAGE_USERID);

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                build_screen(message_userid);
            }
        });
        thread.start();
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
                Bitmap bmp_temp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++){
                    for (int y = 0; y < height; y++){
                        bmp_temp.setPixel(x, y, bitMatrix.get(x,y) ? Color.BLACK : Color.WHITE);
                    }
                }

                bmp = bmp_temp;

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        image_qr_code.setImageBitmap(bmp);
                        text_userid.setText(userid);
                    }
                });
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }



        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                PasteHandler(userid);
            }
        });
        thread.start();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        thread.stop();

    }

    public void PasteHandler(String userid){

        ClipData clipData;
        String online_data;
        String online_data_old = null;
        String offline_data_old = null;
        ClipData offline_data_clip;
        String offline_data;


        while (true) {

            online_data = client.Paste(userid);
            offline_data_clip = clipboard.getPrimaryClip();
            offline_data = offline_data_clip.getItemAt(0).getText().toString();



            if (!online_data.equals(online_data_old)){
                online_data_old = online_data;
                offline_data_old = online_data;

                clipData = ClipData.newPlainText("synced text",
                        online_data);

                clipboard.setPrimaryClip(clipData);

                Log.i(TAG, online_data);
            } else if (!offline_data.equals(offline_data_old)){
                online_data_old = offline_data;
                offline_data_old = offline_data;

                client.Copy(userid, offline_data);

                Log.i(TAG, offline_data);

            }



            /*

            // Examines the item on the clipboard. If getText() does not return null, the clip item contains the
            // text. Assumes that this application can only handle one item at a time.
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

            // Gets the clipboard as text.
            String pasteData = item.getText().toString();

            // If the string contains data, then the paste operation is done
            if (pasteData != "") {
                return;

                // The clipboard does not contain text. If it contains a URI, attempts to get data from it
            } else {
                Log.v(TAG, item.getText().toString());
                client.Copy(userid, item.getText().toString());
            }
            */
        }
    }
}
