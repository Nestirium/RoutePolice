package dev.nest.rp.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WxApi {

    private final String API_KEY = "0dcd52b042b445f7beff2ee268";
    private final JSONParser parser;
    private final StringBuilder builder;

    public WxApi() {
        parser = new JSONParser();
        builder = new StringBuilder();
    }

    public String getMetar(String icao) throws IOException {
        final String BASE_URL = "https://api.checkwx.com/metar/%s";
        URL url = new URL(String.format(BASE_URL, icao));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("X-API-Key", API_KEY);
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        reader.lines().forEach(builder::append);
        String rawResult = builder.toString();
        builder.setLength(0);
        builder.trimToSize();
        try {
            Object json = parser.parse(rawResult);
            JSONObject jsonObject = (JSONObject) json;
            Object array = jsonObject.get("data");
            JSONArray jsonArray = (JSONArray) array;
            return (String) jsonArray.get(0);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

}
