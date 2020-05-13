package com.deepakyadav.newsgateway;

import java.io.Serializable;

public class Article implements Serializable {

    String articleTitle;
    String articlePublishDate;
    String articleAuthor;
    String articleImageURL;
    String articleText;
    String articleURL;

    // ===================================================================
    //                              Getters
    // ===================================================================
    public String getArticleTitle(){
        return articleTitle;
    }

    public String getArticlePublishDate(){
        return articlePublishDate;
    }

    public String getArticleAuthor(){
        return articleAuthor;
    }

    public String getArticleImageURL() {
        return articleImageURL;
    }

    public String getArticleText() {
        return articleText;
    }

    public String getArticleURL() {
        return articleURL;
    }

    // ===================================================================
    //                              Setters
    // ===================================================================
    public void setArticleAuthor(String articleAuthor) {
        this.articleAuthor = articleAuthor;
    }

    public void setArticleImageURL(String articleImageURL) {
        this.articleImageURL = articleImageURL;
    }

    public void setArticlePublishDate(String articlePublishDate) {
        this.articlePublishDate = articlePublishDate;
    }

    public void setArticleText(String articleText) {
        this.articleText = articleText;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public void setArticleURL(String articleURL) {
        this.articleURL = articleURL;
    }

}
