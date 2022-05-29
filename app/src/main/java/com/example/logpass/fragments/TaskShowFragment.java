package com.example.logpass.fragments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.logpass.DB.AppDB;
import com.example.logpass.MainActivity;
import com.example.logpass.R;
import com.example.logpass.classes.TaskItem;
import com.example.logpass.fragments.dialogs.TaskEditDialogMD;
import com.example.logpass.recievers.AlarmReceiver;
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
    EditText task_tV, edit_task;
    Button save;

    AppDB database;

    long ver;

    SharedPreferences preferences;

    Calendar calendar = Calendar.getInstance();

    TaskItem item;

    DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference("Tasks");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    int size;

    public TaskShowFragment(TaskItem item, int size, Context context) {
        this.item = item;
        this.size = size;
        database = Room.databaseBuilder(context, AppDB.class, MainActivity.DATABASE_NAME)
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.show_task_fragment_new, container, false);
        setHasOptionsMenu(true);
        init();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        task_tV.setText(item.task);
        date_tV.setText(item.date);
        time_tV.setText(item.time);
        edit_task.setText(item.description);
        edit_task.setMovementMethod(new ScrollingMovementMethod());
        del_task.setOnClickListener(v -> {
            item.done = "false";
            item.arch = "true";
            updateItem(item);
            ((AppCompatActivity) requireContext()).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.fragment_container, new MainFragment()).commit();
        });
        time_tV.setOnClickListener(v -> {
            MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                    .setMinute(calendar.get(Calendar.MINUTE))
                    .setTitleText("Выберите время")
                    .build();
            materialTimePicker.addOnPositiveButtonClickListener(v1 -> {
                String time;
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.MINUTE, materialTimePicker.getMinute());
                calendar.set(Calendar.HOUR_OF_DAY, materialTimePicker.getHour());
                if (calendar.get(Calendar.MINUTE) < 10)
                    time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + "0" + calendar.get(Calendar.MINUTE);
                else
                    time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                item.time = time;
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
                calendar.set(Calendar.YEAR, Integer.parseInt(date.substring(date.length() - 7, date.length() - 3)));
                @SuppressLint("DefaultLocale") String date1 = String.format("%02d.%02d.", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1) + calendar.get(Calendar.YEAR);
                date_tV.setText(date1);
                item.date = date1;
            });

            materialDatePicker.show(getChildFragmentManager(), "MD_DATE_PICKER");
        });
        save.setOnClickListener(v -> {
            String descriptN = edit_task.getText().toString();
            String taskN = task_tV.getText().toString();
            if (!descriptN.equals(item.description)) {
                item.description = descriptN;
            }
            if (!taskN.equals(item.task)) {
                item.task = taskN;
            }
            updateItem(item);
            ver = System.currentTimeMillis();
            if (MainActivity.hasConnection(requireContext())) {
                mDataBase.child(user.getUid()).child(item.id).setValue(item);
                mDataBase.child(user.getUid()).child("version").setValue(ver);
            }
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(MainActivity.DATABASE_NAME + "version", ver).apply();
            AlarmManager alarmMgr = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent1 = new Intent(getContext(), AlarmReceiver.class);
            intent1.putExtra("Task", item.task);
            intent1.putExtra("idStr", item.id);
            intent1.putExtra("Date", item.time + item.date);
            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), (int) System.currentTimeMillis(), intent1, PendingIntent.FLAG_ONE_SHOT);
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            //getFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.fragment_container, new MainFragment()).commit();
        });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.archive_menu_back, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ((AppCompatActivity) requireContext()).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.fragment_container, new MainFragment()).commit();
        requireActivity().setTitle("EasyTodo: Задачи");
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        task_tV = view.findViewById(R.id.tV_taskView);
        date_tV = view.findViewById(R.id.tV_dateView);
        time_tV = view.findViewById(R.id.tV_timeView);
        del_task = view.findViewById(R.id.delete_task);
        edit_task = view.findViewById(R.id.eT_taskView);
        save = view.findViewById(R.id.btn_crtView);
        preferences = requireActivity().getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    private void updateItem(TaskItem item) {
        Thread thread = new Thread(() -> {
            SharedPreferences.Editor editor = preferences.edit();
            item.edited = "true";
            database.itemDao().update(item);
            ver = System.currentTimeMillis();
            editor.putLong(MainActivity.DATABASE_NAME + "version", ver).apply();
            requireActivity().runOnUiThread(() -> {
                ((AppCompatActivity) requireContext()).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.fragment_container, new MainFragment()).commit();
                requireActivity().setTitle("EasyTodo: Задачи");
            });
        });
        thread.start();
    }

}
