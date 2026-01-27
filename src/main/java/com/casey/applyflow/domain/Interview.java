package com.casey.applyflow.domain;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Interview {
    private Date date;
    private String type;
    private Contact interviewer;
    private List<Note> notes = new ArrayList<>();

    public Interview(Date date, String type, Contact interviewer) {
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

    public Contact getInterviewer() {
        return interviewer;
    }

    public void setInterviewer(Contact interviewer) {
        this.interviewer = interviewer;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void addNote(Note note) {
        notes.add(note);
    }
}
