package com.example.logpass;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InActivity extends Fragment {

    View view;

    Button exit;

    TextView tv_name, tv_surname, tv_role;

    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_in,
                container, false);
        init();
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                AccountFragment.mAuth = null;
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new AccountFragment()).commit();
                Toast toast = Toast.makeText(getActivity(), "Вы вышли из аккаунта", Toast.LENGTH_LONG);
                toast.show();
            }
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
        exit = (Button) view.findViewById(R.id.bt_signOut);
        tv_name = (TextView) view.findViewById(R.id.tV_name);
        tv_surname = (TextView) view.findViewById(R.id.tv_sur);
        tv_role = (TextView) view.findViewById(R.id.tV_role);
    }
    private void print(DataSnapshot snapshot){
        tv_name.setText("Имя: " + snapshot.child("Users").child(AccountFragment.mAuth.getUid()).child("name").getValue(String.class));
        tv_surname.setText("Фамилия: " +snapshot.child("Users").child(AccountFragment.mAuth.getUid()).child("surname").getValue(String.class));
        tv_role.setText("Роль: "+snapshot.child("Users").child(AccountFragment.mAuth.getUid()).child("role").getValue(String.class));
    }

    }