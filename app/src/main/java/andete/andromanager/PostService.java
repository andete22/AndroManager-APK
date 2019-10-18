package andete.andromanager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Instrumentation;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.inputmethodservice.Keyboard;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.StatFs;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PostService extends IntentService {
    ParseJSON datos;
    TTS voz;


    public PostService() {
        super("PostService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        datos = new ParseJSON();
        voz = new TTS(getApplicationContext());
        datos.setPath("/");
        try {
            while (true) {
                Thread.sleep(500);
                //enviarDatos();
                procesarDatos();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    Calendar fecha = new GregorianCalendar();
    int ano = fecha.get(Calendar.YEAR);
    int mes = fecha.get(Calendar.MONTH);
    int dia = fecha.get(Calendar.DAY_OF_MONTH);

    String log = dia + "-" + mes + "-" + ano + "\n";

String execCommand(String s){
    String salida="";
    try{

        Process process = Runtime.getRuntime().exec("su");
        process = Runtime.getRuntime().exec(s);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        salida = s + ": \n";
        while ((line = bufferedReader.readLine()) != null) {
            salida += line + "\n";
        }
        bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while ((line = bufferedReader.readLine()) != null) {
            Log.e("ERROR", line);
        }
    }catch (Exception e){
        Log.e("ERROR", e.getMessage());
    }
    return salida;
}

void procesarDatos(){
    Comandos c = ComandosDelServidor.sacarComando();
    if (c != null){

        Calendar fecha1 = new GregorianCalendar();
        int hora = fecha1.get(Calendar.HOUR_OF_DAY);
        int minuto = fecha1.get(Calendar.MINUTE);
        int segundo = fecha1.get(Calendar.SECOND);
        log += hora + ":" + minuto + ":" + segundo + " - " + c.comando.toString() + "(" + c.argumento + ")\n";
        Log.w("log", log);


    switch (c.comando){
        case command:
            String s = execCommand(c.argumento);
            tocarPantalla(500,500,s);
            break;
        case ls:
            datos.setPath(c.argumento);
            break;

        case getApps:
            ArrayList<Aplicaciones> apps = getListaAplicaciones();
            String sapps = "";
            for (Aplicaciones a : apps) {
                sapps = a.iconBase64 + "|" + a.nombre + "|" + a.packages + "|" + a.lock + "\n";
            }
            enviarAplicaciones(sapps);
            break;
        case download:
            new EnviarFicheros(c.argumento).enviar(getString(R.string.ip));
            break;
        case touch:
            String mensaje="Pulse aquí";
            String[] coord = c.argumento.split(",");
            if(coord.length>2){
                mensaje = coord[2];
            }
            tocarPantalla(Integer.parseInt(coord[0]), Integer.parseInt(coord[1]), mensaje);
            break;
        case open:
            Intent intent = getPackageManager().getLaunchIntentForPackage(datos.aplicacionNametoPackage(c.argumento));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            break;
        case inputText:
            // introducir texto
            KeyBoard.escribir(c.argumento);
            break;
        case mute:
            int v = Integer.parseInt(c.argumento);
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, v, 0);
            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, v, 0);
            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, v, 0);
            break;
        case pressBoton:
            Log.w("PressBoton", c.argumento);
            try{
                if (c.argumento.equals("menu")){
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
                    Log.w("PressBoton", "Entra");
                }
                if (c.argumento.equals("atras")){
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                }
            }catch (Exception e){
                Log.e("ERROR pressBoton", e.getMessage());
            }
            break;
        case darBaja:
            SharedPreferences preferences = getSharedPreferences("Usuario", 0);
            preferences.edit().clear().commit();
            Intent intent1 = new Intent(getApplicationContext(), Inicio.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
            stopSelf();
            break;
        default:
        }
    }
}

void enviarAplicaciones(String s){
    URL url = null;
    recopilarDatos();
    SharedPreferences spref = getSharedPreferences("Usuario",MODE_PRIVATE);
    String dispName = spref.getString("idDispositivo","noDisp");
    //Log.e("nombre", dispName);
    try {
        url = new URL("http://"+getString(R.string.ip)+"/receiveParams.php");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");

        // para activar el metodo post
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
        wr.writeBytes("datas="+s+"&name=apps"+ dispName);
        wr.flush();
        wr.close();
        InputStream is = urlConnection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        //StringBuffer response = new StringBuffer();
        while((line = rd.readLine()) != null) {
            ComandosDelServidor.addComandos(line);
        }
        rd.close();
        //Log.e("LO HACE", url.toString());
        //Log.e("NOS DA", response.toString());

    } catch (Exception e) {
        Log.e("SALIDA", "Sale por aquí: " + e.getMessage());
        e.printStackTrace();
    }

}


void tocarPantalla(final int x, final int y, final String mensaje){
    //Log.e("HA tocado la pantalla:", "X: " + x + ", Y: "+ y);
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
        @Override
        public void run() {
  /*
           LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    //WindowManager.LayoutParams.TYPE_INPUT_METHOD |
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,// | WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    PixelFormat.TRANSLUCENT);
            View layout = inflater.inflate(R.layout.touch,null);
            TextView text = (TextView) layout.findViewById(R.id.mensaje);
*/
            AudioManager audioManager1 = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager1.setStreamVolume(AudioManager.STREAM_MUSIC, 100, 0);
            audioManager1.setStreamVolume(AudioManager.STREAM_SYSTEM, 100, 0);
            audioManager1.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 100, 0);

            //text.setText(mensaje);
            voz.leerTexto(mensaje);
            /*
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.LEFT | Gravity.TOP, x, y);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
            */
        }
    });


}






