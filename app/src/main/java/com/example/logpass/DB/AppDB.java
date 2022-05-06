package com.example.logpass.DB;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.logpass.TaskItem;

//@Database(entities = {TaskItem.class}, version = 1)
public abstract class AppDB extends RoomDatabase {
public abstract ItemDao twitDao();
}
