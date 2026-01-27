package com.casey.applyflow.domain;

import com.casey.applyflow.domain.enums.Status;

public class Application {
    private String title;
    private String url;
    private Status status;
    private Company company;
    private Interview interview;

    public Application(String title, String url, Status status, Company company, Interview interview) {
        this.title = title;
        this.url = url;
        this.status = status;
        this.company = company;
        this.interview = interview;
    }

    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }
}
