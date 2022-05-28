package com.example.logpass.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.logpass.classes.TaskItem;

import java.util.List;

@Dao
public interface ItemDao {

    @Query("SELECT * FROM TaskItem")
    List<TaskItem> getAll();

    @Insert
    void insert(TaskItem item);

    @Update
    void update(TaskItem item);

    @Query("SELECT * FROM TaskItem WHERE id =:id1")
    TaskItem getById(String id1);

    @Delete
    void delete(TaskItem item);

    @Query("SELECT * FROM TaskItem WHERE arch = 'false'")
    List<TaskItem> getUncompleted();

    @Query("SELECT * FROM TaskItem WHERE arch = 'true'")
    List<TaskItem> getCompleted();

    @Query("SELECT id FROM TaskItem")
    List<String> getIds();

    @Query("DELETE FROM TaskItem WHERE id =:id")
    void deleteById(String id);
}
