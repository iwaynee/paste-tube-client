package com.example.heinzeri.pastetube;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    public static String TAG = "PasteTube";
    public static final String MESSAGE_USERID = "com.example.heinzeri.pastetube.USERID";

    private TextView version_text;
    private TextView online_text;
    private ImageView online_image;

    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setContentView(R.layout.activity_main);
        version_text = findViewById(R.id.textView2);
        online_text = findViewById(R.id.textView4);
        online_image = findViewById(R.id.imageView);
        version_text.setText(Constant.VERSION);

        // Start a function to check the server status.
        handler.postDelayed(
                runnable = new Runnable(){
                    @Override
                    public void run() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final Boolean running = isServerReachable(getApplicationContext());

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (running){
                                            online_text.setText("Server: Online");
                                            online_image.setColorFilter(Color.GREEN);
                                        } else {
                                            online_text.setText("Server: Offline");
                                            online_image.setColorFilter(Color.RED);

                                        }
                                    }
                                });

                            }
                        }).start();

                        handler.postDelayed(runnable, 5000);
                    }
                }, 0
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); //stop handler when activity not visible

    }

    public void click_create_user(View view){
        Intent intent = new Intent(MainActivity.this, LoggedInActivity.class);
        String message = null;
        intent.putExtra(MESSAGE_USERID, message);
        startActivity(intent);
    }

    public void click_use_qr(View view){
        Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
        //String message = "aaab7e791fcd43bb9435f9bf64dcec344";
        //intent.putExtra(MESSAGE_USERID, message);
        startActivity(intent);
    }

    //@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static public boolean isServerReachable(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            HttpURLConnection urlc = null;
            int resonse = 0;

            try {


                URL url = new URL(Constant.SERVER_URL);   // Change to "http://google.com" for www  test.
                urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(3 * 1000);          // 10 s.
                urlc.connect();
                resonse = urlc.getResponseCode();

            } catch (MalformedURLException e1) {
                return false;
            } catch (IOException e) {
                return false;

            } finally {
                if (urlc != null) {
                    urlc.disconnect();
                }
            }
            if (resonse == 200) {        // 200 = "OK" code (http connection is fine).
                Log.wtf("Connection", "Success !");
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}


