package com.deepakyadav.newsgateway;

import java.io.Serializable;

public class Source implements Serializable {

    String sourceId;
    String sourceName;
    String sourceCategory;
    String sourceURL;

    // ===================================================================
    //                              Getters
    // ===================================================================
    public String getSourceCategory() {
        return sourceCategory;
    }

    public String getSourceName() {
        return sourceName;
    }

    // ===================================================================
    //                              Setters
    // ===================================================================
    public void setSourceCategory(String sourceCategory) {
        this.sourceCategory = sourceCategory;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }
}

