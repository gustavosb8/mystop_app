package com.example.mystop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;

import java.util.List;

import adapter.LinhaAdapter;
import model.Estacao;
import model.Linha;
import rest.MystropApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ListaLinhas extends AppCompatActivity {

    private TextView textViewResult;
    private Location location;
    private ConstraintLayout constraintLayout;
    private LazyLoader loader;
    private Estacao estacaoOrigem;
    private Estacao estacaoDestino;
    static final int ACTIVITY_2_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_linhas);
        textViewResult = findViewById(R.id.text_view_result);
        constraintLayout = findViewById(R.id.layoutcl);

        Intent intent  = getIntent();
        final Bundle parametros = intent.getExtras();
        //location = (Location) parametros.get("location");
        location = new Location("");
        location.setLatitude(-2.542440);
        location.setLongitude(-44.168301);
        //Toast.makeText(this, parametros.get("linha").toString(), Toast.LENGTH_SHORT).show();
        //textViewResult.setText("Code: "+parametros.get("linha").toString());


        try {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://mystopslz.com.br/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            MystropApi mystopApi = retrofit.create(MystropApi.class);

            /*trailingCircularDotsLoader
            this.trailingCircularDotsLoader = new TrailingCircularDotsLoader(
                    this,
                    24,
                    ContextCompat.getColor(this, android.R.color.holo_blue_bright),
                    100,
                    5);
            this.trailingCircularDotsLoader.setAnimDuration(1200);
            this.trailingCircularDotsLoader.setAnimDelay(200);
            this.trailingCircularDotsLoader.setX(400);
            this.trailingCircularDotsLoader.setY(650);*/

            loader = new LazyLoader(this, 30, 20, ContextCompat.getColor(this, android.R.color.holo_blue_bright),
                    ContextCompat.getColor(this, android.R.color.holo_green_light),
                    ContextCompat.getColor(this, android.R.color.holo_red_light));
            loader.setAnimDuration(400);
            loader.setFirstDelayDuration(100);
            loader.setSecondDelayDuration(200);
            loader.setInterpolator(new LinearInterpolator());
            loader.setX(400);
            loader.setY(650);

            constraintLayout.addView(this.loader);

            if (parametros.get("linha").toString().isEmpty()) {

                // Varias Linhas

                Call<List<Linha>> call = mystopApi.getLinhas();

                call.enqueue(new Callback<List<Linha>>() {
                    @Override
                    public void onResponse(Call<List<Linha>> call, Response<List<Linha>> response) {
                        if (!response.isSuccessful()) {
                            textViewResult.append("Code: " + response.code());
                            return;
                        }

                        constraintLayout.removeView(loader);

                        List<Linha> linhas = response.body();

                        /*
                        for (Linha linha : linhas) {
                            String conteudo = "";
                            conteudo += "ID: " + linha.getId() + "\n";
                            conteudo += "Descrição: " + linha.getDescricao() + "\n\n";

                            textViewResult.append(conteudo);
                        }
                         */


                    }

                    @Override
                    public void onFailure(Call<List<Linha>> call, Throwable t) {
                        textViewResult.append(t.getMessage());
                    }
                });


                /*
                String conteudo = "";
                conteudo += "\nNão foi possivel encontrar linhas para esse local.\n";
                textViewResult.append(conteudo);

                 */
            } else {

                getListaEstacoes(mystopApi, parametros.get("linha").toString());


            }

            constraintLayout.removeView(this.loader);

        }catch (Exception ex){
                textViewResult.append(ex.getMessage());
        }
    }

    public void getListaEstacoes(MystropApi api, String lugar){
        Call<List<Estacao>> call = api.getEstacoesByPlace(lugar,
                this.location.getLatitude(),
                this.location.getLongitude());

        call.enqueue(new Callback<List<Estacao>>() {
            @Override
            public void onResponse(Call<List<Estacao>> call, Response<List<Estacao>> response) {
                if (response.isSuccessful()) {
                    List<Estacao> estacaos = response.body();
                    estacaoOrigem = estacaos.get(0);
                    estacaoDestino = estacaos.get(1);
                    getListaLinhas(api, estacaoOrigem.getId(), estacaoDestino.getId());
                } else {
                    textViewResult.append("Code: " + response.code());
                    return;
                }
            }

            @Override
            public void onFailure(Call<List<Estacao>> call, Throwable t) {
                textViewResult.append(t.getMessage());
            }
        });
    }

    public void getListaLinhas(MystropApi api, int idOrigem, int idDestino){
        Call<List<Linha>> call = api.getLinhasPorEstacao(idOrigem, idDestino);
        call.enqueue(new Callback<List<Linha>>() {
            @Override
            public void onResponse(Call<List<Linha>> call, Response<List<Linha>> response) {
                if (response.isSuccessful()) {

                    List<Linha> linhas = response.body();

                    listarRecycler(linhas);

                    /*
                    for (Linha linha : linhas) {
                        String conteudo = "";
                        conteudo += "ID: " + linha.getId() + "\n";
                        conteudo += "Descrição: " + linha.getDescricao() + "\n\n";

                        textViewResult.append(conteudo);
                    }
                     */
                }

            }

            @Override
            public void onFailure(Call<List<Linha>> call, Throwable t) {

            }
        });
    }

    private void listarRecycler(List<Linha> linhas){
        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rvLinhas);

        // Create adapter passing in the sample user data
        LinhaAdapter adapter = new LinhaAdapter(linhas);
        // Attach the adapter to the recyclerview to populate items
        rvContacts.setAdapter(adapter);
        // Set layout manager to position the items
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

}
