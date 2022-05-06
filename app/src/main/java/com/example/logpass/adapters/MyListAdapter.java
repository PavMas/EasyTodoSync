package com.example.logpass.adapters;

import android.annotation.SuppressLint;

import android.app.Activity;
import android.content.Context;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logpass.DB.AppDB;

import com.example.logpass.R;

import com.example.logpass.TaskItem;
import com.example.logpass.TaskShowFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.List;


@SuppressLint("UseSwitchCompatOrMaterialCode")
public class MyListAdapter extends RecyclerView.Adapter<MyListHolder> {
private List<TaskItem> items;
private Context context;
private Activity activity;
private AppDB database;
DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference("Tasks");
FirebaseAuth mAuth = FirebaseAuth.getInstance();
FirebaseUser user = mAuth.getCurrentUser();
long sTime;
Switch aSwitch;
CheckBox cb;
TextView done;
int position;
boolean arch;

    public MyListAdapter(Context context, Activity activity, boolean arch) {
        this.context = context;
        this.activity = activity;
        this.arch = arch;
    }

    @NonNull
    @Override
    public MyListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int itemId = (arch ? R.layout.archive_item : R.layout.list_item);
        View view = LayoutInflater.from(parent.getContext()).inflate(itemId, parent, false);
        return new MyListHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull MyListHolder holder, int position) {
        TaskItem item = items.get(position);
        holder.date.setText(item.date);
        holder.task.setText(item.task);
        if(!arch) {
            cb = holder.itemView.findViewById(R.id.cB_done);
            aSwitch = holder.itemView.findViewById(R.id.swich);
            aSwitch.setChecked(Boolean.parseBoolean(item.enable));
            aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isChecked)
                    isEnabled("false", position, item.id);
                else
                    isEnabled("true", position, item.id);
            });
            cb.setOnClickListener(v -> {
                mDataBase.child(user.getUid()).child("archive").child(item.id).setValue(item);
                mDataBase.child(user.getUid()).child("archive").child(item.id).child("done").setValue("true");
                mDataBase.child(user.getUid()).child(item.id).removeValue();
                mDataBase.child(user.getUid()).child("items").setValue(items.size() - 1);
            });
        }
        else{
            done = holder.itemView.findViewById(R.id.tv_done);
            if (Boolean.parseBoolean(item.done))
            done.setText("Выполнено");
            else {
                done.setTextColor(Color.RED);
                done.setText("Не выполнено");
            }
        }
        holder.itemView.setOnClickListener(v -> {
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            //activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragment_container, new TaskShowFragment(item.task, item.date, item.time, item.description)).addToBackStack(null).commit();
            activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragment_container, new TaskShowFragment(item, items.size(), arch)).addToBackStack(null).commit();

        });
    }

    private void isEnabled(String enable, int position, String id){
        mDataBase.child(user.getUid()).child(id).child("enable").setValue(enable);
        items.get(position).enable = enable;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public void setItems(List<TaskItem> items){
        this.items = items;
        notifyDataSetChanged();
    }
    public void addItems(List<TaskItem> items){
        this.items.addAll(items);
        notifyDataSetChanged();
    }
    public void deleteItem(int i){
        this.items.remove(i);
        notifyDataSetChanged();
    }


/*
    public interface oCItem{
        void onClickItem(int position);
    }

 */

}


/*
        holder.itemView.setOnTouchListener((v, event) -> {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    sTime = System.currentTimeMillis();

                    break;
                case MotionEvent.ACTION_UP:
                    long eTime = System.currentTimeMillis();
                    if((float)(eTime-sTime)/1000 >= 0.55)
                    {
                        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        if(vibrator.hasVibrator())
                            vibrator.vibrate(50);
                        new MaterialAlertDialogBuilder(context)
                                .setMessage("Что вы хотите сделать?")
                                .setNegativeButton("Удалить", (dialog, which) -> {
                                    mDataBase.child(user.getUid()).child(item.id).removeValue();
                                    items.remove(position);
                                    mDataBase.child(user.getUid()).child("items").setValue(items.size());
                                })
                                .setPositiveButton("Изменить", (dialog, which) -> {

                                })
                                .setNeutralButton("Отмена", null)
                                .show();
                    }
                    break;
            }
            return true;
        });


         */
/*
                AlarmManager alarmManager = (AlarmManager) TaskCreateFragmentMD.context.getSystemService(Context.ALARM_SERVICE);
                Intent myIntent = new Intent(TaskCreateFragmentMD.context.getApplicationContext(), AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(), position, myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);
                 */
/*
        mDataBase.child(user.getUid()).child(position+"").child("enable").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                currentData.setValue(enable);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

            }
        });

         */