package com.activity.results.model;

public class UrlSpanInfo {

    private String text;
    private String url;

    public UrlSpanInfo(String text, String url) {
        this.text = text;
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public String getUrl() {
        return url;
    }
}
