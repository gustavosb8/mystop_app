package rest;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class WebService extends AsyncTask<Void, Void, Object> {

    private Object objeto;
    private String url;

    public WebService(Object objeto){
        this.objeto = objeto;
    }

    @Override
    protected String doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(this.getUrl());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-type", "application/json"); //fala o que vai mandar
            urlConnection.setDoOutput(true); //fala que voce vai enviar algo

            PrintStream printStream = new PrintStream(urlConnection.getOutputStream());
            printStream.println(this.objeto); //seta o que voce vai enviar

            urlConnection.connect(); //envia para o servidor

            return new Scanner(urlConnection.getInputStream()).next(); //pega resposta
        } catch (Exception e) {
            e.printStackTrace();
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }





    public String execute(String url){
        this.setUrl(url);
        return this.doInBackground();
    }

    public Object getObjeto() {
        return objeto;
    }

    public void setObjeto(Object objeto) {
        this.objeto = objeto;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
