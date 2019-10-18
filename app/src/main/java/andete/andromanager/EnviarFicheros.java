package andete.andromanager;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * Created by andete on 14/06/17.
 */

public class EnviarFicheros {
    String imgBase64;
    String imgName;
    String imgRuta;

    public EnviarFicheros(String imgRuta) {
        this.imgRuta = imgRuta;
        String[] nombreFich = imgRuta.split("/");
        this.imgName = nombreFich[nombreFich.length - 1];
        this.imgBase64 = base64();
    }

    public String getImgBase64() {
        return imgBase64;
    }

    public void setImgBase64(String imgBase64) {
        this.imgBase64 = imgBase64;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }


    String base64() {
        File file = new File(imgRuta);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(bytes, Base64.URL_SAFE);
    }
    void enviar(String uri){
        enviarUnArchivo.execute(imgBase64, imgName, uri);
    }
    AsyncTask enviarUnArchivo = new AsyncTask() {

        @Override
        protected Object doInBackground(Object[] params) {
            String b64 = (String) params[0];
            String name = (String) params[1];

            URL url = null;
            try {
                url = new URL("http://"+(String) params[2]+"/downloadFile.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes("file=" + b64 + "&name=" + name);
                wr.flush();
                wr.close();
                InputStream is = urlConnection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response1 = new StringBuffer();
                while ((line = rd.readLine()) != null) {
                    response1.append(line);
                    response1.append('\r');
                }
                rd.close();
                Log.e("LO HACE 3", url.toString());
                Log.e("NOS DA 3", response1.toString());

            } catch (Exception e) {
                Log.e("SALIDA 3", "Sale por aqui: " + e.getMessage());
            }
            return null;
        }
    };
}