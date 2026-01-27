package com.casey.applyflow.domain;

public class Application {
    private String title;
    private String url;
    private String status;

    public Application(String title, String url, String status) {
        this.title = title;
        this.url = url;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getStatus() {
        return status;
    }
}
