package com.deepakyadav.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

public class NewsService extends Service {

        private static final String TAG = "NewsService";
        private ServiceReceiver serviceReceiver;
        private boolean serviceRunning = true;
        private ArrayList<Article> articleArrayList = new ArrayList<>();

        @Override
        public IBinder onBind (Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand (Intent intent,int flags, int startId) {
            serviceReceiver = new ServiceReceiver();
            IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_SERVICE);
            registerReceiver(serviceReceiver, intentFilter);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (serviceRunning) {
                        while (articleArrayList.isEmpty()) {
                            try {
                                Thread.sleep(250);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        Intent intent = new Intent();
                        intent.setAction(MainActivity.ACTION_NEWS_STORY);
                        intent.putExtra(MainActivity.ARTICLE_LIST, articleArrayList);
                        sendBroadcast(intent);
                        articleArrayList.clear();
                    }
                }
            }).start();

            return Service.START_STICKY;
        }


        // UpdateArticles
        public void UpdateArticles (ArrayList < Article > articleList) {
            Log.d(TAG, "UpdateArticles: STARTED");
            articleArrayList.clear();
            articleArrayList.addAll( articleList );
            Log.d(TAG, "UpdateArticles: COMPLETED");
        }

        // Service ServiceReceiver
        class ServiceReceiver extends BroadcastReceiver {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: STARTED");

                if( intent.getAction() == MainActivity.ACTION_SERVICE ) {
                    String sourceId = "";
                    String temp = "";
                    if (intent.hasExtra(MainActivity.SOURCE_ID)) {
                        sourceId = intent.getStringExtra(MainActivity.SOURCE_ID);
                        temp = sourceId.replaceAll(" ", "-");
                    }
                    new ArticleDownload(NewsService.this, temp).execute();
                }

                Log.d(TAG, "onReceive: COMPLETED");
            }
        }

        @Override
        public void onDestroy () {
            Log.d(TAG, "onDestroy: STARTED");
            serviceRunning = false;
            Log.d(TAG, "onDestroy: COMPLETED");
        }

    }
