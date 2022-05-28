package com.example.logpass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Context;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.logpass.DB.AppDB;
import com.example.logpass.classes.TaskItem;
import com.example.logpass.fragments.AccountFragment;
import com.example.logpass.fragments.InFragment;
import com.example.logpass.fragments.MainFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static String DATABASE_NAME;
    private final String APP_PREFERENCES = "myprefs";

    private AppDB database;

    private static FirebaseUser user;
    private DatabaseReference mDatabase;

    SharedPreferences mPrefs;
    SharedPreferences.Editor editor;

    long ver;
    long verApp;

    ValueEventListener vListener;

    List<TaskItem> items;

    boolean notify;


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                verApp = mPrefs.getLong(DATABASE_NAME + "version", 0);
                if (ver != verApp)
                    if (verApp > ver) {
                        syncDB(1, true);
                        Toast.makeText(getApplicationContext(), "lDB -> cDB", Toast.LENGTH_SHORT).show();
                    } else {
                        syncDB(0, true);
                        Toast.makeText(getApplicationContext(), "cDB -> lDB", Toast.LENGTH_SHORT).show();
                    }
            }
            if (msg.what == 2)
                loadToDB(notify);

        }
    };
    @SuppressLint("StaticFieldLeak")
    protected static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("EasyTodo: Задачи");
        user = FirebaseAuth.getInstance().getCurrentUser();
        mPrefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        DATABASE_NAME = mPrefs.getString("dbname", "defaultDB");
        database = Room.databaseBuilder(this, AppDB.class, DATABASE_NAME).build();
        if (hasConnection(this)) {
            new syncThread().start();
            /*
            if (user != null) {
                if (mPrefs.contains(DATABASE_NAME + "version")) {
                    mDatabase = FirebaseDatabase.getInstance().getReference("Tasks");
                    mDatabase.child(DATABASE_NAME).child("version").get().addOnCompleteListener(task -> {
                        ver = task.getResult().getValue(Long.class);
                        handler.sendEmptyMessage(1);
                    });

                    int verApp = mPrefs.getInt("version", 0);
                    if(ver > verApp){
                        Toast.makeText(this, "Версия локальной бд меньше", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(this, "Версия облачной бд меньше", Toast.LENGTH_SHORT).show();
                    }



                }
            }
            */
        }

        //database = Room.databaseBuilder(this, AppDB.class, DATABASE_NAME).build();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();

        BottomNavigationView bottonNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottonNav.setOnNavigationItemSelectedListener(navListener);
