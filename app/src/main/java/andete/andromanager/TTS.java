package andete.andromanager;

import android.annotation.TargetApi;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by andete on 21/06/17.
 */

public class TTS implements TextToSpeech.OnInitListener {

    TextToSpeech tts;
    void leerTexto(String texto){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(texto);
        } else {
            ttsUnder20(texto);
        }

    }

    public TTS(Context c) {
        this.tts = new TextToSpeech(c,this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(new Locale("es", "ES"));
            if (result == TextToSpeech.LANG_MISSING_DATA ||  result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "LENGUAJE NO ENCONTRADO");
            }else{
                Log.e("TTS","PASA POR AQUI" );
            }
        } else {
            Log.e("TTS", "NO SE PUEDE INICIALIZAR TTS");
        }
    }


    @SuppressWarnings("deprecation")
    private void ttsUnder20(String texto) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String texto) {
        String utteranceId=this.hashCode() + "";
        int n = tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null, null);
        Log.e("TTS-mensaje", ""+n);

    }

}
