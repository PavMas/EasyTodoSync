package com.example.logpass.fragments;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.logpass.MainActivity;
import com.example.logpass.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AccountFragment extends Fragment implements View.OnClickListener {
    EditText editLog;
    EditText editPass;
    Button signIn_btn;
    TextView msg;
    Button goRegBtn;
    View view;
    Context context;
    protected static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    protected static FirebaseUser user;
    private String pass;
    private String log;

    SharedPreferences mPrefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account,
                container, false);
        init();
        goRegBtn.setOnClickListener(this);
        signIn_btn.setOnClickListener(this);
        return view;
    }

    private void init() {
        editLog = view.findViewById(R.id.id_log);
        editPass = view.findViewById(R.id.id_pass);
        signIn_btn = view.findViewById(R.id.signIn_btn);
        msg = view.findViewById(R.id.id_msg);
        goRegBtn = view.findViewById(R.id.goReg);
        context = requireContext();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.goReg:
                ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragment_container, new RegisterFragment()).commit();
                break;
            case R.id.signIn_btn:
                pass = editPass.getText().toString();
                log = editLog.getText().toString();
                mAuth = FirebaseAuth.getInstance();
                if (FieldsOk()) {
                    mPrefs = context.getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mPrefs.edit();
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.signInWithEmailAndPassword(log, pass).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            assert user != null;
                            editor.putString("dbname", user.getUid());
                            editor.putLong(user.getUid() + "version", 0).apply();
                            MainActivity.DATABASE_NAME = user.getUid();
                            ((MainActivity) context).updateUser();
                            ((MainActivity) context).startThread();
                            ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.fragment_container, new InFragment()).commit();
                        } else {
                            msg.setTextColor(Color.parseColor("#D50A0A"));
                            msg.setText("Неверный логин или пароль");
                        }
                    });
                } else {
                    msg.setTextColor(Color.parseColor("#D50A0A"));
                    msg.setText("Данные введены некоректно");
                }
                break;
        }
    }

    protected static boolean isSign() {
        if (mAuth == null)
            mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser() != null;
    }

    private boolean FieldsOk() {
        return !log.equals("") && !pass.equals("") && mAuth.getCurrentUser() == null;

    }
}
