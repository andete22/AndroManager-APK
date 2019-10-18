package andete.andromanager;

import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by andete on 14/06/17.
 */

public class ComandosDelServidor {

    static ArrayList<Comandos> colaDeComandos = new ArrayList<>();

    public ComandosDelServidor() {
        this.colaDeComandos = new ArrayList<>();
    }
    static void addComandos(String c){
        String[] cs = c.split("\n");
        for (String a : cs) {
            String[] partes = a.split(Pattern.quote("("));
            String arg =partes[1].substring(0, partes[1].length()-1);
            colaDeComandos.add(new Comandos(TiposArgumentos.valueOf(partes[0]), arg));
        }

    }
    static Comandos sacarComando(){
        Comandos c = null;
        if (colaDeComandos.size() > 0){
            c = colaDeComandos.get(0);
            colaDeComandos.remove(0);
        }
        return c;
    }
}
