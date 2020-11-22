package rest;

import java.util.List;

import model.Estacao;
import model.Linha;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MystropApi {

    @GET("estacoes?operacao=buscaPorLugar")
    Call<List<Estacao>> getEstacoesByPlace(@Query("lugar") String lugar, @Query("latitude") double latitude, @Query("longitude") double longitude);

    @GET("linhas?operacao=busca")
    Call<Linha> getLinhaPorId(@Query("id") String id);

    @GET("linhas?operacao=buscaPorEstacao")
    Call<List<Linha>> getLinhasPorEstacao(@Query("estacaoOrigem")Integer id1, @Query("estacaoDestino") Integer id2);

    @GET("linhas?operacao=buscaTodos")
    Call<List<Linha>> getLinhas();

}
