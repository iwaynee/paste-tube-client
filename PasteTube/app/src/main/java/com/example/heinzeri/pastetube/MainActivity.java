package com.example.heinzeri.pastetube;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    public static String TAG = "PasteTube";
    public static final String MESSAGE_USERID = "com.example.heinzeri.pastetube.USERID";

    private Button button_create_user;
    private Button button_old_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}
