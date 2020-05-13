package com.deepakyadav.newsgateway;

import java.io.Serializable;
import java.util.ArrayList;

public class LayoutManager implements Serializable {

    private int source;
    private ArrayList<Source> sourceArrayList = new ArrayList<>();

    private int article;
    private ArrayList<Article> articleArrayList = new ArrayList<>();

    private ArrayList<String> categoriesArrayList = new ArrayList<>();

    // ===================================================================
    //                              Getters
    // ===================================================================
    public ArrayList<Source> getSourceArrayList() {
        return sourceArrayList;
    }

    public ArrayList<Article> getArticleArrayList() {
        return articleArrayList;
    }

    public ArrayList<String> getCategoriesArrayList() {
        return categoriesArrayList;
    }

    // ===================================================================
    //                              Setters
    // ===================================================================

    public void setSource(int source) {
        this.source = source;
    }

    public void setSourceArrayList(ArrayList<Source> sourceArrayList) {
        this.sourceArrayList = sourceArrayList;
    }

    public void setArticle(int article) {
        this.article = article;
    }

    public void setArticleArrayList(ArrayList<Article> articleArrayList) {
        this.articleArrayList = articleArrayList;
    }

    public void setCategoriesArrayList(ArrayList<String> categoriesArrayList) {
        this.categoriesArrayList = categoriesArrayList;
    }
}
