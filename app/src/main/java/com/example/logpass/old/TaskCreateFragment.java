package com.example.logpass.old;

import androidx.fragment.app.Fragment;

public class TaskCreateFragment extends Fragment {
    /*
    View view;
    Button create, setTime, setDate;
    EditText et_task;
    TextView tv_time, tv_date;
    private String task;
    protected static boolean isOK = false;
    private String date, time;
    private Calendar TimeAndDate = Calendar.getInstance();
    protected DatabaseReference mDataBase;
    int count;
    FirebaseUser user = AccountFragment.mAuth.getCurrentUser();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.task_create_fragment, container, false);
        init();
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getContext(), t,
                        TimeAndDate.get(Calendar.HOUR_OF_DAY),
                        TimeAndDate.get(Calendar.MINUTE), true)
                        .show();
            }
        });
        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), d,
                        TimeAndDate.get(Calendar.YEAR),
                        TimeAndDate.get(Calendar.MONTH),
                        TimeAndDate.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FieldsOk()){
                    mDataBase = FirebaseDatabase.getInstance().getReference("Tasks");
                    mDataBase.child(user.getUid()).child("items").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful()){
                                count = Integer.parseInt(task.getResult().getValue().toString());
                                createItems();
                            }
                        }
                    });


                    //MainActivity.context.startService(new Intent(MainActivity.context, notifyService.class));

        calendar.set(Calendar.DAY_OF_MONTH, 8);
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.YEAR, 2022);

                    ////alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
                   //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 100, alarmIntent);
                    //ArrList.items.add(new Item(date, task));
                    ////getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();
                }

            }
        });
        return view;
    }

    private void createItems(){
        time = new StringBuilder().append(DateUtils.formatDateTime(getContext(),
                TimeAndDate.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_TIME)).toString();
        date = new StringBuilder().append("   ").append(DateUtils.formatDateTime(getContext(),
                TimeAndDate.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR)).toString();
        task = "   "+et_task.getText();
        task = new StringBuilder().append(task).toString();
        isOK = true;
        mDataBase.child(user.getUid()).child("items").setValue(count+1);
       // mDataBase.child(user.getUid()).child(String.valueOf(count)).setValue(new TaskItem(date, time, task, "true"));
        char[] hr = DateUtils.formatDateTime(getContext(),
                TimeAndDate.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_TIME).toCharArray();
        AlarmManager alarmMgr = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent1 = new Intent(getContext(), AlarmReceiver.class);
        intent1.putExtra("Task", et_task.getText().toString());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), (int) System.currentTimeMillis(), intent1, PendingIntent.FLAG_ONE_SHOT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf((hr[0]-48)*10+hr[1]-48));
        calendar.set(Calendar.MINUTE, Integer.valueOf((hr[3]-48)*10+hr[4]-48));
        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();
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

    private void setInitialTime(){
        tv_time.setText(DateUtils.formatDateTime(getContext(),
                TimeAndDate.getTimeInMillis(),
                 DateUtils.FORMAT_SHOW_TIME));
    }
    private void setInitialDate(){
        tv_date.setText(DateUtils.formatDateTime(getContext(),
                TimeAndDate.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            TimeAndDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
            TimeAndDate.set(Calendar.MINUTE, minute);
            setInitialTime();
        }
    };
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            TimeAndDate.set(Calendar.YEAR, year);
            TimeAndDate.set(Calendar.MONTH, monthOfYear);
            TimeAndDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDate();
        }
    };

    private void init(){
        tv_date = view.findViewById(R.id.tV_date);
        create = view.findViewById(R.id.create_btn);
        et_task = view.findViewById(R.id.create_task);
        setTime = view.findViewById(R.id.set_time_btn);
        setDate = view.findViewById(R.id.set_date_btn);
        tv_time = view.findViewById(R.id.tV_time);
    }
    private int getMonth(String date){
        //todo
        return 0;
    }
    */
}


