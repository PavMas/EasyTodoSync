package com.example.logpass.old;

import androidx.fragment.app.Fragment;

public class TaskCreateFragmentMD extends Fragment {
    /*
    View view;

    Button create, setTime, setDate;
    EditText et_task;
    TextView tv_time, tv_date;
    Calendar calendar = Calendar.getInstance();
    protected DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference("Tasks");
    FirebaseUser user = AccountFragment.mAuth.getCurrentUser();
    int count;
    String date1, task, time;
    boolean enable = true;
    public static Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.task_create_fragment, container, false);
        init();
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
        create.setOnClickListener(v -> {
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




        return view;
    }
    private void createItem(){
        if(FieldsOk()) {
            long id = System.currentTimeMillis();
            task = et_task.getText().toString();
            mDataBase.child(user.getUid()).child("items").setValue(count + 1);
            mDataBase.child(user.getUid()).child(String.valueOf(id)).setValue(new TaskItem(date1, time, task, "true", id+""));
            AlarmManager alarmMgr = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent1 = new Intent(getContext(), AlarmReceiver.class);
            intent1.putExtra("Task", et_task.getText().toString());
            intent1.putExtra("id", id);
            intent1.putExtra("idStr", id+"");
            PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), (int)System.currentTimeMillis(), intent1, PendingIntent.FLAG_ONE_SHOT);
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();
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
    private int getMonth(String str){
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
    private int getDay(String str){
        if(str.charAt(1) == ' ')
            return str.charAt(0) - 48;
        else
            return Integer.parseInt(str.substring(0, 2));
    }
    private void init(){
        tv_date = view.findViewById(R.id.tV_date);
        create = view.findViewById(R.id.create_btn);
        et_task = view.findViewById(R.id.create_task);
        setTime = view.findViewById(R.id.set_time_btn);
        setDate = view.findViewById(R.id.set_date_btn);
        tv_time = view.findViewById(R.id.tV_time);
        context = getContext();
    }

     */
}
