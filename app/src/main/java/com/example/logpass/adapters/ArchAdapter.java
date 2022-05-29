package com.example.logpass.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.logpass.DB.AppDB;
import com.example.logpass.MainActivity;
import com.example.logpass.R;
import com.example.logpass.classes.TaskItem;
import com.example.logpass.fragments.TaskShowArchiveFragment;
import com.example.logpass.fragments.TaskShowFragment;

import java.util.ArrayList;
import java.util.List;

public class ArchAdapter extends RecyclerView.Adapter<ArchAdapter.ArchiveHolder> {

    List<TaskItem> list = new ArrayList<>();

    public ArchAdapter(Context context) {
    }

    @NonNull
    @Override
    public ArchiveHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.archive_item, parent, false);
        return new ArchiveHolder(view);
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
        holder.itemView.setOnClickListener(v ->
                ((AppCompatActivity) v.getContext()).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragment_container, new TaskShowArchiveFragment(item, v.getContext())).addToBackStack(null).commit());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<TaskItem> list){
        this.list = list;
    }

    static class ArchiveHolder extends RecyclerView.ViewHolder{

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
