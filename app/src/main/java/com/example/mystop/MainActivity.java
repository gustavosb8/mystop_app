package com.example.mystop;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity  {

    private Button btnGoogle;
    private Button btnDigital;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.btnGoogle = findViewById(R.id.bntGoogle);
        this.btnDigital = findViewById(R.id.btnDigital);
    }

}
