package com.example.logpass.old;


import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logpass.R;

class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private int count;
    private String mTask;
    private String mDateTime;
    private int viewHolderCount = -1;

    public TaskAdapter(int count) {

    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return count;
    }

    class TaskViewHolder extends RecyclerView.ViewHolder{

        TextView tV_task;
        TextView tV_date;
        CheckBox is_done;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tV_task = itemView.findViewById(R.id.tV_task);
            tV_date = itemView.findViewById(R.id.tv_date);
            is_done = itemView.findViewById(R.id.cB_done);
        }

    }
}
/*
Context context = parent.getContext();
        int idTask = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(idTask, parent, false);
        TaskViewHolder taskViewHolder = new TaskViewHolder(view);
        viewHolderCount++;

        return taskViewHolder;
 */
/*
if(cursor.moveToFirst()) {
            holder.bind();
        }
        holder.is_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int actualPosition = holder.getAdapterPosition();
                notifyItemRemoved(actualPosition);
                TaskCreateFragment.editor.putInt("number", TaskCreateFragment.pref.getInt("number",0)-1);
                TaskCreateFragment.editor.commit();
                count--;
            }
        });
 */
