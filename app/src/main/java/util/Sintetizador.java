package util;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class Sintetizador implements TextToSpeech.OnInitListener {

    private Context contexto;
    private TextToSpeech tts;
    private static String speed="Normal";

    public Sintetizador(Context contexto){
        this.contexto = contexto;
        this.tts = new TextToSpeech(this.contexto, this);
    }

    public void falaParaTexto(){

    }

    private void setSpeed(){
        if(speed.equals("Very Slow")){
            tts.setSpeechRate(0.1f);
        }
        if(speed.equals("Slow")){
            tts.setSpeechRate(0.5f);
        }
        if(speed.equals("Normal")){
            tts.setSpeechRate(1.0f);//default 1.0
        }
        if(speed.equals("Fast")){
            tts.setSpeechRate(1.5f);
        }
        if(speed.equals("Very Fast")){
            tts.setSpeechRate(2.0f);
        }
        //for setting pitch you may call
        //tts.setPitch(1.0f);//default 1.0
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(new Locale("PT", "BR"));

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                Log.e("TTS", "#################  Language supported!    ###################");
            }

            Log.e("TTS", "#################  Initialization success  ##################");
            //this.falar("Bem vindo ao My Stop!");

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    public void falar(String texto) {

        //this.tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null);

        this.tts.speak(texto, TextToSpeech.QUEUE_ADD, null, null);


        /*
        if(!tts.isSpeaking()){

        }
        */
    }

    private void onPause(){
        if (tts != null) {
            //tts.stop();
            //tts.shutdown();
        }
    }
}