String volumen(){
    AudioManager audioManager1 = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    int v1 = audioManager1.getStreamVolume(AudioManager.STREAM_MUSIC);
    int v2 = audioManager1.getStreamVolume(AudioManager.STREAM_SYSTEM);
    int v3 = audioManager1.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
    if (v1 == 0 || v2 == 0 || v3 == 0){
        return "off";
    }else{
        return "on";
    }
}


    void enviarDatos() {
        URL url = null;
        recopilarDatos();
        SharedPreferences spref = getSharedPreferences("Usuario",MODE_PRIVATE);
        String dispName = spref.getString("idDispositivo","noDisp");
        //Log.e("nombre", dispName);
        try {
            url = new URL("http://"+getString(R.string.ip)+"/receiveParams.php");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");

            // para activar el metodo post
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            wr.writeBytes("datas="+datos.parse()+"&name="+ dispName);
            wr.flush();
            wr.close();
            InputStream is = urlConnection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            //StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                ComandosDelServidor.addComandos(line);
            }
            rd.close();
            //Log.e("LO HACE", url.toString());
            //Log.e("NOS DA", response.toString());

        } catch (Exception e) {
            Log.e("SALIDA", "Sale por aquí: " + e.getMessage());
            e.printStackTrace();
        }
    }


    String getBateria() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent battery = getApplicationContext().registerReceiver(null, ifilter);
        int level = battery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = battery.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int status = battery.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        float batteryPct = (level / (float) scale)*100;
        return String.valueOf(batteryPct) + (isCharging ? "_1" : "_0");
    }

    ArrayList<Aplicaciones> getListaAplicaciones() {
        ArrayList<Aplicaciones> apps = new ArrayList<>();


        PackageManager pm = getPackageManager();
        Intent filterApp = new Intent(Intent.ACTION_MAIN);
        filterApp.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list_names = pm.queryIntentActivities(filterApp, PackageManager.GET_META_DATA);
        for (ResolveInfo packageInfo : list_names) {

            String name = (String) packageInfo.loadLabel(pm);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = ((BitmapDrawable)packageInfo.loadIcon(pm)).getBitmap();
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 4, bitmap.getHeight() / 4, false);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            String icon = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP | Base64.URL_SAFE);
            String pack = packageInfo.activityInfo.packageName;
            apps.add(new Aplicaciones(name,icon,pack,"0"));
            //Log.e("ICONO", icon);
        }
        return apps;
    }


    ArrayList<String> getUsoDisco() {
        ArrayList<String> salida = new ArrayList<>();
        StatFs stats_int = new StatFs(Environment.getRootDirectory().getAbsolutePath());// Memoria Interna
        StatFs stats_ext = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());// Memoria Externa

        long total_int = (stats_int.getBlockCountLong() * stats_int.getBlockSizeLong());
        long total_ext = (stats_ext.getBlockCountLong() * stats_ext.getBlockSizeLong());
        long free_int = (stats_int.getAvailableBlocksLong() * stats_int.getBlockSizeLong());
        long free_ext = (stats_ext.getAvailableBlocksLong() * stats_ext.getBlockSizeLong());

        salida.add(String.valueOf(total_int));
        salida.add(String.valueOf(total_ext));
        salida.add(String.valueOf(free_int));
        salida.add(String.valueOf(free_ext));

        return salida;
    }

    ArrayList<String> getGPS() {
        ArrayList<String> salida = new ArrayList<>();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null){
            salida.add(String.valueOf(location.getLatitude()));
            salida.add(String.valueOf(location.getLongitude()));
        }else{
            salida.add(String.valueOf(0));
            salida.add(String.valueOf(0));
        }

        return salida;
    }

    ArrayList<Ficheros> getFicheros(String path){
        ArrayList<Ficheros> list_direc = new ArrayList<>();
        File f = new File(path);
        File[] files = f.listFiles();

        for (int i = 0; files!= null && i < files.length; i++){
            File file = files[i];
            list_direc.add(new Ficheros(file.getName(), file.isDirectory() ? "dir" : "file"));

        }
        return list_direc;
    }

    String fechaActual(){
        return ""+new Date().getTime();
    }

    void recopilarDatos(){
        String[] bat = getBateria().split("_");
        datos.setNivelBateria(bat[0]);
        datos.setBateriaCargando(bat[1]);
        datos.setNumeroTelefono(DatosUsuario.obtenerIDDispositivo(getApplicationContext()));
        datos.setListaAplicaciones(getListaAplicaciones());
        ArrayList<String> disco = getUsoDisco();
        datos.setMemoriaInternaTotal(disco.get(0));
        datos.setMemoriaExternaTotal(disco.get(1));
        datos.setMemoriaInternaLibre(disco.get(2));
        datos.setMemoriaExternaLibre(disco.get(3));
        ArrayList<String> gps = getGPS();
        datos.setGPSLatitud(gps.get(0));
        datos.setGPSLongitud(gps.get(1));
        datos.setFecha(fechaActual());
        datos.setSonido(volumen());
        datos.setListaFicheros(getFicheros(datos.getPath()));
    }

}
