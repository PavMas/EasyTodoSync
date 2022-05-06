package com.example.logpass;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    protected DatabaseReference mDataBase;
    protected String USER_KEY;
    Button regBtn1;

    RadioButton btMain, btSub;

    private EditText et_name;
    private EditText et_surname;
    private EditText et_post;
    private EditText et_password;
    private TextView tv_err;

    View view;

    private boolean role;
    private boolean role1 = false;
    private String name;
    private String surname;
    private String email;
    private String password;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.register_activity,
                container, false);
        init();
        regBtn1.setOnClickListener(this);
        btMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                role = true;
                role1 = true;
            }
        });
        btSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                role = false;
                role1 = true;
            }
        });

        return view;
    }

    private void init() {
        regBtn1 = (Button) view.findViewById(R.id.regBtn);
        et_name = (EditText) view.findViewById(R.id.et_name);
        et_surname = (EditText) view.findViewById(R.id.et_surname);
        et_post = (EditText) view.findViewById(R.id.et_email);
        et_password = (EditText) view.findViewById(R.id.et_pass);
        tv_err = (TextView) view.findViewById(R.id.tv_err);
        btMain = (RadioButton) view.findViewById(R.id.bt_r_main);
        btSub = (RadioButton) view.findViewById(R.id.bt_r_sub);
    }
    @Override
    public void onClick(View v) {
        /*
        int id = v.getId();
        switch (id) {
            case R.id.regBtn:
         */
                name = et_name.getText().toString();
                surname = et_surname.getText().toString();
                email = et_post.getText().toString();
                password = et_password.getText().toString();
                if (FieldsOk()) {
                    tv_err.setTextColor(Color.parseColor("#D50A0A"));
                    tv_err.setText("Данный логин занят или Вы ошиблись при вводе");
                }
                else
                {
                    FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        AccountFragment.user = AccountFragment.mAuth.getCurrentUser();
                                        mDataBase = FirebaseDatabase.getInstance().getReference("Tasks");
                                        mDataBase.child(AccountFragment.user.getUid()).child("items").setValue(0);
                                        Toast.makeText(getActivity(), "Регестрация прошла успешно", Toast.LENGTH_LONG).show();
                                        USER_KEY = AccountFragment.user.getUid();
                                        mDataBase = FirebaseDatabase.getInstance().getReference("Users");
                                        mDataBase.child(USER_KEY).setValue(new RegUser(name, surname, getRole(role), USER_KEY));
                                        et_name.getText().clear();
                                        et_surname.getText().clear();
                                        et_post.getText().clear();
                                        et_password.getText().clear();
                                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new InActivity()).commit();
                                    } else {
                                        tv_err.setTextColor(Color.parseColor("#D50A0A"));
                                        tv_err.setText("Данный логин занят или Вы ошиблись при вводе");
                                    }
                                }
                            });
                }
               // break;
        }
    private String getRole(boolean role){
        if(role)
            return "High";
        else
            return "Low";
    }
    private boolean FieldsOk(){
        return email.equals("") || password.equals("") || name.equals("") || surname.equals("") || !role1;
    }
}
