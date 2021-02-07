package adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mystop.DiarioDeBordo;
import com.example.mystop.R;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import model.Estacao;
import model.Linha;

import static android.content.ContentValues.TAG;

public class LinhaAdapter extends RecyclerView.Adapter<LinhaAdapter.ViewHolder> {

    private List<Linha> mLinhas;
    public Estacao estacaoOrigem;
    public Estacao estacaoDestino;
    private Context mContext;

    public LinhaAdapter(List<Linha> linhas, Estacao embarque, Estacao desembarque, Context context){
        mLinhas = linhas;
        estacaoOrigem = embarque;
        estacaoDestino = desembarque;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements TextToSpeech.OnInitListener{

        public TextView txtLinha;
        public TextView txtLinhaDetalhe;
        public TextView txtPosicao;
        public ImageButton bntLinha;
        public ImageButton parentLayout;
        public LinearLayout layoutLinha;
        private TextToSpeech tts;
        private String speed="Normal";

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtLinha = itemView.findViewById(R.id.txtLinha);
            txtLinhaDetalhe = itemView.findViewById(R.id.txtDetalhes);
            txtPosicao = itemView.findViewById(R.id.txtID);
            parentLayout = itemView.findViewById(R.id.btnIr);
            layoutLinha = itemView.findViewById(R.id.layLinha);
            this.tts = new TextToSpeech(mContext, this);

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

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void falar(String texto) {

            //this.tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null);

            this.tts.speak(texto, TextToSpeech.QUEUE_ADD, null, null);


        /*
        if(!tts.isSpeaking()){

        }
        */
        }

        public void onPause() {
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
        }
    }

    @NonNull
    @Override
    public LinhaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_linha, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull LinhaAdapter.ViewHolder holder, int position) {
        Linha linha = mLinhas.get(position);

        // Set item views based on your views and data model
        TextView txtLinha = holder.txtLinha;
        txtLinha.setText(linha.getDescricao());

        TextView txtDetalhe = holder.txtLinhaDetalhe;
        txtDetalhe.setText("Detalhe da Linha");

        TextView txtId = holder.txtPosicao;
        txtId.setText(""+linha.getId());

        ImageButton btnLinha = holder.bntLinha;

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: Clicou "+mLinhas.get(position).getDescricao());

                Intent intent = new Intent(mContext, DiarioDeBordo.class);
                intent.putExtra("origem", (Serializable) estacaoOrigem);
                intent.putExtra("destino", (Serializable) estacaoDestino);
                intent.putExtra("linha", (Serializable) mLinhas.get(position));

                mContext.startActivity(intent);

            }
        });

        holder.layoutLinha.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: Clicou no layout");
                //Toast.makeText(mContext, "Clicou no layout", Toast.LENGTH_LONG).show();
                holder.falar(mLinhas.get(position).getDescricao());


            }
        });

    }

    @Override
    public int getItemCount() {
        return mLinhas.size();
    }

}
