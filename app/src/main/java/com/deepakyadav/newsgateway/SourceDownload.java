package com.deepakyadav.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SourceDownload  extends AsyncTask<String, Integer, String> {

    private static final String TAG = "Source Download";
    String API_KEY = "6cf063a80d844844a46b78c24a841302";
    String SOURCE_URL_PART1 = "https://newsapi.org/v2/sources?language=en&country=us&category=";
    String SOURCE_URL_PART2 = "&apiKey="+API_KEY;
    private StringBuilder stringBuilder;
    private String articleCategory;
    private String sourceURL;
    private ArrayList<Source> sourceArrayList = new ArrayList <>();
    private ArrayList<String> categoryArrayList = new ArrayList <>();
    private Uri.Builder buildURL = null;
    private MainActivity mainActivity;

    // Constructor
    public SourceDownload(MainActivity mainActivity, String category){
        Log.d(TAG, "SourceDownload: STARTED");
        this.mainActivity = mainActivity;

        if( category.equalsIgnoreCase("all") ||
                category.equalsIgnoreCase("")) {
            this.articleCategory = "";
            sourceURL ="https://newsapi.org/v2/sources?language=en&country=us&apiKey="+API_KEY;
        } else{
            sourceURL = SOURCE_URL_PART1 + category + SOURCE_URL_PART2;
            this.articleCategory = category;
        }
        Log.d(TAG, "SourceDownload: COMPLETED");
    }

    // connect to API and get data
    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: STARTED");

        buildURL = Uri.parse(sourceURL).buildUpon();
        String urlToUse = buildURL.build().toString();
        stringBuilder = new StringBuilder();
        try {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if(conn.getResponseCode() != HttpURLConnection.HTTP_NOT_FOUND) {
                conn.setRequestMethod("GET");
                InputStream inputStream = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(inputStream)));
                String line;

                while ((line = bufferedReader.readLine()) != null)
                    stringBuilder.append(line).append('\n');

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "doInBackground: COMPLETED");

        return stringBuilder.toString();
    }

    // Convert JSON data and add to array list
    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: STARTED");
        super.onPostExecute(s);

        if( s.length() > 0 ){
            try{
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray sourcesArray = jsonObject.getJSONArray("sources");
                    for(int index = 0; index < sourcesArray.length(); index++){
                        JSONObject sourceJSON = (JSONObject) sourcesArray.get( index );
                        Source source = new Source();
                        source.setSourceId( sourceJSON.getString("id"));
                        source.setSourceName( sourceJSON.getString("name"));
                        source.setSourceCategory( sourceJSON.getString("category"));
                        source.setSourceURL( sourceJSON.getString("url"));

                        sourceArrayList.add( source );
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }

            for(int sourceItem = 0 ; sourceItem < sourceArrayList.size() ; sourceItem++) {
                String sourceCat = sourceArrayList.get( sourceItem ).getSourceCategory();
                if( ! categoryArrayList.contains( sourceCat ) )
                    categoryArrayList.add( sourceCat );
            }

            mainActivity.init(sourceArrayList , categoryArrayList);
        }

        Log.d(TAG, "onPostExecute: COMPLETED");
    }

}
