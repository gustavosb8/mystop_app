package adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mystop.R;

public class ItemHolder extends RecyclerView.ViewHolder{

    public TextView txtid;
    public TextView txtlinha;
    public TextView txtdetalhes;
    public ImageButton btnIr;

    public ItemHolder(@NonNull View itemView) {
        super(itemView);
        txtid = itemView.findViewById(R.id.txtID);
        txtlinha = itemView.findViewById(R.id.txtLinha);
        txtdetalhes = itemView.findViewById(R.id.txtDetalhes);
        btnIr = itemView.findViewById(R.id.btnIr);
        
    }

}
