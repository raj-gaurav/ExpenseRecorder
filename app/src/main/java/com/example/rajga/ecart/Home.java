package com.example.rajga.ecart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.rajga.ecart.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;


public class Home extends AppCompatActivity {

    private TextView totamt;
    Toolbar toolbar;

    private FloatingActionButton fab_btn;

    private DatabaseReference mDatabase;
    private FirebaseAuth mauth;

    String type,amount,note,postKey;

    String stotal;
    //Data data1;
    private RecyclerView recyclerView;

    public FirebaseRecyclerAdapter<Data,MyViewHolder>adapter;
    MainActivity mA=new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView=findViewById(R.id.recycler_home);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        //mDatabase.keepSynced(true);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Daily Expense Calculator");




        totamt=findViewById(R.id.total_amount);


        mauth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mauth.getCurrentUser();
        String uID=mUser.getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Shopping List").child(uID);
        fetch();

        //Total sum number

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int totalamount=0;
                //mauth=FirebaseAuth.getInstance();
                //FirebaseUser mUser=mauth.getCurrentUser();
                //String uID=mUser.getUid();


               // DataSnapshot  ds= dataSnapshot.child("Shopping List").child(uID);
                //Iterable<DataSnapshot> children=ds.getChildren();
                for(DataSnapshot snap:dataSnapshot.getChildren()){
                    //Log.d("data1--->","---------------------->"+uID);
                   // Log.d("Amount","----------------<Amount>-------------"+uID);

                    Log.d("Count","----------------<child count>-------------"+snap.child("amount").getValue());

                    //Data data1=snap.getValue(Data.class);
                    String amount= (String) snap.child("amount").getValue();
                    //Log.d("Amount","----------------<Amount>-------------"+amount);
                    totalamount += Integer.parseInt(amount) ;
                    stotal= String.valueOf(totalamount);
                    //Log.w("debug",stotal);
                    //snap.child("amount").getValue();

                }

                totamt.setText("Rs. " +   stotal);

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        fab_btn=findViewById(R.id.fab);

