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

public class ArticleDownload extends AsyncTask<String, Integer, String> {

    private static final String TAG = "Article Download";
    String API_KEY = "6cf063a80d844844a46b78c24a841302";
    String ARTICLE_URL_PART1 = "https://newsapi.org/v2/everything?sources=";
    String ARTICLE_URL_PART2 = "&language=en&pageSize=100&apiKey="+API_KEY;

    private String sourceId;
    private NewsService newsService;
    private Uri.Builder buildURL = null;
    private StringBuilder stringBuilder;
    private ArrayList<Article> articleArrayList = new ArrayList <>();

    // Article Download constructor
    public ArticleDownload(NewsService service, String sourceId){
        Log.d(TAG, "ArticleDownload: STARTED");
        this.sourceId = sourceId;
        this.newsService = service;
        Log.d(TAG, "ArticleDownload: COMPLETED");
    }

    // Connect to API to get the data
    @Override
    protected String doInBackground(String... strings) {

        Log.d(TAG, "doInBackground: STARTED");
        String queryURL = ARTICLE_URL_PART1 + sourceId + ARTICLE_URL_PART2;
        buildURL = Uri.parse(queryURL).buildUpon();
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "URL is : "+urlToUse);
        stringBuilder = new StringBuilder();

        try {
            URL url = new URL(urlToUse);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if(connection.getResponseCode() != HttpURLConnection.HTTP_NOT_FOUND) {
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(inputStream)));
                String line;

                while ((line = bufferedReader.readLine()) != null)
                    stringBuilder.append(line).append('\n');

                Log.d(TAG, "doInBackground: string is "+(stringBuilder).toString() );
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
        if( s.length() > 0) {
            try {
                JSONObject jsonObject = new JSONObject( stringBuilder.toString() );
                JSONArray articlesArray = jsonObject.getJSONArray("articles");

                for(int article = 0; article < articlesArray.length(); article++) {

                    JSONObject articleJSON = (JSONObject) articlesArray.get(article);

                    Article articleObject = new Article();
                    articleObject.setArticleTitle( articleJSON.getString("title") );
                    articleObject.setArticlePublishDate( articleJSON.getString("publishedAt") );
                    articleObject.setArticleAuthor( articleJSON.getString("author") );
                    articleObject.setArticleImageURL( articleJSON.getString("urlToImage") );
                    articleObject.setArticleText( articleJSON.getString("description") );
                    articleObject.setArticleURL( articleJSON.getString("url") );

                    articleArrayList.add( articleObject );
                    Log.d(TAG, "onPostExecute article updated in the array list");

                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        newsService.UpdateArticles(articleArrayList);
        Log.d(TAG, "onPostExecute: COMPLETED");
    }

}
