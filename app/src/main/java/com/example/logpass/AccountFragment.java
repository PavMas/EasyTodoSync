package com.example.logpass;

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
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class AccountFragment extends Fragment implements View.OnClickListener {
    EditText editLog;
    EditText editPass;
    Button signIn_btn;
    TextView msg;
    Button goRegBtn;
    View view;
    protected static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    protected static FirebaseUser user;
    private String pass;
    private String log;

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
        editLog = (EditText) view.findViewById(R.id.id_log);
        editPass = (EditText) view.findViewById(R.id.id_pass);
        signIn_btn = (Button) view.findViewById(R.id.signIn_btn);
        msg = (TextView) view.findViewById(R.id.id_msg);
        goRegBtn = (Button) view.findViewById(R.id.goReg);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.goReg:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new RegisterFragment()).commit();
                break;
            case R.id.signIn_btn:
                pass = editPass.getText().toString();
                log = editLog.getText().toString();
                if (FieldsOk()) {
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.signInWithEmailAndPassword(log, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                user = mAuth.getCurrentUser();
                                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new InActivity()).commit();
                            } else {
                                msg.setTextColor(Color.parseColor("#D50A0A"));
                                msg.setText("Неверный логин или пароль");
                            }
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
        return mAuth.getCurrentUser() != null;
    }

    private boolean FieldsOk() {
        return !log.equals("") && !pass.equals("") && mAuth.getCurrentUser() == null;

    }
}
