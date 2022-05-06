package com.example.logpass;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.logpass.adapters.MyListAdapter;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class TaskShowFragment extends Fragment {
    protected View view;

    TextView date_tV, time_tV, del_task;
    String task, date, time, descript;
    EditText  task_tV, edit_task;
    Button save;

    boolean arch;

    Calendar calendar = Calendar.getInstance();

    TaskItem item;

    DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference("Tasks");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    int size;
/*
    public TaskShowFragment(String task, String date, String time, String description) {
        this.task = task;
        this.date = date;
        this.time = time;
        this.descript = description;
    }

 */
    public TaskShowFragment(TaskItem item, int size){
        this.item = item;
        this.descript = item.description;
        this.task = item.task;
        this.date = item.date;
        this.time = item.time;
        this.size = size;
    }
    public TaskShowFragment(TaskItem item, int size, boolean arch){
        this.item = item;
        this.descript = item.description;
        this.task = item.task;
        this.date = item.date;
        this.time = item.time;
        this.size = size;
        this.arch = arch;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.show_task_fragment, container, false);
        setHasOptionsMenu(true);
        init();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        task_tV.setText(task);
        date_tV.setText(date);
        time_tV.setText(time);
        edit_task.setText(descript);
        if(!arch)
        del_task.setOnClickListener(v -> {
            mDataBase.child(user.getUid()).child("archive").child(item.id).setValue(item);
            mDataBase.child(user.getUid()).child("archive").child(item.id).child("done").setValue("false");
            mDataBase.child(user.getUid()).child(item.id).removeValue();
            mDataBase.child(user.getUid()).child("items").setValue(size-1);
            getFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.fragment_container, new MainFragment()).commit();
        });
        else del_task.setText("");
        time_tV.setOnClickListener(v -> {
            MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                    .setMinute(calendar.get(Calendar.MINUTE))
                    .setTitleText("Выберите время")
                    .build();
            materialTimePicker.addOnPositiveButtonClickListener(v1 -> {
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.MINUTE, materialTimePicker.getMinute());
                calendar.set(Calendar.HOUR_OF_DAY, materialTimePicker.getHour());
                if(calendar.get(Calendar.MINUTE)<10)
                    time = calendar.get(Calendar.HOUR_OF_DAY)+":"+"0"+calendar.get(Calendar.MINUTE);
                else
                    time = calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
                mDataBase.child(user.getUid()).child(item.id).child("time").setValue(time);
                time_tV.setText(time);
            });
            materialTimePicker.show(getChildFragmentManager(), "MD_TIME_PICKER");
        });
        date_tV.setOnClickListener(v -> {
            MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
            materialDateBuilder.setTitleText("Выберите дату");
            MaterialDatePicker materialDatePicker = materialDateBuilder.build();
            materialDatePicker.addOnPositiveButtonClickListener(v1 -> {
                String date = materialDatePicker.getHeaderText();
                calendar.set(Calendar.DAY_OF_MONTH, TaskEditDialogMD.getDay(date));
                calendar.set(Calendar.MONTH, TaskEditDialogMD.getMonth(date));
                calendar.set(Calendar.YEAR, Integer.parseInt(date.substring(date.length()-7, date.length()-3)));
                @SuppressLint("DefaultLocale") String date1 = String.format("%02d.%02d.", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH))+calendar.get(Calendar.YEAR);
                date_tV.setText(date1);
                mDataBase.child(user.getUid()).child(item.id).child("date").setValue(date1);
            });

            materialDatePicker.show(getChildFragmentManager(), "MD_DATE_PICKER");
        });
        save.setOnClickListener(v -> {
            String descriptN = edit_task.getText().toString();
            String taskN = task_tV.getText().toString();
            if(!descriptN.equals(descript)) {
                descript = descriptN;
                mDataBase.child(user.getUid()).child(item.id).child("description").setValue(descriptN);
            }
            if(!taskN.equals(task)) {
                task = taskN;
                mDataBase.child(user.getUid()).child(item.id).child("task").setValue(taskN);
            }
            getFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.fragment_container, new MainFragment()).commit();
        });

    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.archive_menu_back, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(!arch) {
            getFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.fragment_container, new MainFragment()).commit();
            getActivity().setTitle("EasyTodo: Задачи");
        }
        else {
            getFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.fragment_container, new ArchiveFragment()).commit();
            getActivity().setTitle("EasyTodo: Архив");
        }
        return super.onOptionsItemSelected(item);
    }

    private void init(){
        task_tV = view.findViewById(R.id.tV_taskView);
        date_tV = view.findViewById(R.id.tV_dateView);
        time_tV = view.findViewById(R.id.tV_timeView);
        del_task = view.findViewById(R.id.delete_task);
        edit_task = view.findViewById(R.id.eT_taskView);
        save = view.findViewById(R.id.btn_crtView);
    }
}
