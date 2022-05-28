package com.example.logpass.DB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.logpass.classes.TaskItem;

@Database(entities = {TaskItem.class}, version = 1)
public abstract class AppDB extends RoomDatabase {
    public abstract ItemDao itemDao();
}
