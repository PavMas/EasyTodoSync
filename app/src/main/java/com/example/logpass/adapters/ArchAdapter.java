package com.example.logpass.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.logpass.DB.AppDB;
import com.example.logpass.MainActivity;
import com.example.logpass.R;
import com.example.logpass.classes.TaskItem;

import java.util.ArrayList;
import java.util.List;

public class ArchAdapter extends RecyclerView.Adapter<ArchAdapter.ArchiveHolder> {

    AppDB db;

    Context context;

    List<TaskItem> list;

    public ArchAdapter(Context context) {
        this.context = context;
        db = Room.databaseBuilder(context, AppDB.class, MainActivity.DATABASE_NAME)
                .build();
        getArchiveItems();

    }

    @NonNull
    @Override
    public ArchiveHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.archive_item, parent, false);
        return new ArchAdapter.ArchiveHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArchiveHolder holder, int position) {
        TaskItem item = list.get(position);
        holder.date.setText(item.date);
        holder.task.setText(item.task);
        if (Boolean.parseBoolean(item.done))
            holder.done.setText("Выполнено");
        else {
            holder.done.setTextColor(Color.RED);
            holder.done.setText("Не выполнено");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void getArchiveItems() {
        list = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            public void run() {
                List<TaskItem> items = db.itemDao().getCompleted();
                list = items;
            }
        });
        thread.start();
    }

    class ArchiveHolder extends RecyclerView.ViewHolder{

        TextView task, date, done;
        public ArchiveHolder(@NonNull View itemView) {
            super(itemView);
            init();
        }

        private void init(){
            date = itemView.findViewById(R.id.tv_date);
            task = itemView.findViewById(R.id.tv_task);
            done = itemView.findViewById(R.id.tv_done);
        }
    }
}
