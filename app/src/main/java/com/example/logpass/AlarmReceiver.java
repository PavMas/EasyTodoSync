package com.example.logpass;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.BoringLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AlarmReceiver extends BroadcastReceiver {
    boolean enabled;
    @Override
    public void onReceive(Context context, Intent intent) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Tasks");
       // long id = intent.getLongExtra("id", 0);
        String id = intent.getStringExtra("idStr");
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
    }
}
