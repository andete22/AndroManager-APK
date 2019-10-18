package andete.andromanager;

/**
 * Created by andete on 14/06/17.
 */


public class Comandos{

    Comandos(TiposArgumentos _comando, String _argumento){
        comando = _comando;
        argumento = _argumento;
    }


    public TiposArgumentos comando;
    public  String argumento;
}
