package com.example.mystop;

import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Locale;

import model.Estacao;
import model.Linha;
import util.GeofenceRegistrationService;

public class DiarioDeBordo extends AppCompatActivity implements TextToSpeech.OnInitListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //Informações tela anterior
    private Estacao estacaoOrigem;
    private Estacao estacaoDestino;
    private Linha linha;
    private static final String TAG = "DiarioDeBordo";

    //Variáveis de layout
    private TextView txtOrigem;
    private TextView txtLinhaEscolhida;
    private TextView txtDestino;
    private ImageButton btnReproduzir;
    private ImageButton btnNotificar;

    //Text to speech
    private TextToSpeech tts;
    private static String speed = "Normal";
    private String frase;

    //Geofencing
    private GeofenceRegistrationService geofenceRegistrationService;
    private GoogleApiClient googleApiClient;
    private GeofencingRequest geofencingRequest;
    private PendingIntent pendingIntent;
    private boolean isMonitoring = false;

    //Geofence constants
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 101;
    public String GEOFENCE_ID;
    public static final float GEOFENCE_RADIUS_IN_METERS = 100;
    public HashMap<String, LatLng> AREA_LANDMARKS;


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

        //Location monitoring
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
    }

    private void startLocationMonitor() {
        Log.d(TAG, "start location monitor");
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(2000)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "Location Change Lat Lng " + location.getLatitude() + " " + location.getLongitude());
                }
            });
        } catch (SecurityException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private void startGeofencing() {
        Log.d(TAG, "Start geofencing monitoring call");
        pendingIntent = getGeofencePendingIntent();
        geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
                .addGeofence(getGeofence())
                .build();

        if (!googleApiClient.isConnected()) {
            Log.d(TAG, "Google API client not connected");
        } else {
            try {
                LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, pendingIntent)
                        .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.d(TAG, "Successfully Geofencing Connected");
                        } else {
                            Log.d(TAG, "Failed to add Geofencing " + status.getStatusCode());
                        }
                    }
                });
            } catch (SecurityException e) {
                Log.d(TAG, e.getMessage());
            }
        }
        isMonitoring = true;
        invalidateOptionsMenu();
    }

    private Geofence getGeofence() {
        LatLng latLng = this.AREA_LANDMARKS.get(this.GEOFENCE_ID);
        return new Geofence.Builder()
                .setRequestId(this.GEOFENCE_ID)
                .setExpirationDuration(18000000) //5 horas
                .setCircularRegion(latLng.latitude, latLng.longitude, this.GEOFENCE_RADIUS_IN_METERS)
                .setNotificationResponsiveness(1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(this, GeofenceRegistrationService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    private void stopGeoFencing() {
        pendingIntent = getGeofencePendingIntent();
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, pendingIntent)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess())
                            Log.d(TAG, "Stop geofencing");
                        else
                            Log.d(TAG, "Not stop geofencing");
                    }
                });
        isMonitoring = false;
        invalidateOptionsMenu();
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

        GEOFENCE_ID = "MyStop Geofence";

        AREA_LANDMARKS = new HashMap<String, LatLng>();

        //AREA_LANDMARKS.put(GEOFENCE_ID, new LatLng(estacaoDestino.getLatitude(), estacaoDestino.getLongitude()));

        AREA_LANDMARKS.put(GEOFENCE_ID, new LatLng(-2.533336, -44.246554));
    }

    private void notificar() {
        //TO DO
        /*
         * Habilitar notificação quando usuário entrar no raio de 100m da estação.
         * */

        if (!isMonitoring){
            startGeofencing();
        }else{
            stopGeoFencing();
        }

        Toast.makeText(this, "Desça a 100 metros da estação: " + estacaoDestino.getDescricao(), Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void reproduzir() {

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
        if(this.googleApiClient != null){
            this.googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(this.googleApiClient != null){
            this.googleApiClient.disconnect();
        }
    }

    public void onPause() {
        super.onPause();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    protected void onResume() {
        super.onResume();
        int response = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(DiarioDeBordo.this);
        if (response != ConnectionResult.SUCCESS) {
            Log.d(TAG, "Google Play Service Not Available");
            GoogleApiAvailability.getInstance().getErrorDialog(DiarioDeBordo.this, response, 1).show();
        } else {
            Log.d(TAG, "Google play service available");
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Google Api Client Connected");
        isMonitoring = true;
        startGeofencing();
        startLocationMonitor();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Google Connection Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        isMonitoring = false;
        Log.e(TAG, "Connection Failed:" + connectionResult.getErrorMessage());
    }
}