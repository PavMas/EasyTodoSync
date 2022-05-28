package com.example.logpass.recievers;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import com.example.logpass.DB.AppDB;
import com.example.logpass.MainActivity;
import com.example.logpass.R;
import com.example.logpass.classes.TaskItem;
import com.example.logpass.fragments.MainFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AlarmReceiver extends BroadcastReceiver {
    boolean enabled;
    AppDB database;
    TaskItem item;
    Context context;
    Intent intent;
    String date;
    @Override
    public void onReceive(Context context1, Intent intent1) {
        context = context1;
        intent = intent1;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Tasks");
       // long id = intent.getLongExtra("id", 0);
        String id = intent.getStringExtra("idStr");
        date = intent.getStringExtra("Date");
        database = Room.databaseBuilder(context, AppDB.class, MainActivity.DATABASE_NAME).build();
        getItem(id);
        /*
        mDatabase.child(user.getUid()).child(id).child("enable").get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
                enabled = Boolean.parseBoolean(String.valueOf(task.getResult().getValue()));
            if(enabled){
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainFragment.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                        .setContentTitle("Задача")
                        .setContentText(intent.getStringExtra("Task"))
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentIntent(PendingIntent.getActivity(context.getApplicationContext(),
                                0, new Intent(context, MainFragment.class),
                                PendingIntent.FLAG_CANCEL_CURRENT));
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify((int) System.currentTimeMillis(), builder.build());
            }
        });
         */
    }
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1) {
                database.close();
                try {
                    if (Boolean.parseBoolean(item.enable)&&(item.time+item.date).equals(date)) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainFragment.CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                                .setContentTitle("Задача")
                                .setContentText(intent.getStringExtra("Task"))
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setContentIntent(PendingIntent.getActivity(context.getApplicationContext(),
                                        0, new Intent(context, MainActivity.class),
                                        PendingIntent.FLAG_CANCEL_CURRENT));
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
                    }
                }
                catch (Exception ignored){

                }
            }

        }
    };


    public void getItem(String id){
        Thread thread = new Thread(() -> {
            item = database.itemDao().getById(id);
            handler.sendEmptyMessage(1);
        });
        thread.start();
    }
}
