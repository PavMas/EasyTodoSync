package com.example.logpass.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.logpass.MainActivity;
import com.example.logpass.R;
import com.example.logpass.classes.RegUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterFragment extends Fragment {

    protected DatabaseReference mDataBase;
    protected String USER_KEY;
    Button regBtn1;


    private EditText et_name;
    private EditText et_surname;
    private EditText et_post;
    private EditText et_password;
    private TextView tv_err;

    View view;

    private String name;
    private String surname;
    private String email;
    private String password;

    SharedPreferences mPrefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.register_activity,
                container, false);
        init();
        regBtn1.setOnClickListener(v -> {
            name = et_name.getText().toString();
            surname = et_surname.getText().toString();
            email = et_post.getText().toString();
            password = et_password.getText().toString();
            if (FieldsOk()) {
                tv_err.setTextColor(Color.parseColor("#D50A0A"));
                tv_err.setText("Данный логин занят или Вы ошиблись при вводе");
            } else {
                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                mPrefs = requireActivity().getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = mPrefs.edit();
                                USER_KEY = FirebaseAuth.getInstance().getUid();
                                mDataBase = FirebaseDatabase.getInstance().getReference("Tasks");
                                mDataBase.child(USER_KEY).child("items").setValue(0);
                                mDataBase.child(USER_KEY).child("version").setValue(0);
                                Toast.makeText(getActivity(), "Регестрация прошла успешно", Toast.LENGTH_LONG).show();
                                mDataBase = FirebaseDatabase.getInstance().getReference("Users");
                                mDataBase.child(USER_KEY).setValue(new RegUser(name, surname, USER_KEY));
                                editor.putString("dbname", USER_KEY);
                                editor.putLong(USER_KEY + "version", System.currentTimeMillis()).apply();
                                MainActivity.DATABASE_NAME = USER_KEY;
                                ((MainActivity) requireContext()).updateUser();
                                et_name.getText().clear();
                                et_surname.getText().clear();
                                et_post.getText().clear();
                                et_password.getText().clear();
                                ((AppCompatActivity) requireContext()).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.fragment_container, new InFragment()).commit();
                            } else {
                                tv_err.setTextColor(Color.parseColor("#D50A0A"));
                                tv_err.setText("Данный логин занят или Вы ошиблись при вводе");
                            }
                        });
            }
        });

        return view;
    }

    private void init() {
        regBtn1 = view.findViewById(R.id.regBtn);
        et_name = view.findViewById(R.id.et_name);
        et_surname = view.findViewById(R.id.et_surname);
        et_post = view.findViewById(R.id.et_email);
        et_password = view.findViewById(R.id.et_pass);
        tv_err = view.findViewById(R.id.tv_err);
    }


    private boolean FieldsOk() {
        return email.equals("") || password.equals("") || name.equals("") || surname.equals("");
    }
}
