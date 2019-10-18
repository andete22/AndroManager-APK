package andete.andromanager;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by andete on 12/06/17.
 */

public class DatosUsuario {
    String nombreDispositivo; // db: disp->nombre
    String idUser; // db: user->id
    String nameUser; // db: user->usuario


    public DatosUsuario(String cadena) {
        String[] subCad = cadena.split("_");
        Log.w("Cadena", cadena);
        nombreDispositivo = subCad[0];
        idUser = subCad[1];
        nameUser = subCad[2];
    }

    public DatosUsuario(String nombreDispositivo, String idUser, String nameUser) {
        this.nombreDispositivo = nombreDispositivo;
        this.idUser = idUser;
        this.nameUser = nameUser;
    }

    static String obtenerIDDispositivo(Context appContext) {
        TelephonyManager tMgr = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(appContext, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(appContext, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        if (tMgr.getLine1Number() != null)
            return tMgr.getLine1Number();
        else
            return "";
    }

    public String getNombreDispositivo() {
        return nombreDispositivo;
    }

    public void setNombreDispositivo(String nombreDispositivo) {
        this.nombreDispositivo = nombreDispositivo;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }
}
