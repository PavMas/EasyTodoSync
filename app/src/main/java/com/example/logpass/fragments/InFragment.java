package com.example.logpass.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.logpass.MainActivity;
import com.example.logpass.R;
import com.example.logpass.fragments.AccountFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InFragment extends Fragment {

    View view;

    Button exit;

    TextView tv_name, tv_surname, tv_role;

    private final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_in,
                container, false);
        init();
        exit.setOnClickListener(v -> {
            SharedPreferences mPrefs = requireActivity().getSharedPreferences("myprefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.remove("dbname").apply();
            MainActivity.DATABASE_NAME = "defaultDB";
            FirebaseAuth.getInstance().signOut();
            AccountFragment.mAuth = null;
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new AccountFragment()).commit();
            Toast.makeText(getActivity(), "Вы вышли из аккаунта", Toast.LENGTH_LONG).show();
        });
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                print(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return view;

    }

    private void init(){
        exit = view.findViewById(R.id.bt_signOut);
        tv_name = view.findViewById(R.id.tV_name);
        tv_surname = view.findViewById(R.id.tv_sur);
        tv_role = view.findViewById(R.id.tV_role);
    }
    @SuppressLint("SetTextI18n")
    private void print(DataSnapshot snapshot){
        tv_name.setText("Имя: " + snapshot.child("Users").child(user.getUid()).child("name").getValue(String.class));
        tv_surname.setText("Фамилия: " +snapshot.child("Users").child(user.getUid()).child("surname").getValue(String.class));
        tv_role.setText("Роль: "+snapshot.child("Users").child(user.getUid()).child("role").getValue(String.class));
    }

    }