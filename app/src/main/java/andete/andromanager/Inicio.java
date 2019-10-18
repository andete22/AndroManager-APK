package andete.andromanager;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class Inicio extends AppCompatActivity {
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    SurfaceView cameraView;
    TextView info;
    final int PERMISOS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        permisos();


        info = (TextView) findViewById(R.id.infoInicio);
        info.setText("Entre en "+ getResources().getString(R.string.ip)+" y escaneé el codigo QR.");
        SharedPreferences spref = getSharedPreferences("Usuario", MODE_PRIVATE);
        String url_share = spref.getString("idDispositivo", "noUser");
        if (!url_share.equals("noUser")) {
            Intent intent = new Intent(getApplicationContext(), Verificar.class);
            intent.putExtra("_ID", "yaTiene");
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(getApplicationContext(), Verificar.class);
            intent.putExtra("_ID", "1_1_andetebest@hotmail.com");
            startActivity(intent);
            finish();


/*
            // creo el detector qr
            barcodeDetector =
                    new BarcodeDetector.Builder(getApplicationContext())
                            .setBarcodeFormats(Barcode.QR_CODE)
                            .build();
            // preparo el detector de QR
            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {
                    Log.w("Hago el release", "RELEASE");
                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                    if (barcodes.size() != 0) {

                        String s = barcodes.valueAt(0).displayValue;
                        Log.e("HE LEIDO", s);
                        Intent intent = new Intent(getApplicationContext(), Verificar.class);
                        intent.putExtra("_ID", s);
                        startActivity(intent);
                        finish();
                    }
                    //barcodeDetector.release();
                }
            });

            // creo la camara principal
            cameraSource = new CameraSource
                    .Builder(getApplicationContext(), barcodeDetector)
                    .setRequestedPreviewSize(640, 480)
                    .build();
            cameraView = (SurfaceView) findViewById(R.id.camera_view);
            // listener de ciclo de vida de la camara

            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    // verifico si el usuario dio los permisos para la camara
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        try {
                            cameraSource.start(cameraView.getHolder());
                        } catch (IOException ie) {
                            Log.e("CAMERA SOURCE", ie.getMessage());
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "ERROR: Error de la camara", Toast.LENGTH_SHORT).show();
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
            */

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISOS:

                if (grantResults.length > 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                    onStart();
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        try {
                            cameraSource.start(cameraView.getHolder());
                        } catch (IOException ie) {
                            Log.e("CAMERA SOURCE", ie.getMessage());
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "ERROR: Error de la camara", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Para hacer funcionar AndroManager tiene que conceder todos los permisos.", Toast.LENGTH_LONG).show();
                    finish();
                }

                break;
            default:
                finish();
        }

    }

    void permisos(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS},
                        PERMISOS);

        }

        }

}