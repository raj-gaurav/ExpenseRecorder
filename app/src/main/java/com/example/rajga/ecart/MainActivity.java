package com.example.rajga.ecart;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText user_log,pass_log;
    private TextView backreg;
    private Button signin ;
    public static final String PREF_NAME="user_name";
    public SharedPreferences mPref;
    SharedPreferences.Editor editor;
    private FirebaseAuth mauth;

    private ProgressDialog mdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPref=getSharedPreferences(PREF_NAME,MODE_PRIVATE);
        String user=mPref.getString("uID","");

        if(user.length()>0)
        {
            Intent i=new Intent(MainActivity.this,LoadingScreen.class);
            startActivity(i);
            finish();
        }

        mauth=FirebaseAuth.getInstance();
        mdialog=new ProgressDialog(this);

        user_log=(EditText)findViewById(R.id.user_log);
        pass_log=(EditText)findViewById(R.id.pass_log);
        signin=(Button)findViewById(R.id.signin);
        backreg= (TextView) findViewById(R.id.backreg);


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= user_log.getText().toString().trim();
                String pass= pass_log.getText().toString().trim();

                if(TextUtils.isEmpty(email))
                {
                    user_log.setError("Username can't be eempty");
                    return;
                }
                else if(TextUtils.isEmpty(pass))
                {
                    pass_log.setError("Fill the password");
                    return;
                }
                else
                {
                    mdialog.setMessage("Logging in...");
                    mdialog.show();

                    mauth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){


                                editor=mPref.edit();
                                editor.putString("uID",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                editor.apply();

                                Toast.makeText(getApplicationContext(),"Successfull",Toast.LENGTH_SHORT).show();
                                //mdialog.dismiss();
                                startActivity(new Intent(getApplicationContext(),LoadingScreen.class));
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Invalid username or password",Toast.LENGTH_SHORT).show();
                                mdialog.dismiss();
                            }
                        }
                    });
                }

            }
        });

        backreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
                finish();
            }
        });

    }

    public void logout()
    {

       // Toast.makeText(getApplicationContext(),"get",Toast.LENGTH_SHORT).show();
        /*editor.putString("uID","");
        editor.apply();
        mdialog.dismiss();*/
    }
}
