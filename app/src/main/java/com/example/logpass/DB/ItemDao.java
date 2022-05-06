package com.example.logpass.DB;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.logpass.TaskItem;

import java.util.List;

@Dao
public interface ItemDao {

    //@Query("SELECT * FROM TaskItem")
    List<TaskItem> getAll();

    @Insert
    void insert(TaskItem item);

    @Update
    void update(TaskItem item);
}
