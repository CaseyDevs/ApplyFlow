package com.casey.applyflow.domain;

import java.sql.Date;

public class Interview {
    private Date date;
    private String type;
    private String interviewer;
    // Add notes

    public Interview(Date date, String type, String interviewer) {
        this.date = date;
        this.type = type;
        this.interviewer = interviewer;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInterviewer() {
        return interviewer;
    }

    public void setInterviewer(String interviewer) {
        this.interviewer = interviewer;
    }
}
