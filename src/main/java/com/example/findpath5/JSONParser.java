package com.example.findpath5;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

/*
 *Authors: Hanlin Wang s2171010
 *
 *
 *Description:    In this part, it mainly create JsonParser to prepare
 *                to parse JSON data from urls.
 *
 *
 * */


public class JSONParser {
    static String readStream(InputStream is) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, StandardCharsets.ISO_8859_1), 8);
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
            is.close();
        } catch (Exception e) {
            Log.e("readStream Exception", e.toString());
        }
        return(sb.toString());
    }

    //To get the stream of url
    public static String getStream(String url) {
        InputStream is = null;
        try {
            URL u = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) u.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            is = conn.getInputStream();
        } catch (UnsupportedEncodingException e) {
            Log.e("getStream Exception",  e.toString());
        } catch (Exception e) {
            Log.e("getStream Exception",  e.toString());
        }
        return readStream(is);
    }


    //data from urls
    public static JSONObject getJSONFromUrl(String url) {
        JSONObject jObj = null;
        try {
            jObj = new JSONObject(getStream(url));
        } catch (JSONException e) {
            Log.e("Exception",  e.toString());
        }
        return jObj;
    }

    public static JSONArray getJSONArrayFromUrl(String url) {
        JSONArray jArray = null;
        try {
            jArray = new JSONArray(getStream(url));
        } catch (JSONException e) {
            Log.e("Exception",  e.toString());
        }
        return jArray;
    }
}
