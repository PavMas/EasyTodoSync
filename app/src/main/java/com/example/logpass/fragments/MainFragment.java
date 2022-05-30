package com.example.logpass.fragments;


import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.logpass.DB.AppDB;
import com.example.logpass.MainActivity;
import com.example.logpass.R;
import com.example.logpass.adapters.MainAdapter;
import com.example.logpass.classes.TaskItem;
import com.example.logpass.fragments.dialogs.TaskEditDialogMD;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

public class MainFragment extends Fragment {


    View view;
    private RecyclerView rv;
    private AppCompatButton add;
    private MainAdapter adapter;
    public static String CHANNEL_ID = "Main channel";
    static PendingIntent contentIntent;
    DatabaseReference mDataBase;
    private AppDB database;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Context context;


    SharedPreferences preferences;

    List<TaskItem> list;

    private ItemTouchHelper.SimpleCallback simpleCallback;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.archive_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (AccountFragment.isSign()) {
            ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragment_container, new ArchiveFragment()).commit();
            requireActivity().setTitle("EasyTodo: Архив");
        } else
            Toast.makeText(getContext(), "Войдите в аккаунт", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
        context = getContext();
        Intent intent = new Intent(getContext(), MainActivity.class);
        contentIntent = PendingIntent.getActivity(getContext(),
                0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        createNotificationChannel();
        init();
        setManagerAndAdapter();
        getTasks();
        simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    int position = viewHolder.getAdapterPosition();
                    deleteItem(list.get(position));
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rv);
        if (user != null) {
            mDataBase = FirebaseDatabase.getInstance().getReference("Tasks");
            add.setOnClickListener(v -> TaskEditDialogMD.display(((AppCompatActivity) context).getSupportFragmentManager(), getContext()));
        } else
            Toast.makeText(getContext(), "Пожалуйста, войдите в аккаунт или зарегестрируйтесь", Toast.LENGTH_LONG).show();
        return view;
    }

    private void init() {
        add = view.findViewById(R.id.addTask);
        rv = view.findViewById(R.id.recycler_create);
        database = Room.databaseBuilder(requireContext(), AppDB.class, MainActivity.DATABASE_NAME).build();
        preferences = requireContext().getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
    }


    private void setManagerAndAdapter() {
        rv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new MainAdapter(requireContext());
        rv.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        CharSequence name = "Tasks";
        String description = "Task";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @SuppressLint("NotifyDataSetChanged")
    protected void update() {
        adapter.notifyDataSetChanged();
    }

    public void getTasks() {
        @SuppressLint("NotifyDataSetChanged") Thread thread = new Thread(() -> {
            list = database.itemDao().getUncompleted();
            requireActivity().runOnUiThread(() -> {
                adapter.setList(list);
                adapter.notifyDataSetChanged();
            });
        });
        thread.start();
    }

    private void deleteItem(TaskItem item) {
        @SuppressLint("NotifyDataSetChanged") Thread thread = new Thread(() -> {
            item.arch = "true";
            item.done = "false";
            database.itemDao().update(item);
            SharedPreferences.Editor editor = preferences.edit();
            long ver = System.currentTimeMillis();
            if (MainActivity.hasConnection(context)) {
                mDataBase.child(user.getUid()).child(item.id).child("done").setValue("false");
                mDataBase.child(user.getUid()).child(item.id).child("arch").setValue("true");
                mDataBase.child(user.getUid()).child("version").setValue(ver);
            }
            editor.putLong(MainActivity.DATABASE_NAME + "version", ver).apply();
            requireActivity().runOnUiThread(() -> {
                list.remove(item);
                adapter.setList(list);
                adapter.notifyDataSetChanged();
            });
        });
        thread.start();
    }
}
