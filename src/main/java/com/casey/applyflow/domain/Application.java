package com.casey.applyflow.domain;

import com.casey.applyflow.domain.enums.Status;

public class Application {
    private String title;
    private String url;
    private Status status;

    public Application(String title, String url, Status status) {
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

    public Status getStatus() {
        return status;
    }
}
