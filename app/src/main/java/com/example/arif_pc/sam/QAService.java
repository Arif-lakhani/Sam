package com.example.arif_pc.sam;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class QAService {

    protected int timeout = 10000;
    public String text;
    private Context context;

    public QAService(Context ctx, int timeout) {
        this.timeout = timeout;
        this.context = ctx;
    }

    public String getText() { return text; }



    private String processText(String s) {
        Log.e(s, "processText ");
        // delete all sentences except the first
    //    s = deleteSentences(s);

        Log.e(s, "processText ");

        // delete parenthetical
        s = s.replaceAll("\\(.*?\\)", "");

        // NEO not Jeannie
        int index = s.indexOf("Jeannie");
        if (index != -1) {
            s = s.substring(0,index) + "sam" + s.substring(index+"Jeannie".length());
        }int index1 = s.indexOf("Pannous");
        if (index1 != -1) {
            s = s.substring(0, index1) + "sam tech" + s.substring(index + "Pannous".length());
        }
       int index2 = s.indexOf("Myself");
        if (index2 != -1) {
            s = s.substring(0,index2) + "Arif" + s.substring(index2+"Myself".length());
        }

        return s;
    }

    public static String streamToString(InputStream is, String encoding)
            throws IOException {
        try {
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream outStream = new ByteArrayOutputStream(
                    buffer.length);
            int numRead;
            while ((numRead = is.read(buffer)) != -1) {
                outStream.write(buffer, 0, numRead);
            }
            return outStream.toString(encoding);
        } finally {
            is.close();
        }
    }

    public String runQA(String input, String location) {



        Log.i(input, "running QA now" );

        try {
            input = URLEncoder.encode(input, "UTF-8");
        } catch (Exception ex) {
            Log.e("",ex.getMessage());
        }
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

// Install the all-trusting trust manager
        try {

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {


        }

        int timeZoneInMinutes = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 1000 / 60;

        String url = "https://weannie.pannous.com/api?input=" + input
                + "&locale=en"
                + "&timeZone=" + timeZoneInMinutes
                + "&location=" + location
                + "&login=test-user";
        Log.e(url, "runQA ");

        String result = "";

        Log.i("", "launching URL connection");

        try {
            URLConnection conn = new URL(url).openConnection();
            Log.i("", "conn instantiated");
            conn.setDoOutput(true);
            Log.i("", "set do output done");
            conn.setReadTimeout(timeout);
            Log.i("", "setreadtimeout done");
            conn.setConnectTimeout(timeout);
            Log.i("", "setconnecttimeout done");
            result = streamToString(conn.getInputStream(), "UTF-8");
            Log.i("", "maybe a null pointer exception");
        } catch (Exception ex) {
            text = "Error: " + ex.getMessage();
           ex.printStackTrace();
            return text;
        }

        Log.i("", "finding answer now");

        try {

            if (result == null || result.length() == 0) {
                text = "Error processing answer";
                return text;
            }

            JSONArray outputJson = new JSONObject(result).getJSONArray("output");
            if (outputJson.length() == 0) {
                text = "Sorry, nothing found";
                return text;
            }

            JSONObject firstHandler = outputJson.getJSONObject(0);
            if (firstHandler.has("errorMessage") && firstHandler.getString("errorMessage").length() > 0) {
                throw new RuntimeException("Server side error: "
                        + firstHandler.getString("errorMessage"));
            }

            JSONObject actions = firstHandler.getJSONObject("actions");
            if (actions.has("say")) {
                Object obj = actions.get("say");
                if (obj instanceof JSONObject) {
                    JSONObject sObj = (JSONObject) obj;
                    text = sObj.getString("text");
                } else {
                    text = obj.toString();
                }
                text = processText(text);
            }
        } catch (Exception ex) {
            text = "Parsing error: " + ex.getMessage();
            Log.e("",text);
            return text;
        }
        return text;
    }
}
