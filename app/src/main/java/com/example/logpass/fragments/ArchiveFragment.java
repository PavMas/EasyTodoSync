package com.example.logpass.fragments;

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

import com.example.logpass.R;
import com.example.logpass.adapters.ArchAdapter;
import com.example.logpass.classes.TaskItem;
import com.example.logpass.adapters.MainAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ArchiveFragment extends Fragment {

    private View view;
    private RecyclerView rv;
    DatabaseReference mDataBase;
    private List<TaskItem> items = new ArrayList<>();
    private ArchAdapter listAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.archive_fragment, container, false);
        setHasOptionsMenu(true);
        rv = view.findViewById(R.id.archive);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDataBase = FirebaseDatabase.getInstance().getReference("Tasks");
        setManagerAndAdapter();
        //mDataBase.addValueEventListener(vListener);
    }
    /*
    ValueEventListener vListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(items.size() > 0)
                items.clear();
            for (DataSnapshot ds : snapshot.child(AccountFragment.user.getUid()).child("archive").getChildren()) {
                ds.child("Tasks").child(AccountFragment.user.getUid()).child(ds.getKey());
                TaskItem tItem = new TaskItem(ds.child("date").getValue(String.class), ds.child("time").getValue(String.class), ds.child("task").getValue(String.class), ds.child("enable").getValue(String.class), ds.child("id").getValue(String.class), ds.child("description").getValue(String.class), ds.child("done").getValue(String.class));
                items.add(tItem);
                //updateItem(tItem);
            }
            setManagerAndAdapter();
            listAdapter.notifyDataSetChanged();
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            mDataBase.addValueEventListener(vListener);
        }
    };

     */

    private void setManagerAndAdapter() {
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        listAdapter = new ArchAdapter(getContext());
        //listAdapter.setItems(items);
        rv.setAdapter(listAdapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.archive_menu_back, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ((AppCompatActivity)getContext()).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.fragment_container, new MainFragment()).commit();
        getActivity().setTitle("EasyTodo: Задачи");
        return super.onOptionsItemSelected(item);
    }

}
