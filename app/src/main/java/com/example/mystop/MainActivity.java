package com.example.mystop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

import util.GooglePlacesAutocompleteAdapter;


public class MainActivity extends AppCompatActivity  implements OnItemClickListener {

    private EditText txtLinha;
    private String linha;
    private Location location;
    private LocationManager lm;
    private LocationRequest mLocationRequest;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private GoogleApiClient mGoogleApiClient;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private FusedLocationProviderClient fusedLocationClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private ImageView micButton;

    AutoCompleteTextView autoCompView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        autoCompView = findViewById(R.id.autoCompleteTextView);
        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        autoCompView.setOnItemClickListener(this);


        //final Button btnEnviar = findViewById(R.id.btnEnviar);
        micButton = findViewById(R.id.mic);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        createLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(this.mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                    }
                }
            }
        });

        try {

            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                                                            ) {

                this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                this.fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if(location != null){
                                    atualizaLocation(location);

                                }else{
                                    locationRequest = LocationRequest.create();
                                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                    locationRequest.setInterval(20 * 1000);

                                    locationCallback = new LocationCallback(){
                                        public void OnLocationResult(LocationResult locationResult){
                                            if (locationResult == null) {
                                                return;
                                            }

                                            //Toast.makeText(getApplicationContext(),locationResult.getLocations().toString(), Toast.LENGTH_LONG).show();

                                            for (Location location : locationResult.getLocations()) {
                                                if (location != null) {
                                                    atualizaLocation(location);
                                                    Toast.makeText(getApplicationContext(),"Latitude: "+location.getLongitude()+" | Longitude: "+location.getLongitude(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                    };
                                }

                            }
                        })

                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

                //Toast.makeText(this,"Latitude: "+this.location.getLongitude()+" | Longitude: "+this.location.getLongitude(), Toast.LENGTH_LONG).show();

            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)&&ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)&&ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.RECORD_AUDIO)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                         Manifest.permission.ACCESS_COARSE_LOCATION,
                                         Manifest.permission.RECORD_AUDIO},
                            MY_PERMISSIONS_REQUEST_LOCATION);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
                //Toast.makeText(this,"You are not worthy!!!", Toast.LENGTH_SHORT).show();
            }

            //Toast.makeText(this,"Latitude: "+this.location.getLongitude()+" | Longitude: "+this.location.getLongitude(), Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                micButton.setImageResource(R.drawable.ic_mic_black_off);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                autoCompView.setText(data.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        micButton.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    Toast.makeText(getApplicationContext(), "Deixa de ouvir", Toast.LENGTH_SHORT).show();
                    speechRecognizer.stopListening();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    micButton.setImageResource(R.drawable.ic_mic_black_24dp);
                    Toast.makeText(getApplicationContext(), "Começa ouvir", Toast.LENGTH_SHORT).show();
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return false;
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {

                case Activity.RESULT_OK:
                    Toast.makeText(this,"Aceitou!", Toast.LENGTH_SHORT).show();
                    break;

                case Activity.RESULT_CANCELED:
                    this.finish();
                    break;
            }
        }
    }

    public void enviar(View v){
        Intent intent = new Intent(this, ListaLinhas.class);
        intent.putExtra("linha", getTextFromView());
        intent.putExtra("location", this.location);
        startActivity(intent);
    }

    private String getTextFromView(){
        return this.autoCompView.getText().toString();
    }

    private void createLocationRequest() {
        this.mLocationRequest = new LocationRequest();
        this.mLocationRequest.setInterval(10000);
        this.mLocationRequest.setFastestInterval(5000);
        this.mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void atualizaLocation(Location location){
        this.location = location;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(this, ListaLinhas.class);
        intent.putExtra("linha", autoCompView.getText());
        intent.putExtra("location", this.location);
        startActivity(intent);

    }
}
