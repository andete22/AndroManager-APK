package andete.andromanager;

/**
 * Created by andete on 19/06/17.
 */

public class Aplicaciones {
    public String nombre;
    public String iconBase64;
    public String packages;
    public String lock;

    public Aplicaciones(String nombre, String iconBase64, String packages, String lock) {
        this.nombre = nombre;
        this.iconBase64 = iconBase64;
        this.packages = packages;
        this.lock= lock;

    }
}
