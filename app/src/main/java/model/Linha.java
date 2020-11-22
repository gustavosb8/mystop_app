package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Linha {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("descricao")
    @Expose
    private String descricao;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
