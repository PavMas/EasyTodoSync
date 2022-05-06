package com.example.logpass;


import android.app.AlarmManager;
import android.app.Dialog;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
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
    FirebaseUser user = AccountFragment.mAuth.getCurrentUser();

    int count;
    String date1, task, time, descript;

    public static TaskEditDialogMD display(FragmentManager fragmentManager) {
        TaskEditDialogMD exampleDialog = new TaskEditDialogMD();
        exampleDialog.show(fragmentManager, TAG);
        return exampleDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.dialog, container, false);
        init();

        return view;
    }


    @Override
    public void onViewCreated(View view1, Bundle savedInstanceState) {
        super.onViewCreated(view1, savedInstanceState);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle("Создать задачу");
        //toolbar.inflateMenu(R.menu.dialog_menu);
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
                if(calendar.get(Calendar.MINUTE)<10)
                    time = calendar.get(Calendar.HOUR_OF_DAY)+":"+"0"+calendar.get(Calendar.MINUTE);
                else
                    time = calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
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
                calendar.set(Calendar.YEAR, Integer.parseInt(date.substring(date.length()-7, date.length()-3)));
                // tv_date.setText(calendar.get(Calendar.DAY_OF_MONTH)+"."+calendar.get(Calendar.MONTH)+"."+calendar.get(Calendar.YEAR));
                tv_date.setText(date);
                date1 = calendar.get(Calendar.DAY_OF_MONTH)+"."+calendar.get(Calendar.MONTH)+"."+calendar.get(Calendar.YEAR);
                //tv_date.setText(materialDatePicker.getHeaderText());

            });

            materialDatePicker.show(getChildFragmentManager(), "MD_DATE_PICKER");
        });
        crt.setOnClickListener(v -> {
            mDataBase = FirebaseDatabase.getInstance().getReference("Tasks");
            mDataBase.child(user.getUid()).child("items").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        count = Integer.parseInt(String.valueOf(task.getResult().getValue()));
                        createItem();
                    }
                }
            });
        });
        toolbar.setOnMenuItemClickListener(item -> {
            dismiss();
            return true;

        });
    }

    private void init(){
        toolbar = view.findViewById(R.id.toolbar);
        crt = view.findViewById(R.id.create_btn);
        setTime = view.findViewById(R.id.set_time_btn);
        setDate = view.findViewById(R.id.set_date_btn);
        tv_time = view.findViewById(R.id.tV_time);
        tv_date = view.findViewById(R.id.tV_date);
        et_task = view.findViewById(R.id.create_task);
        et_descript = view.findViewById(R.id.create_task_deskript);
    }
    private void createItem(){
        if(FieldsOk()) {
            long id = System.currentTimeMillis();
            task = et_task.getText().toString();
            descript = et_descript.getText().toString();
            mDataBase.child(user.getUid()).child("items").setValue(count + 1);
            mDataBase.child(user.getUid()).child(String.valueOf(id)).setValue(new TaskItem(date1, time, task, "true", id+"", descript));
            AlarmManager alarmMgr = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent1 = new Intent(getContext(), AlarmReceiver.class);
            intent1.putExtra("Task", et_task.getText().toString());
            intent1.putExtra("id", id);
            intent1.putExtra("idStr", id+"");
            PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), (int)System.currentTimeMillis(), intent1, PendingIntent.FLAG_ONE_SHOT);
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            dismiss();
        }
    }
    private boolean FieldsOk(){
        if(tv_time.getText().equals("")||tv_date.getText().equals("")||et_task.getText().toString().equals(""))
            return false;
        else
            try {
                user.getUid();
                return true;
            }
            catch (Exception e){
                Toast.makeText(getContext(), "Вы не вошли в аккаунт", Toast.LENGTH_LONG).show();
                return false;
            }
    }
    public static int getMonth(String str){
        String sub;
        if(str.charAt(1) == ' ')
            sub = str.substring(2, 5);
        else
            sub = str.substring(3, 6);
        switch (sub){
            case "янв":
                return 0;
            case "фев":
                return 1;
            case "мар":
                return 2;
            case "апр":
                return 3;
            case "май":
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
    public static int getDay(String str){
        if(str.charAt(1) == ' ')
            return str.charAt(0) - 48;
        else
            return Integer.parseInt(str.substring(0, 2));
    }
}
