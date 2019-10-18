package andete.andromanager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by andete on 13/06/17.
 */

public class ParseJSON {

    String nivelBateria;
    String bateriaCargando;
    String memoriaInternaLibre;
    String memoriaInternaTotal;
    String memoriaExternaLibre;
    String memoriaExternaTotal;
    String GPSLatitud;
    String GPSLongitud;
    String path;
    ArrayList<Ficheros> listaFicheros;
    ArrayList<Aplicaciones> listaAplicaciones;


    String numeroTelefono;
    String fecha;

    String sonido;

    public String getSonido() {
        return sonido;
    }

    public void setSonido(String sonido) {
        this.sonido = sonido;
    }

    public ArrayList<Aplicaciones> getListaAplicaciones() {
        return listaAplicaciones;
    }

    public void setListaAplicaciones(ArrayList<Aplicaciones> listaAplicaciones) {
        this.listaAplicaciones = listaAplicaciones;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getBateriaCargando() {
        return bateriaCargando;
    }

    public void setBateriaCargando(String bateriaCargando) {
        this.bateriaCargando = bateriaCargando;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public String parse(){
        String json = "{";
        json += "\"bat\" : \"" + nivelBateria + "\",";
        json += "\"apps\" : [";
        for (int i = 0 ; i < listaAplicaciones.size() ; i++) {
            json += "{ \"appname\" : \"" + listaAplicaciones.get(i).nombre + "\", \"appicon\" : \"" + listaAplicaciones.get(i).iconBase64 + "\"}";
            if (i != listaAplicaciones.size() -1){
                json += ",";
            }
        }
        json += "],";
        json += "\"path\" : \"" + path + "\",";
        json += "\"files\" : [";
        for (int i = 0 ; i < listaFicheros.size() ; i++) {
            json += "{\"name\" : \"" + listaFicheros.get(i).nombre + "\", \"type\" : \"" + listaFicheros.get(i).tipo + "\"}";
            if (i != listaFicheros.size() -1){
                json += ",";
            }
        }
        json += "],";
        json += "\"mem_int\" : {";
        json += "\"total\" : \"" + memoriaInternaTotal + "\",";
        json += "\"libre\" : \"" + memoriaInternaLibre + "\"";
        json += "},";
        json += "\"mem_ext\" : {";
        json += "\"total\" : \"" + memoriaExternaTotal + "\",";
        json += "\"libre\" : \"" + memoriaExternaLibre + "\"";
        json += "},";
        json += "\"coord\" : {";
        json += "\"latitud\" : \"" + GPSLatitud + "\",";
        json += "\"longitud\" : \"" + GPSLongitud + "\"";
        json += "},";
        json += "\"batChange\" : \"" + bateriaCargando + "\",";
        json += "\"numero\" : \"" + numeroTelefono + "\",";
        json += "\"fecha\" : \"" + fecha + "\",";
        json += "\"sonido\" : \"" + sonido + "\"";
        json += "}";

        return json;
    }

    public ParseJSON(){
        this.nivelBateria = "";
        this.memoriaInternaLibre = "";
        this.memoriaInternaTotal = "";
        this.memoriaExternaLibre = "";
        this.memoriaExternaTotal = "";
        this.GPSLatitud = "";
        this.GPSLongitud = "";
        this.path = "";
        this.listaFicheros = new ArrayList<>();
        this.listaAplicaciones = new ArrayList<>();
    }


    public String getNivelBateria() {
        return nivelBateria;
    }

    public void setNivelBateria(String nivelBateria) {
        this.nivelBateria = nivelBateria;
    }

    public String getMemoriaInternaLibre() {
        return memoriaInternaLibre;
    }

    public void setMemoriaInternaLibre(String memoriaInternaLibre) {
        this.memoriaInternaLibre = memoriaInternaLibre;
    }

    public String getMemoriaInternaTotal() {
        return memoriaInternaTotal;
    }

    public void setMemoriaInternaTotal(String memoriaInternaTotal) {
        this.memoriaInternaTotal = memoriaInternaTotal;
    }

    public String getMemoriaExternaLibre() {
        return memoriaExternaLibre;
    }

    public void setMemoriaExternaLibre(String memoriaExternaLibre) {
        this.memoriaExternaLibre = memoriaExternaLibre;
    }

    public String getMemoriaExternaTotal() {
        return memoriaExternaTotal;
    }

    public void setMemoriaExternaTotal(String memoriaExternaTotal) {
        this.memoriaExternaTotal = memoriaExternaTotal;
    }

    public String getGPSLatitud() {
        return GPSLatitud;
    }

    public void setGPSLatitud(String GPSLatitud) {
        this.GPSLatitud = GPSLatitud;
    }

    public String getGPSLongitud() {
        return GPSLongitud;
    }

    public void setGPSLongitud(String GPSLongitud) {
        this.GPSLongitud = GPSLongitud;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ArrayList<Ficheros> getListaFicheros() {
        return listaFicheros;
    }

    public void setListaFicheros(ArrayList<Ficheros> listaFicheros) {
        this.listaFicheros = listaFicheros;
    }
    public String aplicacionNametoPackage(String nombre){
        String pack = "";
        for (Aplicaciones nom: listaAplicaciones) {
            if (nom.nombre.equals(nombre)){
                pack = nom.packages;
            }
        }
        return pack;
    }
}
