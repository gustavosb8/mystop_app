package model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Estacao implements Serializable {

    @SerializedName("id")
    private Integer id;

    @SerializedName("descricao")
    private String descricao;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("bairro")
    private String bairro;

    @SerializedName("referencia")
    private String referencia;

    @SerializedName("ativo")
    private Integer ativo;

    public Integer getId() {
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Integer getAtivo() {
        return this.ativo;
    }

    public void setAtivo(Integer ativo) {
        this.ativo = ativo;
    }

    public String toString(){
        return
                "id: "+this.id+"\n"+
                "descricao: "+this.descricao+"\n";

    }
}
