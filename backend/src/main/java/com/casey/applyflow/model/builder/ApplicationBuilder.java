package com.casey.applyflow.model.builder;

import com.casey.applyflow.model.Application;
import com.casey.applyflow.model.Company;
import com.casey.applyflow.model.User;
import com.casey.applyflow.model.enums.Status;

public class ApplicationBuilder {
    private String title;
    private String url;
    private String location;
    private Status status;
    private Company company;
    private User user;

    public ApplicationBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public ApplicationBuilder withUrl(String url){
        this.url = url;
        return this;
    }
    public ApplicationBuilder withLocation(String location){
        this.location = location;
        return this;
    }

    public ApplicationBuilder withStatus(Status status){
        this.status = status;
        return this;
    }

    public ApplicationBuilder withCompany(Company company){
        this.company = company;
        return this;
    }

    public ApplicationBuilder withUser(User user){
        this.user = user;
        return this;
    }

    public Application build() {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL is required");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }
        if (user == null) {
            throw new IllegalArgumentException("User is required");
        }

        Application app = new Application(title, url, location, company, status);
        app.setUser(user);  // Set user after construction
        return app;
    }
}
