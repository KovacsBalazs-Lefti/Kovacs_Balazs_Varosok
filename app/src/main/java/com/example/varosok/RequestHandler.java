package com.example.varosok;

import android.util.StringBuilderPrinter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class RequestHandler {
    private RequestHandler() {}

    // Backend és frontend közötti kommunikáció megvalósítása
    private static HttpURLConnection setupConnection(String url) throws IOException {
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.setRequestProperty("Accept", "application/json");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        return connection;
    }

    // Response lekérdezése
    private static Response getResponse(HttpURLConnection connection) throws IOException {
        // ResponseCode és kontent létrehozása
        int responseCode = connection.getResponseCode();
        InputStream inputStream;
        if (responseCode < 400) {
            inputStream = connection.getInputStream();
        } else {
            inputStream = connection.getErrorStream();
        }
        StringBuilder content = new StringBuilder();
        // Input streamből olvasok
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        // Ha nem üres a sor, akkor hozzáadja kontenthéz
        while ((line = reader.readLine()) != null) {
            content.append(line);
        }
        reader.close();
        inputStream.close();
        return new Response(responseCode, content.toString());
    }

    private static void addRequestBody(HttpURLConnection connection, String requestBody) throws IOException {
        connection.setRequestProperty("Content-Type", "application/json");
        //létrehozzuk az output streamet
        connection.setDoOutput(true);
        //letárolás
        OutputStream outputStream = connection.getOutputStream();
        //Bufferwriter amiben csinálunk egy új streamwritert
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
       // requestbody írásaa writerbe
        writer.write(requestBody);
        //véglegesítünk
        writer.flush();
       //bezárás
        writer.close();
        outputStream.close();
    }
    //get metódus létrehozása
    public static Response get(String url) throws IOException{
        //connection létrehozása
        HttpURLConnection connection = setupConnection(url);
        //a connection típust beállítjuk
        connection.setRequestMethod("GET");
        //response visszaadása
        return getResponse(connection);
    }
    public static Response post(String url, String requestBody) throws IOException{
        //connection létrehozása
        HttpURLConnection connection = setupConnection(url);
        //a connection típust beállítjuk
        connection.setRequestMethod("POST");
        //requestbody hozzáadása
        addRequestBody(connection, requestBody);
        //response visszaadása
        return getResponse(connection);
    }

   public static Response put (String url, String requestBody) throws IOException{
            //connection létrehozása
            HttpURLConnection connection = setupConnection(url);
            //a connection típust beállítjuk
            connection.setRequestMethod("PUT");
            //requestbody hozzáadása
            addRequestBody(connection, requestBody);
            //response visszaadása
            return getResponse(connection);
    }
    public static Response delete(String url) throws IOException{
        //connection létrehozása
        HttpURLConnection connection = setupConnection(url);
        //a connection típust beállítjuk
        connection.setRequestMethod("DELETE");
        //response visszaadása
        return getResponse(connection);
    }
}
