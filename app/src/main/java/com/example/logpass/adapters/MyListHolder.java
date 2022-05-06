package com.example.logpass.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logpass.R;

public class MyListHolder extends RecyclerView.ViewHolder {
TextView task;
TextView date;
    public MyListHolder(@NonNull View itemView) {
        super(itemView);
        init();
    }
    private void init(){
        task = itemView.findViewById(R.id.tv_date);
        date = itemView.findViewById(R.id.tV_task);
    }
}
