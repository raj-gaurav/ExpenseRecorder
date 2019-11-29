package com.example.rajga.ecart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

    private EditText user_reg,pass_reg;
    private TextView backsign;
    private Button register;

    private FirebaseAuth auth;
    public SharedPreferences mPref;
    SharedPreferences.Editor editor;

    private ProgressDialog mdialog;
    public static final String PREF_NAME="user_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth=FirebaseAuth.getInstance();
        mdialog=new ProgressDialog(this);

        mPref=getSharedPreferences(PREF_NAME,MODE_PRIVATE);
        String user=mPref.getString("uID","");

        user_reg=(EditText)findViewById(R.id.user_reg);
        pass_reg=(EditText)findViewById(R.id.pass_reg);
        backsign=(TextView)findViewById(R.id.backsign);
        register= (Button) findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= user_reg.getText().toString().trim();
                String pass= pass_reg.getText().toString().trim();

                if(TextUtils.isEmpty(email))
                {
                    user_reg.setError("Username can't be eempty");
                    return;
                }
                if(TextUtils.isEmpty(pass))
                {
                    pass_reg.setError("Fill the password");
                    return;
                }

                mdialog.setMessage("Processing...");
                mdialog.show();

                auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                      if(task.isSuccessful()){

                          Toast.makeText(getApplicationContext(),"successfully registered", Toast.LENGTH_SHORT).show();
                          mdialog.dismiss();
                          editor=mPref.edit();
                          editor.putString("uID",FirebaseAuth.getInstance().getCurrentUser().getUid());
                          editor.apply();
                          startActivity(new Intent(getApplicationContext(),LoadingScreen.class));
                          finish();

                      }
                      else
                      {
                          Toast.makeText(getApplicationContext(),"registration failed", Toast.LENGTH_SHORT).show();
                          mdialog.dismiss();
                      }
                    }
                });
            }
        });

        backsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });

    }
}
