package com.example.logpass.classes;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class TaskItem {
    @PrimaryKey
    @NonNull
    public String id;
    public String date;
    public String time;
    public String task;
    public String enable;
    public String description;
    public String done;
    public String edited;
    public String arch;
    @Ignore
    private boolean expandable;

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public TaskItem(String id, String date, String time, String task, String enable, String description, String done, String edited, String arch) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.task = task;
        this.enable = enable;
        this.description = description;
        this.done = done;
        this.edited = edited;
        this.arch = arch;
        this.expandable = false;
    }

    @Ignore
    public TaskItem(String date, String time, String task, String enable, String description, String done, String edited, String arch) {
        this.date = date;
        this.time = time;
        this.task = task;
        this.enable = enable;
        this.description = description;
        this.done = done;
        this.edited = edited;
        this.arch = arch;
    }


    @Ignore
    @NonNull
    @Override
    public String toString() {
        return date + ", " + task + ", " + time + ", " + id;
    }
}
