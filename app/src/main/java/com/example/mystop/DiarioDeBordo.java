package com.example.mystop;

import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import model.Estacao;
import model.Linha;

public class DiarioDeBordo extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private Estacao estacaoOrigem;
    private Estacao estacaoDestino;
    private Linha linha;
    private static final String TAG = "DiarioDeBordo";
    private TextView txtOrigem;
    private TextView txtLinhaEscolhida;
    private TextView txtDestino;
    private ImageButton btnReproduzir;
    private ImageButton btnNotificar;
    private TextToSpeech tts;
    private static String speed = "Normal";
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 101;

    private String frase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diario_de_bordo);
        txtOrigem = findViewById(R.id.txtOrigem);
        txtLinhaEscolhida = findViewById(R.id.txtLinhaEscolhida);
        txtDestino = findViewById(R.id.txtDestino);
        btnReproduzir = findViewById(R.id.btnReproduzir);
        btnNotificar = findViewById(R.id.btnNotificar);
        this.tts = new TextToSpeech(this, this);

        getIncomingIntent();

        btnNotificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificar();
            }
        });

        btnReproduzir.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                reproduzir();
            }
        });
    }

    private void getIncomingIntent() {
        Log.d(TAG, "getIncomingIntent: Verificar extra");
        if (getIntent().hasExtra("origem") && getIntent().hasExtra("destino") && getIntent().hasExtra("linha")) {
            this.estacaoOrigem = (Estacao) getIntent().getSerializableExtra("origem");
            Log.d(TAG, "getIncomingIntent: Estação Origem: " + this.estacaoOrigem.getDescricao());
            this.estacaoDestino = (Estacao) getIntent().getSerializableExtra("destino");
            Log.d(TAG, "getIncomingIntent: Estação Destino: " + this.estacaoDestino.getDescricao());
            this.linha = (Linha) getIntent().getSerializableExtra("linha");
            Log.d(TAG, "getIncomingIntent: Linha: " + this.linha.getDescricao());
            setData();
        }
    }

    private void setData() {
        Log.d(TAG, "setData: Populando Diário de Bordo");
        txtOrigem.setText("Alcance a estação de embarque: " + this.estacaoOrigem.getDescricao());
        txtLinhaEscolhida.setText("Embarque na linha: " + this.linha.getDescricao());
        txtDestino.setText("Solicite parada na estação de destino: " + estacaoDestino.getDescricao());

        this.setFrase("Alcance a estação de embarque: " + this.estacaoOrigem.getDescricao() + "." +
                " Embarque na linha: " + this.linha.getDescricao() + "." +
                " Solicite parada na estação de destino: " + estacaoDestino.getDescricao());
    }

    private void notificar() {
        //TO DO
        /*
         * Habilitar notificação quando usuário entrar no raio de 100m da estação.
         * */


        Toast.makeText(this, "Desça a 100 metros da estação: " + estacaoDestino.getDescricao(), Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void reproduzir() {
        //TO DO
        /*
         * Habilitar voz para reproduzir frase.
         * */

        //Toast.makeText(this, this.frase, Toast.LENGTH_LONG).show();
        falar(this.frase);
    }

    public String getFrase() {
        return frase;
    }

    public void setFrase(String frase) {
        this.frase = frase;
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

    private void setSpeed() {
        if (speed.equals("Very Slow")) {
            tts.setSpeechRate(0.1f);
        }
        if (speed.equals("Slow")) {
            tts.setSpeechRate(0.5f);
        }
        if (speed.equals("Normal")) {
            tts.setSpeechRate(1.0f);//default 1.0
        }
        if (speed.equals("Fast")) {
            tts.setSpeechRate(1.5f);
        }
        if (speed.equals("Very Fast")) {
            tts.setSpeechRate(2.0f);
        }
        //for setting pitch you may call
        //tts.setPitch(1.0f);//default 1.0
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void falar(String texto) {

        //this.tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null);

        this.tts.speak(texto, TextToSpeech.QUEUE_ADD, null, null);


        /*
        if(!tts.isSpeaking()){

        }
        */
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void onPause() {
        super.onPause();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}