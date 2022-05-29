package com.example.logpass.fragments.dialogs;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.room.Room;

import com.example.logpass.DB.AppDB;
import com.example.logpass.MainActivity;
import com.example.logpass.R;
import com.example.logpass.classes.TaskItem;
import com.example.logpass.fragments.MainFragment;
import com.example.logpass.recievers.AlarmReceiver;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;


public class TaskEditDialogMD extends DialogFragment {
    public static final String TAG = "dialog";


    private Toolbar toolbar;
    private Button crt, setTime, setDate;
    EditText et_task, et_descript;
    private View view;

    TextView tv_time, tv_date;

    Calendar calendar = Calendar.getInstance();
    protected DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference("Tasks");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private final AppDB database;

    Context context;

    int count;
    String date1, task, time, descript;

    public TaskEditDialogMD(Context context) {
        database = Room.databaseBuilder(context, AppDB.class, MainActivity.DATABASE_NAME)
                .build();
        this.context = context;
    }

    public static void display(FragmentManager fragmentManager, Context context) {
        TaskEditDialogMD exampleDialog = new TaskEditDialogMD(context);
        exampleDialog.show(fragmentManager, TAG);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.dialog, container, false);
        init();

        return view;
    }


    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view1, Bundle savedInstanceState) {
        super.onViewCreated(view1, savedInstanceState);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle("Создать задачу");
        setTime.setOnClickListener(v -> {
            MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                    .setMinute(calendar.get(Calendar.MINUTE))
                    .setTitleText("Выберите время")
                    .build();
            materialTimePicker.addOnPositiveButtonClickListener(view -> {
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.MINUTE, materialTimePicker.getMinute());
                calendar.set(Calendar.HOUR_OF_DAY, materialTimePicker.getHour());
                if (calendar.get(Calendar.MINUTE) < 10)
                    time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + "0" + calendar.get(Calendar.MINUTE);
                else
                    time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                tv_time.setText(time);
            });
            materialTimePicker.show(getChildFragmentManager(), "MD_TIME_PICKER");
        });
        setDate.setOnClickListener(v -> {
            MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
            materialDateBuilder.setTitleText("Выберите дату");
            MaterialDatePicker materialDatePicker = materialDateBuilder.build();
            materialDatePicker.addOnPositiveButtonClickListener(view -> {
                String date = materialDatePicker.getHeaderText();
                calendar.set(Calendar.DAY_OF_MONTH, getDay(date));
                calendar.set(Calendar.MONTH, getMonth(date));
                calendar.set(Calendar.YEAR, Integer.parseInt(date.substring(date.length() - 7, date.length() - 3)));
                tv_date.setText(date);
                date1 = String.format("%02d.%02d.", calendar.get(Calendar.DAY_OF_MONTH), (calendar.get(Calendar.MONTH) + 1)) + calendar.get(Calendar.YEAR);
            });

            materialDatePicker.show(getChildFragmentManager(), "MD_DATE_PICKER");
        });
        crt.setOnClickListener(v ->
                createItem());
        toolbar.setOnMenuItemClickListener(item -> {
            dismiss();
            return true;

        });
    }

    private void init() {
        toolbar = view.findViewById(R.id.toolbar);
        crt = view.findViewById(R.id.create_btn);
        setTime = view.findViewById(R.id.set_time_btn);
        setDate = view.findViewById(R.id.set_date_btn);
        tv_time = view.findViewById(R.id.tV_time);
        tv_date = view.findViewById(R.id.tV_date);
        et_task = view.findViewById(R.id.create_task);
        et_descript = view.findViewById(R.id.create_task_deskript);
    }

    private void createItem() {
        if (FieldsOk()) {
            String uid = user.getUid();
            long id = System.currentTimeMillis();
            task = et_task.getText().toString();
            descript = et_descript.getText().toString();
            TaskItem item = new TaskItem(id + "", date1, time, task, "true", descript, "false", "false", "false");
            addItem(item);
            SharedPreferences.Editor editor = requireActivity().getSharedPreferences("myprefs", Context.MODE_PRIVATE).edit();
            editor.putLong(uid + "version", id).apply();
            if (MainActivity.hasConnection(requireContext())) {
                mDataBase.child(uid).child("items").setValue(count + 1);
                mDataBase.child(uid).child(id + "").setValue(item);
                mDataBase.child(uid).child("version").setValue(id);
            }
            AlarmManager alarmMgr = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent1 = new Intent(getContext(), AlarmReceiver.class);
            intent1.putExtra("Task", et_task.getText().toString());
            intent1.putExtra("id", id);
            intent1.putExtra("idStr", id + "");
            intent1.putExtra("Date", time + date1);
            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), (int) System.currentTimeMillis(), intent1, PendingIntent.FLAG_ONE_SHOT);
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            dismiss();

        }
    }

    private boolean FieldsOk() {
        if (tv_time.getText().equals("") || tv_date.getText().equals("") || et_task.getText().toString().equals(""))
            return false;
        else
            try {
                user.getUid();
                return true;
            } catch (Exception e) {
                Toast.makeText(getContext(), "Вы не вошли в аккаунт", Toast.LENGTH_LONG).show();
                return false;
            }
    }

    public static int getMonth(String str) {
        String sub;
        if (str.charAt(1) == ' ')
            sub = str.substring(2, 5);
        else
            sub = str.substring(3, 6);
        switch (sub) {
            case "янв":
                return 0;
            case "фев":
                return 1;
            case "мар":
                return 2;
            case "апр":
                return 3;
            case "мая":
                return 4;
            case "июн":
                return 5;
            case "июл":
                return 6;
            case "авг":
                return 7;
            case "сен":
                return 8;
            case "окт":
                return 9;
            case "ноя":
                return 10;
            case "дек":
                return 11;
        }
        return 0;
    }

    public static int getDay(String str) {
        if (str.charAt(1) == ' ')
            return str.charAt(0) - 48;
        else
            return Integer.parseInt(str.substring(0, 2));
    }

    public void addItem(TaskItem twit) {
        Thread thread = new Thread(() -> {
            database.itemDao().insert(twit);
            database.close();
        });
        thread.start();
    }
}
