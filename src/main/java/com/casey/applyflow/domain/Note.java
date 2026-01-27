package com.casey.applyflow.domain;

public class Note {
    private String description;

    public Note(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
