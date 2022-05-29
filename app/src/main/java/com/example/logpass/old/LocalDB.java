package com.example.logpass.old;

import android.content.Context;

import androidx.room.Room;

import com.example.logpass.DB.AppDB;
import com.example.logpass.classes.TaskItem;

import java.util.ArrayList;
import java.util.List;

public class LocalDB {

    protected List<TaskItem> list = new ArrayList<>();
    private AppDB database;

    public LocalDB(Context context){
        database = Room.databaseBuilder(context, AppDB.class, "TaskDB")
                .build();
    }

    public void getList(){
        Thread thread = new Thread(new Runnable() {
            public void run() {
                List<TaskItem> items = database.itemDao().getAll();
                list = items;
            }
        });
        thread.start();
    }

    public void addItem(TaskItem item){
        list.add(item);
        Thread thread = new Thread(new Runnable() {
            public void run() {
                database.itemDao().insert(item);
            }
        });
        thread.start();
    }

    private void updateItem(TaskItem item){
        Thread thread = new Thread(new Runnable() {
            public void run() {
                database.itemDao().update(item);
            }
        });
        thread.start();
    }

}

