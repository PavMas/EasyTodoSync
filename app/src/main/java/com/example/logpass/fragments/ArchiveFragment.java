package com.example.logpass.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.logpass.DB.AppDB;
import com.example.logpass.MainActivity;
import com.example.logpass.R;
import com.example.logpass.adapters.ArchAdapter;
import com.example.logpass.classes.TaskItem;

import java.util.List;

public class ArchiveFragment extends Fragment {

    private View view;
    private RecyclerView rv;
    private List<TaskItem> items;
    private ArchAdapter adapter;
    AppDB database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.archive_fragment, container, false);
        setHasOptionsMenu(true);
        init();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setManagerAndAdapter();
        getArchiveItems();
    }

    private void init() {
        rv = view.findViewById(R.id.archive);
        database = Room.databaseBuilder(requireContext(), AppDB.class, MainActivity.DATABASE_NAME).build();
    }

    private void setManagerAndAdapter() {
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new ArchAdapter();
        rv.setAdapter(adapter);
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

    private void getArchiveItems() {
        @SuppressLint("NotifyDataSetChanged") Thread thread = new Thread(() -> {
            items = database.itemDao().getCompleted();
            requireActivity().runOnUiThread(() -> {
                adapter.setList(items);
                adapter.notifyDataSetChanged();
                database.close();
            });
        });
        thread.start();
    }

}