/*
        bottonNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return false;
            }
        });

 */

        context = getApplicationContext();


    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        int anim = 0, anim1 = 0;
        Fragment selectedFragment = null;
        switch (item.getItemId()) {
            case R.id.page_1:
                setTitle("EasyTodo: Задачи");
                selectedFragment = new MainFragment();
                anim = R.anim.slide_in_left;
                anim1 = R.anim.slide_out_right;
                break;
            case R.id.page_2:
                if (user == null) {
                    setTitle("EasyTodo: Вход в аккаунт");
                    selectedFragment = new AccountFragment();
                } else {
                    setTitle("EasyTodo: Личный кабинет");
                    selectedFragment = new InFragment();
                }
                anim = R.anim.slide_in_right;
                anim1 = R.anim.slide_out_left;
                break;
        }
        assert selectedFragment != null;
        getSupportFragmentManager().beginTransaction().setCustomAnimations(anim, anim1).replace(R.id.fragment_container, selectedFragment).commit();
        return true;
    };

    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public void syncDB(int syncType, boolean notify) {
        this.notify = notify;
        Thread thread = new Thread(() -> {
            database = Room.databaseBuilder(this, AppDB.class, DATABASE_NAME).build();
            if (mDatabase == null)
                mDatabase = FirebaseDatabase.getInstance().getReference("Tasks");
            if (syncType == 1) {
                TaskItem item;
                List<TaskItem> items = database.itemDao().getAll();
                for (int i = 0; i < items.size(); i++) {
                    item = items.get(i);
                    mDatabase.child(DATABASE_NAME).child(item.id).setValue(item);
                }
                mDatabase.child(DATABASE_NAME).child("version").setValue(verApp);
            }
            if (syncType == 0) {
                items = new ArrayList<>();
                mDatabase.getRoot().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.child("Tasks").child(DATABASE_NAME).getChildren()) {
                            TaskItem tItem = new TaskItem(ds.child("id").getValue(String.class), ds.child("date").getValue(String.class), ds.child("time").getValue(String.class), ds.child("task").getValue(String.class), ds.child("enable").getValue(String.class), ds.child("description").getValue(String.class), ds.child("edited").getValue(String.class), ds.child("done").getValue(String.class), ds.child("arch").getValue(String.class));
                            if (!ds.getKey().equals("items") && !ds.getKey().equals("archive") && !ds.getKey().equals("version"))
                                items.add(tItem);
                        }
                        handler.sendEmptyMessage(2);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
        thread.start();
    }

    private void loadToDB(boolean notify) {
        Thread thread = new Thread(() -> {
            int len = items.size();
            List<String> list = database.itemDao().getIds();
            for (int i = 0; i < items.size(); i++) {
                TaskItem item = items.get(i);
                try {
                    database.itemDao().insert(item);
                } catch (Exception e) {
                    database.itemDao().update(item);
                }
                list.remove(item.id);
            }
            for (int i = 0; i < list.size(); i++)
                database.itemDao().deleteById(list.get(i));
            editor = mPrefs.edit();
            editor.putLong(DATABASE_NAME + "version", ver).apply();
            if (notify)
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();
        });
        thread.start();
    }

    class syncThread extends Thread {
        final Object lock = new Object();
        @Override
        public void run() {
            super.run();
            if (user != null) {
                if (mPrefs.contains(DATABASE_NAME + "version")) {
                    mDatabase = FirebaseDatabase.getInstance().getReference("Tasks");
                    mDatabase.child(DATABASE_NAME).child("version").get().addOnCompleteListener(task -> {
                        ver = task.getResult().getValue(Long.class);
                        verApp = mPrefs.getLong(DATABASE_NAME + "version", 0);
                        if (ver != verApp)
                            if (verApp > ver) {
                                syncDB(1, true);
                                Toast.makeText(getApplicationContext(), "lDB -> cDB", Toast.LENGTH_SHORT).show();
                            } else {
                                syncDB(0, true);
                            }
                    });
                }
            }


        }
        public void syncDB(int syncType, boolean notify) {
            Thread thread = new Thread(() -> {
                synchronized (lock) {
                    if (mDatabase == null)
                        mDatabase = FirebaseDatabase.getInstance().getReference("Tasks");
                    if (syncType == 1) {
                        TaskItem item;
                        List<TaskItem> items = database.itemDao().getAll();
                        for (int i = 0; i < items.size(); i++) {
                            item = items.get(i);
                            mDatabase.child(DATABASE_NAME).child(item.id).setValue(item);
                        }
                        mDatabase.child(DATABASE_NAME).child("version").setValue(verApp);
                        Toast.makeText(getApplicationContext(), "lDB -> cDB", Toast.LENGTH_SHORT).show();
                    }
                    if (syncType == 0) {
                        items = new ArrayList<>();
                        mDatabase.getRoot().addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.child("Tasks").child(DATABASE_NAME).getChildren()) {
                                    TaskItem tItem = new TaskItem(ds.child("id").getValue(String.class), ds.child("date").getValue(String.class), ds.child("time").getValue(String.class), ds.child("task").getValue(String.class), ds.child("enable").getValue(String.class), ds.child("description").getValue(String.class), ds.child("edited").getValue(String.class), ds.child("done").getValue(String.class), ds.child("arch").getValue(String.class));
                                    if (!ds.getKey().equals("items") && !ds.getKey().equals("archive") && !ds.getKey().equals("version"))
                                        items.add(tItem);
                                }
                                loadToDB(true);
                                Toast.makeText(getApplicationContext(), "cDB -> lDB", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                }
            });
            thread.start();
        }
        private void loadToDB(boolean notify) {
            Thread thread = new Thread(() -> {
                synchronized (lock){
                List<String> list = database.itemDao().getIds();
                for (int i = 0; i < items.size(); i++) {
                    TaskItem item = items.get(i);
                    try {
                        database.itemDao().insert(item);
                    } catch (Exception e) {
                        database.itemDao().update(item);
                    }
                    list.remove(item.id);
                }
                for (int i = 0; i < list.size(); i++)
                    database.itemDao().deleteById(list.get(i));
                editor = mPrefs.edit();
                editor.putLong(DATABASE_NAME + "version", ver).apply();
                if(notify)
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();
            }
            });
            thread.start();
        }
    }
}