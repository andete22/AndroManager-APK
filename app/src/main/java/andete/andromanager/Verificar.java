package andete.andromanager;

import android.*;
import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Scanner;


public class Verificar extends AppCompatActivity {
    DatosUsuario du;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificar);
        Intent intent = getIntent();
        String message = intent.getStringExtra("_ID");
        TextView textView = (TextView) findViewById(R.id.infoVerificar);

        if (message.compareTo("yaTiene") == 0){
            SharedPreferences spref = getSharedPreferences("Usuario",MODE_PRIVATE);
            du = new DatosUsuario(spref.getString("nombreDispositivo","NO"),spref.getString("idUser","NO"),spref.getString("nameUser","NO"));
        }else{

            du = new DatosUsuario(message);
            registrarDispositivo.execute(du);
            SharedPreferences pref = getSharedPreferences("Usuario",MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("nombreDispositivo",du.getNombreDispositivo());
            editor.putString("idUser",du.getIdUser());
            editor.putString("nameUser",du.getNameUser());
            editor.commit();
        }
        textView.setText("El dispositivo \"" + du.getNombreDispositivo() + "\" esta ahora vinculado a la cuenta de \"" + du.getNameUser() + "\".");
        startService(new Intent(this, PostService.class));
        Intent intent1 = new Intent(this, RecordingActivity.class);
        if (Build.VERSION.SDK_INT > 20){
            startActivity(intent1);
        }
    }
    AsyncTask registrarDispositivo = new AsyncTask() {

        @Override
        protected Object doInBackground(Object[] params) {
            DatosUsuario du = (DatosUsuario) params[0];
            URL url = null;
            try {
                url = new URL("http://"+getString(R.string.ip)+"/dispositivos/newDisp.php?user=" + du.getIdUser() + "&name=" + du.getNombreDispositivo());
                Log.w("me conecto a ", url.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream response = urlConnection.getInputStream();
                Scanner scanner = new Scanner(response);
                String responseBody = scanner.useDelimiter("\\A").next();
                SharedPreferences pref = getSharedPreferences("Usuario",MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("idDispositivo", responseBody);
                editor.commit();



                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    };

}