        fab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog();
            }
        });


    }



    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scan_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.scan)
        {
            Intent i=new Intent(Home.this,ScanProduct.class);
            startActivity(i);

        }
        else if(item.getItemId()==R.id.logout)
        {


            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            SharedPreferences mPref=getSharedPreferences("user_name",MODE_PRIVATE);


            SharedPreferences.Editor editor=mPref.edit();
            editor.putString("uID","");
            editor.apply();
            String user=mPref.getString("uID","");
            if(user.length()<=0)
            {

                ProgressDialog mdialog=new ProgressDialog(Home.this);
                mdialog.setMessage("Logging Out..");
                mdialog.show();
                Intent i=new Intent(Home.this,MainActivity.class);
                startActivity(i);
                finish();
                //mdialog.dismiss();

            }



        }

        return true;
    }

    private void customDialog(){

        AlertDialog.Builder myDialog= new AlertDialog.Builder(Home.this);

        LayoutInflater inflater=LayoutInflater.from(Home.this);
        View myView=inflater.inflate(R.layout.input_data,null);

        final AlertDialog dialog=myDialog.create();

        dialog.setView(myView);

        final EditText type=myView.findViewById(R.id.edt_type);
        final EditText amount=myView.findViewById(R.id.edt_amount);
        final EditText note=myView.findViewById(R.id.edt_note);
        final Button btnSave=myView.findViewById(R.id.btn_save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mType=type.getText().toString().trim();
                String mAmount=amount.getText().toString().trim();
                String mNote=note.getText().toString().trim();



                if(TextUtils.isEmpty(mType))
                {
                    type.setError("Required field...");
                    return;
                }

                else if(TextUtils.isEmpty(mAmount))
                {
                    type.setError("Required field...");
                    return;
                }


                else if(TextUtils.isEmpty(mNote))
                {
                    type.setError("Required field...");
                    return;
                }
                else
                {
                    String id=mDatabase.push().getKey();
                    String date= DateFormat.getDateInstance().format(new Date());
                    Data data=new Data(mType,mAmount,mNote,date,id);

                    mDatabase.child(id).setValue(data);

                    Toast.makeText(getApplicationContext(),"Data Addded", Toast.LENGTH_SHORT).show();

                    dialog.dismiss();
                }


            }
        });


        dialog.show();

    }



    private void fetch() {

        mauth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mauth.getCurrentUser();
        String uID=mUser.getUid();

        Query query= FirebaseDatabase.getInstance().getReference().child("Shopping List").child(uID);

        FirebaseRecyclerOptions<Data> options= new FirebaseRecyclerOptions.Builder<Data>().setQuery(query, new SnapshotParser<Data>() {
            @NonNull
            @Override
            public Data parseSnapshot(@NonNull DataSnapshot snapshot) {
                return new Data
                        (snapshot.child("type").getValue().toString().trim(),
                                snapshot.child("amount").getValue().toString().trim(),
                                snapshot.child("note").getValue().toString().trim(),
                                snapshot.child("date").getValue().toString().trim(),
                                snapshot.child("id").getValue().toString().trim());
            }
        }).build();

          adapter=new FirebaseRecyclerAdapter<Data,MyViewHolder>
               (options){


           @NonNull
           @Override
           public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
               View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_data,viewGroup,false);
               return new MyViewHolder(view);
           }

           @Override
           protected void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull final Data model) {
                holder.setAmount(model.getAmount());
                holder.setDate(model.getDate());
                holder.setNote(model.getNote());
                holder.setType(model.getType());

                holder.myview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        postKey=getRef(position).getKey();
                        type=model.getType();
                        note=model.getNote();
                        amount=model.getAmount();



                        updateData();
                    }
                });

           }


       };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
       // adapter.startListening();

    }

    @Override
    protected void onResume() {
        super.onResume();
        /*Intent i=new Intent(Home.this,LoadingScreen.class);
        startActivity(i);*/
    }

    @Override
    protected void onStop() {
        super.onStop();
       // adapter.stopListening();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View myview;

        public MyViewHolder(View itemView){
            super(itemView);
            myview=itemView;
        }

        public void setType(String type){
            TextView mtype=myview.findViewById(R.id.type);
            mtype.setText(type);
        }

        public void setNote(String note){
            TextView mnote=myview.findViewById(R.id.note);
            mnote.setText(note);
        }

        public void setDate(String date){
            TextView mdate=myview.findViewById(R.id.date);
            mdate.setText(date);
        }

        public void setAmount(String amount){
            TextView mamount=myview.findViewById(R.id.amount);
            mamount.setText("Rs." + amount);
        }

    }


    public void updateData()
    {
        AlertDialog.Builder myDialog= new AlertDialog.Builder(Home.this);
        LayoutInflater inflater=LayoutInflater.from(Home.this);

        View mView= inflater.inflate(R.layout.update_inputfield, null);

        final AlertDialog dialog=myDialog.create();

        dialog.setView(mView);

        final EditText edt_type=mView.findViewById(R.id.edt_type_udt);
        final EditText edt_amt=mView.findViewById(R.id.edt_amount_udt);
        final EditText edt_note=mView.findViewById(R.id.edt_note_udt);

        edt_amt.setText(amount);
        edt_amt.setSelection(amount.length());  //to put the cursor at the end of pre setted text
        edt_note.setText(note);
        edt_note.setSelection(note.length());
        edt_type.setText(type);
        edt_type.setSelection(type.length());

        final Button update=mView.findViewById(R.id.btn_update);
        final Button delete=mView.findViewById(R.id.btn_delete);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type=edt_type.getText().toString().trim();
                amount=edt_amt.getText().toString().trim();
                note=edt_note.getText().toString().trim();

                String date=DateFormat.getDateInstance().format(new Date());

                Data data=new Data(type,amount,note,date,postKey);
                mDatabase.child(postKey).setValue(data);

                dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Data updated successfully",Toast.LENGTH_SHORT);
              /*  final Snackbar snac=Snackbar.make(v, "Data updated successfully",Snackbar.LENGTH_SHORT);
               // snac.getView().setBackgroundColor(ContextCompat.getColor(v.getContext(),android.R.color.holo_blue_dark));
               // snac.setActionTextColor(getResources().getColor(android.R.color.holo_red_dark));
                snac.show();
                snac.setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        snac.dismiss();

                    }
                });*/



            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.child(postKey).removeValue();
                dialog.dismiss();
               Toast.makeText(getApplicationContext(),"Data deleted successfully",Toast.LENGTH_SHORT);

            }
        });

        dialog.show();



    }


}
