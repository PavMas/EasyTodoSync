package com.example.logpass;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

public class TaskItem {
    //private long id;
    public String date;
    public String time;
    public String task;
    public String enable;

    public String id;
    public String description;
    public String done;
/*
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
*/
    public TaskItem(String date, String time, String task, String enable, String id, String description) {
        this.date = date;
        this.task = task;
        this.time = time;
        //this.enable = Boolean.parseBoolean(enable);
        this.enable = enable;
        this.id = id;
        this.description = description;
    }

    public TaskItem(String date, String time, String task, String enable, String id, String description, String done) {
        this.date = date;
        this.time = time;
        this.task = task;
        this.enable = enable;
        this.id = id;
        this.description = description;
        this.done = done;
    }
}
