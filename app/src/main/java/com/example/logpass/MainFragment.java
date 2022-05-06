package com.example.logpass;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Intent;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logpass.DB.AppDB;
import com.example.logpass.adapters.MyListAdapter;

import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {

    private static List<TaskItem> items = new ArrayList<>();
    View view;
    private RecyclerView rv;
    private FloatingActionButton add;
    private MyListAdapter listAdapter;
    protected static String CHANNEL_ID = "Main channel";
    static PendingIntent contentIntent;
    DatabaseReference mDataBase;
    private AppDB database;
    int count;
    ValueEventListener vListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(items.size() > 0)
                items.clear();
                for (DataSnapshot ds : snapshot.child(AccountFragment.user.getUid()).getChildren()) {
                    ds.child("Tasks").child(AccountFragment.user.getUid()).child(ds.getKey());
                    TaskItem tItem = new TaskItem(ds.child("date").getValue(String.class), ds.child("time").getValue(String.class), ds.child("task").getValue(String.class), ds.child("enable").getValue(String.class), ds.child("id").getValue(String.class), ds.child("description").getValue(String.class));
                    if(!ds.getKey().equals("items") && !ds.getKey().equals("archive"))
                        items.add(tItem);
                        //updateItem(tItem);
                }
                setManagerAndAdapter();
                listAdapter.notifyDataSetChanged();
            }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            mDataBase.addValueEventListener(vListener);
        }
    };

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.archive_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(AccountFragment.isSign()) {
            getFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragment_container, new ArchiveFragment()).commit();
            getActivity().setTitle("EasyTodo: Архив");
        }
        else
            Toast.makeText(getContext(), "Войдите в аккаунт", Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
        Intent intent = new Intent(getContext(), MainActivity.class);
        contentIntent = PendingIntent.getActivity(getContext(),
                0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        createNotificationChannel();
        init();

        if(AccountFragment.isSign()) {
            mDataBase = FirebaseDatabase.getInstance().getReference("Tasks");
            mDataBase.addValueEventListener(vListener);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TaskEditDialogMD.display(getFragmentManager());
                    //getFragmentManager().beginTransaction().replace(R.id.fragment_container, new TaskCreateFragmentMD()).commit();
                }

            });
        }
        else
            Toast.makeText(getContext(), "Пожалуйста, войдите в аккаунт или зарегестрируйтесь", Toast.LENGTH_LONG).show();
        return view;
    }
    private void init(){
        add = (FloatingActionButton) view.findViewById(R.id.addTask);
        rv = view.findViewById(R.id.recycler_create);
    }
    private void createNotificationChannel() {
            CharSequence name = "Tasks";
            String description = "Task";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
    }
    private void setManagerAndAdapter() {
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        listAdapter = new MyListAdapter(getContext(), getActivity(), false);
        listAdapter.setItems(items);
        rv.setAdapter(listAdapter);
    }
    public static void setItems(List<TaskItem> items1){
        items = items1;
    }


    /*
    public void getList(){
        Thread thread = new Thread(new Runnable() {
            public void run() {
                List<TaskItem> itemList = database.twitDao().getAll();
                items = itemList;
            }
        });
        thread.start();
    }

    public void addItem(TaskItem item){
        items.add(item);
        //notifyDataSetChanged();
        Thread thread = new Thread(new Runnable() {
            public void run() {
                database.twitDao().insert(item);
            }
        });
        thread.start();
    }

    private void updateItem(TaskItem item){
        Thread thread = new Thread(new Runnable() {
            public void run() {
                database.twitDao().update(item);
            }
        });
        thread.start();
    }
    */
}
        /*
        taskRecycle.setLayoutManager(layoutManager);
        taskRecycle.setHasFixedSize(true);
            taskAdapter = new TaskAdapter(TaskCreateFragment.pref.getInt("number",0));
            taskRecycle.setAdapter(taskAdapter);

         */