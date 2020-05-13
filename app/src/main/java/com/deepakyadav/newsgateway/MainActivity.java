package com.deepakyadav.newsgateway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    // String constants for Bundles
    public static final String ACTION_SERVICE = "ACTION_SERVICE";
    public static final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    public static final String ARTICLE_LIST = "ARTICLE_LIST";
    public static final String SOURCE_ID = "SOURCE_ID";
    // Variables and DS
    private String newsSource;
    private int currentSourcePointer;
    private boolean serviceStatus = false;
    private boolean appState ;
    ArrayList<Drawer> drawerArrayList = new ArrayList<>();
    private HashMap<String, Source> sourceStore = new HashMap<>();
    private ArrayList<String> sourceList = new ArrayList <>();
    private ArrayList<Source> sourceArrayList = new ArrayList <>();
    private ArrayList<String> categoryList = new ArrayList <>();
    private List <FragmentsManager> fragmentManagerList = new ArrayList<>();
    private ArrayList<Article> articleArrayList = new ArrayList <>();
    // Objects and views
    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private ActionBarDrawerToggle drawerToggle;
    private ColorsAdapter adapter;
    private Menu optionsMenu;
    private NewsReceiver newsReceiver;
    private MyPageAdapter pageAdapter;
    private ViewPager viewPager;

    // onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: STARTED");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "savedInstanceState: "+ savedInstanceState);

        if( getSupportActionBar() != null ){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        newsReceiver = new NewsReceiver();
        drawerLayout = findViewById(R.id.drawerLayout);
        drawerListView = findViewById(R.id.drawerList);
        adapter = new ColorsAdapter(this, drawerArrayList);
        drawerListView.setAdapter( adapter );
        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(pageAdapter);

        // Start service if not started
        if ( savedInstanceState == null && !serviceStatus ){
            Intent intent = new Intent(MainActivity.this, NewsService.class);
            startService(intent);
            serviceStatus = true;
        }

        IntentFilter filter = new IntentFilter(MainActivity.ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, filter);

        // if no data is there to restore
        if (sourceStore.isEmpty() && savedInstanceState == null )
            new SourceDownload(this, "").execute();

        // add click listener to drawer list view
        drawerListView.setOnItemClickListener(new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        viewPager.setBackgroundResource(0);
                        currentSourcePointer = position;
                        selectListItem(position);
                    }
        });

        // update the drawer toggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_nav, R.string.close_nav);

        Log.d(TAG, "onCreate: COMPLETED");
    }

    // select item
    private void selectListItem(int position) {
        Log.d(TAG, "selectItem: STARTED");
        Log.d(TAG, "selected pos is : "+ position+" sourceList size is: "+ sourceList.size());
        newsSource = sourceList.get(position);
        Intent intent = new Intent(MainActivity.ACTION_SERVICE);
        intent.putExtra(SOURCE_ID, newsSource);
        sendBroadcast(intent);
        drawerLayout.closeDrawer(drawerListView);
        Log.d(TAG, "selectItem: COMPLETED");
    }

    // IMPORTANT for drawer
    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState){
        Log.d(TAG, "onPostCreate: STARTED");
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
        Log.d(TAG, "onPostCreate: COMPLETED");
    }

    // IMPORTANT for drawer
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged: STARTED");
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged: COMPLETED");
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: STARTED");
        if (drawerToggle.onOptionsItemSelected(item)) {  // <== Important!
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }

        new SourceDownload(this, item.getTitle().toString()).execute();
        drawerLayout.openDrawer( drawerListView );

        Log.d(TAG, "onOptionsItemSelected: COMPLETED");
        return super.onOptionsItemSelected(item);
    }

    // dynamically update the options menu from the category list
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: STARTED");
        getMenuInflater().inflate(R.menu.category_options, menu);
        optionsMenu = menu;
        if(appState){
            optionsMenu.add("all");
            for (String cat : categoryList)
                optionsMenu.add( cat );
        }
        Log.d(TAG, "onCreateOptionsMenu: COMPLETED");
        return super.onCreateOptionsMenu(menu);
    }

    public void init(ArrayList<Source> arrayListSourceList, ArrayList<String> arrayListCategoryList) {
        Log.d(TAG, "init: STARTED");
        Log.d(TAG, "arrayListSourceList size : "+arrayListSourceList.size());
        Log.d(TAG, "arrayListCategoryList size : "+arrayListCategoryList.size());
        sourceStore.clear();
        sourceList.clear();
        sourceArrayList.clear();
        drawerArrayList.clear();
        sourceArrayList.addAll(arrayListSourceList);

        for(int index = 0; index < arrayListSourceList.size(); index++){
            sourceList.add( arrayListSourceList.get(index).getSourceName());
            sourceStore.put( arrayListSourceList.get(index).getSourceName(), arrayListSourceList.get(index));
        }

        // Sort and update category list in the options menu
        if(!optionsMenu.hasVisibleItems()) {
            categoryList.clear();
            categoryList = arrayListCategoryList;
            optionsMenu.add("all");
            Collections.sort(arrayListCategoryList);
            for (String categry : arrayListCategoryList)
                optionsMenu.add( categry );
        }

        // Update the drawer
        for( Source s : arrayListSourceList ){
            Drawer drawerContent = new Drawer();
            drawerContent.setName(s.getSourceName());
            drawerArrayList.add( drawerContent );
            drawerContent.setName(s.getSourceName());
        }
        adapter.notifyDataSetChanged();

        Log.d(TAG, "init: COMPLETED");
    }

    private void updateFragments(ArrayList<Article> articles) {
        Log.d(TAG, "updateFragments: STARTED");
        setTitle(newsSource);

        for (int i = 0; i < pageAdapter.getCount(); i++)
            pageAdapter.notifyChangeInPosition(i);

        fragmentManagerList.clear();

        for (int article = 0; article < articles.size(); article++) {
            fragmentManagerList.add( FragmentsManager.newFragment(articles.get(article), article, articles.size()));
        }
        pageAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0);
        articleArrayList = articles;
        Log.d(TAG, "updateFragments: COMPLETED");

    }

    // EXTRA CREDIT: Save instance state when rotating
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: STARTED");
        LayoutManager layoutRestore = new LayoutManager();

        Log.d(TAG, "category array list: "+ categoryList);
        layoutRestore.setCategoriesArrayList(categoryList);

        Log.d(TAG, "sourceArrayList: "+ sourceArrayList);
        layoutRestore.setSourceArrayList(sourceArrayList);

        layoutRestore.setArticle( viewPager.getCurrentItem() );

        Log.d(TAG, "currentSourcePointer : "+ currentSourcePointer);
        layoutRestore.setSource(currentSourcePointer);

        Log.d(TAG, "articleArrayList : "+ articleArrayList);
        layoutRestore.setArticleArrayList(articleArrayList);

        outState.putSerializable("state", layoutRestore);
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: COMPLETED");
    }

    // EXTRA CREDIT: Restore instance state after rotation
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState: STARTED");
        super.onRestoreInstanceState(savedInstanceState);
        setTitle(R.string.app_name);
        LayoutManager layoutManager = (LayoutManager) savedInstanceState.getSerializable("state");
        appState = true;

        articleArrayList = layoutManager.getArticleArrayList();
        Log.d(TAG, "articleArrayList: "+ articleArrayList);

        categoryList = layoutManager.getCategoriesArrayList();
        Log.d(TAG, "category array list: "+ categoryList);

        sourceArrayList = layoutManager.getSourceArrayList();
        Log.d(TAG, "sourceArrayList: "+ sourceArrayList);

        for(int i=0; i<sourceArrayList.size(); i++){
            sourceList.add(sourceArrayList.get(i).getSourceName());
            sourceStore.put(sourceArrayList.get(i).getSourceName(), (Source)sourceArrayList.get(i));
        }

        drawerListView.clearChoices();
        adapter.notifyDataSetChanged();
        drawerListView.setOnItemClickListener(

                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        viewPager.setBackgroundResource(0);
                        currentSourcePointer = position;
                        selectListItem(position);
                    }
                }
        );
        Log.d(TAG, "onRestoreInstanceState: COMPLETED");
    }

    // on destroy
    @Override
    protected void onDestroy() {
        unregisterReceiver(newsReceiver);
        Intent intent = new Intent(MainActivity.this, NewsReceiver.class);
        stopService(intent);
        super.onDestroy();
    }

    class NewsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: STARTED");
            switch (intent.getAction()) {
                case ACTION_NEWS_STORY:
                    ArrayList<Article> artList;
                    if (intent.hasExtra(ARTICLE_LIST)) {
                        artList = (ArrayList <Article>) intent.getSerializableExtra(ARTICLE_LIST);
                        updateFragments(artList);
                    }
                    break;
            }
            Log.d(TAG, "onReceive: COMPLETED");
        }
    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;


        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentManagerList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentManagerList.size();
        }

        @Override
        public long getItemId(int position) {
            return baseId + position;
        }

        public void notifyChangeInPosition(int n) {
            baseId += getCount() + n;
        }


    }
}
