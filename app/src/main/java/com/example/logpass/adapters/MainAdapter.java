package com.example.logpass.adapters;

import android.annotation.SuppressLint;

import android.content.Context;

import android.content.SharedPreferences;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.logpass.DB.AppDB;

import com.example.logpass.MainActivity;
import com.example.logpass.R;

import com.example.logpass.classes.TaskItem;
import com.example.logpass.fragments.TaskShowFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.List;


@SuppressLint("UseSwitchCompatOrMaterialCode")
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.CardHolder> {
    private List<TaskItem> list = new ArrayList<>();
    private final Context context;
    private final AppDB database;
    DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference("Tasks");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    SharedPreferences preferences;
    SwitchCompat aSwitch;
    AppCompatCheckBox cb;
    boolean arch;
    long ver;



    public MainAdapter(Context context){
        this.context = context;
        database = Room.databaseBuilder(context, AppDB.class, MainActivity.DATABASE_NAME)
                .build();
    }


    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new CardHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {
        TaskItem item = list.get(position);
        holder.date.setText(item.date);
        holder.task.setText(item.task);
        holder.tv_description.setText(item.description);
        boolean isExpandable = list.get(position).isExpandable();
        holder.tv_description.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
        holder.description.setText(isExpandable ? "Скрыть" : "Подробнее");
            cb = holder.checkBox;
            aSwitch = holder.switchCompat;
            aSwitch.setChecked(Boolean.parseBoolean(item.enable));
            aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isChecked)
                    isEnabled("false", position, item);
                else
                    isEnabled("true", position, item);
            });
            cb.setOnClickListener(v -> {
                item.arch = "true";
                item.done = "true";
                update(item);
                list.remove(position);
                notifyItemRemoved(position);
                ver = System.currentTimeMillis();
                if(MainActivity.hasConnection(context)){
                    mDataBase.child(user.getUid()).child(item.id).child("done").setValue("true");
                    mDataBase.child(user.getUid()).child(item.id).child("arch").setValue("true");
                    mDataBase.child(user.getUid()).child("version").setValue(ver);
                }
                preferences = context.getSharedPreferences("myprefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putLong(MainActivity.DATABASE_NAME+"version", ver).apply();
/*
                mDataBase.child(user.getUid()).child("archive").child(item.id).setValue(item);
                mDataBase.child(user.getUid()).child("archive").child(item.id).child("done").setValue("true");
                mDataBase.child(user.getUid()).child(item.id).removeValue();
                mDataBase.child(user.getUid()).child("items").setValue(list.size() - 1);
 */
            });
        holder.itemView.setOnClickListener(v -> {

            /*
            if(arch)
            activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragment_container, new TaskShowArchiveFragment(item, context)).addToBackStack(null).commit();
            else

             */
            ((AppCompatActivity) v.getContext()).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragment_container, new TaskShowFragment(item, list.size(), context)).addToBackStack(null).commit();

        });
    }

    private void isEnabled(String enable, int position, TaskItem item) {
       // mDataBase.child(user.getUid()).child(id).child("enable").setValue(enable);
        item.enable = enable;
        list.get(position).enable = enable;
        update(item);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<TaskItem> list){
        this.list = list;
    }


    public void getAll() {
        list = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            public void run() {
                List<TaskItem> items = database.itemDao().getAll();
                list = items;
            }
        });
        thread.start();
    }

    public void getUncompItems() {
        list = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            public void run() {
                List<TaskItem> items = database.itemDao().getUncompleted();
                list = items;
            }
        });
        thread.start();
    }

    public void deleteItem(TaskItem item) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                database.itemDao().delete(item);
            }
        });
        thread.start();
    }

    public void update(TaskItem item) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                database.itemDao().update(item);
            }
        });
        thread.start();
    }

    public class CardHolder extends RecyclerView.ViewHolder {
        TextView task, date, tv_description;
        MaterialButton description;
        MaterialCardView cardView;
        SwitchCompat switchCompat;
        AppCompatCheckBox checkBox;
        public CardHolder(@NonNull View itemView) {
            super(itemView);
            init();
            description.setOnClickListener(v -> {
                int position = getAdapterPosition();
                TaskItem item = list.get(position);
                item.setExpandable(!item.isExpandable());
                notifyItemChanged(position);
            });
        }
        private void init(){
            task = itemView.findViewById(R.id.tv_date);
            date = itemView.findViewById(R.id.tv_task);
            description = itemView.findViewById(R.id.card_btn_des);
            tv_description = itemView.findViewById(R.id.description);
            cardView = itemView.findViewById(R.id.card);
            switchCompat = itemView.findViewById(R.id.enable_switch);
            checkBox = itemView.findViewById(R.id.cB_done);
        }
    }
}