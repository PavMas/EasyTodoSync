package com.example.logpass;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.logpass.DB.AppDB;
import com.example.logpass.classes.TaskItem;
import com.example.logpass.fragments.AccountFragment;
import com.example.logpass.fragments.InFragment;
import com.example.logpass.fragments.MainFragment;
import com.example.logpass.recievers.AlarmReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static String DATABASE_NAME;
    public static final String APP_PREFERENCES = "myprefs";

    private AppDB database;

    private static FirebaseUser user;
    private DatabaseReference mDatabase;

    SharedPreferences mPrefs;
    SharedPreferences.Editor editor;

    long ver;
    long verApp;

    List<TaskItem> items;

    List<String> itemsId = new ArrayList<>();

    @SuppressLint("StaticFieldLeak")
    protected static Context context;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("EasyTodo: ????????????");
        user = FirebaseAuth.getInstance().getCurrentUser();
        mPrefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        DATABASE_NAME = mPrefs.getString("dbname", "defaultDB");
        database = Room.databaseBuilder(this, AppDB.class, DATABASE_NAME).build();
        if (hasConnection(this)) {
            new syncThread().start();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int anim = 0, anim1 = 0;
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.page_1:
                    setTitle("EasyTodo: ????????????");
                    selectedFragment = new MainFragment();
                    anim = R.anim.slide_in_left;
                    anim1 = R.anim.slide_out_right;
                    break;
                case R.id.page_2:
                    if (user == null) {
                        setTitle("EasyTodo: ???????? ?? ??????????????");
                        selectedFragment = new AccountFragment();
                    } else {
                        setTitle("EasyTodo: ???????????? ??????????????");
                        selectedFragment = new InFragment();
                    }
                    anim = R.anim.slide_in_right;
                    anim1 = R.anim.slide_out_left;
                    break;
            }
            assert selectedFragment != null;
            getSupportFragmentManager().beginTransaction().setCustomAnimations(anim, anim1).replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        });

        context = getApplicationContext();
    }

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

    public void startThread() {
        database = Room.databaseBuilder(context, AppDB.class, DATABASE_NAME).build();
        syncThread thread = new syncThread();
        thread.notify = false;
        thread.start();
    }

    public void updateUser() {
        user = FirebaseAuth.getInstance().getCurrentUser();
    }


    class syncThread extends Thread {
        boolean notify = true;
        final Object lock = new Object();
        boolean toLocale = true;

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.child("Tasks").child(DATABASE_NAME).getChildren()) {
                    TaskItem tItem = new TaskItem(ds.child("id").getValue(String.class), ds.child("date").getValue(String.class), ds.child("time").getValue(String.class), ds.child("task").getValue(String.class), ds.child("enable").getValue(String.class), ds.child("description").getValue(String.class), ds.child("done").getValue(String.class), ds.child("edited").getValue(String.class), ds.child("arch").getValue(String.class));
                    if (!ds.getKey().equals("items") && !ds.getKey().equals("archive") && !ds.getKey().equals("version"))
                        items.add(tItem);
                    itemsId.add(tItem.id);
                }
                mDatabase.getRoot().removeEventListener(this);

                if (toLocale)
                    loadToDB(notify);
                else
                    loadToCloudDB();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };


        @Override
        public void run() {
            super.run();
            if (user != null) {
                if (mPrefs.contains(DATABASE_NAME + "version")) {
                    mDatabase = FirebaseDatabase.getInstance().getReference("Tasks");
                    mDatabase.child(DATABASE_NAME).child("version").get().addOnCompleteListener(task -> {
                        ver = task.getResult().getValue(Long.class);
                        verApp = mPrefs.getLong(DATABASE_NAME + "version", 0);
                        if (ver != verApp) {
                            Toast.makeText(getApplicationContext(), "????????????????????????????", Toast.LENGTH_SHORT).show();
                            if (verApp > ver) {
                                toLocale = false;
                                syncDB(1, notify);
                            } else
                                syncDB(0, notify);
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
                        items = new ArrayList<>();
                        mDatabase.getRoot().addValueEventListener(listener);
                    }
                    if (syncType == 0) {
                        items = new ArrayList<>();
                        mDatabase.getRoot().addValueEventListener(listener);
                    }
                }
            });
            thread.start();
        }

        private void loadToDB(boolean notify) {
            Thread thread = new Thread(() -> {
                synchronized (lock) {
                    List<String> list = database.itemDao().getIds();
                    for (int i = 0; i < items.size(); i++) {
                        TaskItem item = items.get(i);
                        try {
                            database.itemDao().insert(item);
                        } catch (Exception e) {
                            database.itemDao().update(item);
                        }
                        list.remove(item.id);
                        if (Boolean.parseBoolean(item.enable)) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(item.time.substring(0, 2)));
                            calendar.set(Calendar.MINUTE, Integer.parseInt(item.time.substring(3, 5)));
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(item.date.substring(0, 2)));
                            calendar.set(Calendar.MONTH, Integer.parseInt(item.date.substring(3, 5)) - 1);
                            calendar.set(Calendar.YEAR, Integer.parseInt(item.date.substring(6)));
                            AlarmManager alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                            Intent intent1 = new Intent(getApplicationContext(), AlarmReceiver.class);
                            intent1.putExtra("Task", item.task);
                            intent1.putExtra("idStr", item.id);
                            intent1.putExtra("Date", item.time + item.date);
                            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) System.currentTimeMillis(), intent1, PendingIntent.FLAG_ONE_SHOT);
                            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
                        }
                    }
                    for (int i = 0; i < list.size(); i++)
                        database.itemDao().deleteById(list.get(i));
                    editor = mPrefs.edit();
                    editor.putLong(DATABASE_NAME + "version", ver).apply();
                    database.close();
                    if (notify)
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();
                }
            });
            thread.start();
        }

        private void loadToCloudDB() {
            Thread thread = new Thread(() -> {
                synchronized (lock) {
                    TaskItem item;
                    List<TaskItem> items1 = database.itemDao().getAll();
                    for (int i = 0; i < items1.size(); i++) {
                        item = items1.get(i);
                        mDatabase.child(DATABASE_NAME).child(item.id).setValue(item);
                        itemsId.remove(item.id + "");
                    }
                    for (int i = 0; i < itemsId.size(); i++) {
                        mDatabase.child(DATABASE_NAME).child(itemsId.get(i) + "").removeValue();
                    }
                    mDatabase.child(DATABASE_NAME).child("version").setValue(verApp);
                }
            });
            thread.start();
        }
    }
}