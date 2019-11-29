package com.example.rajga.ecart;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class ScanProduct extends AppCompatActivity {


    ImageView amazon,flipkart,zomato;
    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    Button srch;

    String intentData = "";
    boolean isEmail = false;
    int F=0;

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_product);



        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
        srch = findViewById(R.id.button3);
        amazon=(ImageView)findViewById(R.id.imageView2);
        flipkart=(ImageView)findViewById(R.id.imageView3);
        zomato=(ImageView)findViewById(R.id.imageView4);


        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Scan_Product");


        initialiseDetectorsAndSources();
        //srch on google start
        srch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (intentData.length() > 0) {
                    if (!isEmail) {
                        String url = "https://www.google.com";
                        try {
                            Uri uri = Uri.parse("googlechrome://navigate?url=" + url + "/search?q=" + intentData.toString());
                            Intent i = new Intent(Intent.ACTION_VIEW, uri);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        } catch (ActivityNotFoundException e) {
                            // Chrome is probably not installed
                        }
                    }
                }
            }
        });
        //srch on google end


        //srch on amazon start
        amazon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (intentData.length() > 0) {
                    if (!isEmail) {
                        String url = "https://www.amazon.com";
                        try {
                            Uri uri = Uri.parse("googlechrome://navigate?url=" + url + "/s?k=" + intentData.toString());
                            Intent i = new Intent(Intent.ACTION_VIEW, uri);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        } catch (ActivityNotFoundException e) {
                            // Chrome is probably not installed
                        }
                    }
                }
            }
        });
        //srch on amazon end

        //srch on flipkart start
        flipkart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (intentData.length() > 0) {
                    if (!isEmail) {
                        String url = "https://www.flipkart.com";
                        try {
                            Uri uri = Uri.parse("googlechrome://navigate?url=" + url + "/search?q=" + intentData.toString());
                            Intent i = new Intent(Intent.ACTION_VIEW, uri);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        } catch (ActivityNotFoundException e) {
                            // Chrome is probably not installed
                        }
                    }
                }
            }
        });
        //srch on flipkart end

        //srch on zomato start
        zomato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (intentData.length() > 0) {
                    if (!isEmail) {
                        String url = "https://www.zomato.com";
                        try {
                            Uri uri = Uri.parse("googlechrome://navigate?url=" + url + "/search?q=" + intentData.toString());
                            Intent i = new Intent(Intent.ACTION_VIEW, uri);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        } catch (ActivityNotFoundException e) {
                            // Chrome is probably not installed
                        }
                    }
                }
            }
        });
        //srch on zomato end

    }

    public void initialiseDetectorsAndSources() {

        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScanProduct.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ScanProduct.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {


                    txtBarcodeValue.post(new Runnable() {

                        @Override
                        public void run() {

                            if (barcodes.valueAt(0).email != null) {
                                txtBarcodeValue.removeCallbacks(null);
                                intentData = barcodes.valueAt(0).email.address;
                                txtBarcodeValue.setText(intentData);
                                isEmail = true;

                            } else {
                                isEmail = false;
                                intentData = barcodes.valueAt(0).displayValue;
                                txtBarcodeValue.setText(intentData);
                                Date currentTime = Calendar.getInstance().getTime();
                                /*if(F==0) {
                                    boolean r1 = db1.Insert(intentData, currentTime.toString());
                                    if (r1 == true) {
                                        Toast.makeText(getApplicationContext(), "inserted sucessfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "insertion failed", Toast.LENGTH_SHORT).show();
                                    }
                                    //Toast.makeText(getApplicationContext(),intentData,Toast.LENGTH_SHORT).show();
                                    F=1;
                                }*/
                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtBarcodeValue.setText("No BarCode Detected");
        initialiseDetectorsAndSources();
    }


}
