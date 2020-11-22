package util;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.List;

import model.Linha;
import rest.MystropApi;
import retrofit2.Call;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class BackgroundService extends IntentService {
    public BackgroundService(){
        super("BackgroundService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //get call from extra
        Bundle bundle = intent.getExtras();
        String operacao = bundle.getString("operacao");
        MystropApi mystopApi = (MystropApi) bundle.get("api");
        TextView textViewResult = (TextView) bundle.get("view");

        if (operacao.equals("getLinhasPorEstacao")) {
            try {
                Call<List<Linha>> call = mystopApi.getLinhasPorEstacao(4, 6);
                List<Linha> linhas = call.execute().body();
                for (Linha linha : linhas){
                    String conteudo = "";
                    conteudo += "ID: "+linha.getId() + "\n";
                    conteudo += "Descrição: "+linha.getDescricao()+"\n\n";

                    Log.d("Log", conteudo);
                    //textViewResult.append(conteudo);
                }

            } catch (Exception e) {
                Toast.makeText(this, "network failure :(", Toast.LENGTH_SHORT).show();
            }
        }


    }
}
