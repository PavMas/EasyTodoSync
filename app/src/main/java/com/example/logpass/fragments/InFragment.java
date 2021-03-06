package com.example.logpass.fragments;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.logpass.MainActivity;
import com.example.logpass.R;
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

    TextView tv_name, tv_surname;

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
            ((MainActivity)requireContext()).updateUser();
            ((AppCompatActivity) requireContext()).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.fragment_container, new AccountFragment()).commit();
            Toast.makeText(getActivity(), "???? ?????????? ???? ????????????????", Toast.LENGTH_LONG).show();
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

    private void init() {
        exit = view.findViewById(R.id.bt_signOut);
        tv_name = view.findViewById(R.id.tV_name);
        tv_surname = view.findViewById(R.id.tv_sur);
    }

    @SuppressLint("SetTextI18n")
    private void print(DataSnapshot snapshot) {
        tv_name.setText("??????: " + snapshot.child("Users").child(user.getUid()).child("name").getValue(String.class));
        tv_surname.setText("??????????????: " + snapshot.child("Users").child(user.getUid()).child("surname").getValue(String.class));

    }

}