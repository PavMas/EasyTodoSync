package com.example.logpass.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.logpass.DB.AppDB;
import com.example.logpass.MainActivity;
import com.example.logpass.R;
import com.example.logpass.classes.TaskItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TaskShowArchiveFragment extends Fragment {

    TextView date_tV, time_tV, del_task, task_tV, edit_task;

    private View view;

    AppDB database;

    TaskItem item;
    long delVer;

    Context context;

    SharedPreferences preferences;

    private DatabaseReference mDatabase;

    public TaskShowArchiveFragment(TaskItem item, Context context) {
        database = Room.databaseBuilder(context, AppDB.class, MainActivity.DATABASE_NAME)
                .build();
        this.item = item;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.task_show_fragment_arch, container, false);
        setHasOptionsMenu(true);
        init();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        task_tV.setText(item.task);
        date_tV.setText(item.date);
        time_tV.setText(item.time);
        edit_task.setText(item.description);
        mDatabase = FirebaseDatabase.getInstance().getReference("Tasks");
        del_task.setOnClickListener(v -> {
            preferences = requireActivity().getSharedPreferences("myprefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            delVer = System.currentTimeMillis();
            if(MainActivity.hasConnection(context)) {
                mDatabase.child(MainActivity.DATABASE_NAME).child(item.id).removeValue();
                mDatabase.child(MainActivity.DATABASE_NAME).child("version").setValue(delVer);
                //mDatabase.child(MainActivity.DATABASE_NAME).child("delete").child("ids").child(item.id).setValue(item.id);
            }
            editor.putLong(MainActivity.DATABASE_NAME+"version", delVer).apply();
            //editor.putLong("delTime", delVer).apply();
            deleteItem(item);
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.archive_menu_back, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.fragment_container, new ArchiveFragment()).commit();
            requireActivity().setTitle("EasyTodo: Архив");
        return super.onOptionsItemSelected(item);
    }

    private void init(){
        task_tV = view.findViewById(R.id.tV_taskView_arch);
        date_tV = view.findViewById(R.id.tV_dateView_arch);
        time_tV = view.findViewById(R.id.tV_timeView_arch);
        del_task = view.findViewById(R.id.delete_task_arch);
        edit_task = view.findViewById(R.id.eT_taskView_arch);
    }

    private void deleteItem(TaskItem item){
        Thread thread = new Thread(() -> {
            database.itemDao().delete(item);
            requireActivity().runOnUiThread(() -> ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.fragment_container, new ArchiveFragment()).commit());
        });
        thread.start();
    }
}
