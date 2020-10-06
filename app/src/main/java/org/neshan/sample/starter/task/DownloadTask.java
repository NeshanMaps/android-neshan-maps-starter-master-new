package org.neshan.sample.starter.task;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask extends AsyncTask<String, Void, JSONObject> {
    // hold reference of activity to emit messages
    private Callback callback;

    public DownloadTask(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        String response = downloadRawFile(strings[0]);
        try {
            // convert raw to JSON object
            return new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        this.callback.onJsonDownloaded(jsonObject);
    }

    // download file from link (in this sample link is https://api.neshan.org/points.geojson)
    private String downloadRawFile(String link) {
        StringBuilder response = new StringBuilder();
        try {
            //Prepare the URL and the connection
            URL u = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //Get the Stream reader ready
                BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()),8192);
                //Loop through the return data and copy it over to the response object to be processed
                String line;
                while ((line = input.readLine()) != null) {
                    response.append(line);
                }
                input.close();
            }
            return response.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public interface Callback{
        void onJsonDownloaded(JSONObject jsonObject);
    }
}
