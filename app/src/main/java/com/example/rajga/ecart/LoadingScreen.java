package com.example.rajga.ecart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.FirebaseDatabase;

public class LoadingScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        ProgressDialog dialog=new ProgressDialog(this);
        dialog.show();
        dialog.setMessage("Retrieving data....");


        FirebaseDatabase.getInstance().getReference().keepSynced(true);
        Intent i=new Intent(getApplicationContext(),Home.class);
        startActivity(i);
        finish();

    }
}
