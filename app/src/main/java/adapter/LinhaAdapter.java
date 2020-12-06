package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mystop.R;

import java.util.List;

import model.Linha;

public class LinhaAdapter extends RecyclerView.Adapter<LinhaAdapter.ViewHolder> {

    private List<Linha> mLinhas;

    public LinhaAdapter(List<Linha> linhas){
        mLinhas = linhas;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtLinha;
        public TextView txtLinhaDetalhe;
        public TextView txtPosicao;
        public ImageButton bntLinha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtLinha = itemView.findViewById(R.id.txtLinha);
            txtLinhaDetalhe = itemView.findViewById(R.id.txtDetalhes);
            txtPosicao = itemView.findViewById(R.id.txtID);
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
        txtId.setText(""+(Integer)(position+1));

        ImageButton btnLinha = holder.bntLinha;
    }

    @Override
    public int getItemCount() {
        return mLinhas.size();
    }


}
