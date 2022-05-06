package com.example.logpass.old;

import android.content.Intent;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/*
public class LogPassValid {
    protected static boolean isSigned = false;
    private String log;
    private String pass;
    //private static FirebaseAuth auth = FirebaseAuth.getInstance();


    protected LogPassValid(String log, String pass) {
        this.log = log;
        this.pass = pass;
    }

    protected void signIn() {
        if(!log.equals("")&&!pass.equals("")) {
            auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(log, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        isSigned = true;

                    }
                    else{
                        isSigned = false;
                    }
                }
            });

        }
    }

    protected static boolean isSign(){
        if (AccountFragment.auth.getCurrentUser() != null) {

           return true;
        }
        else {
            return false;
        }
    }
}

  */


















        /*
    private static boolean isSigned = false;
    public static boolean signIn(String email, String password){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // Already signed in
            // Do nothing
        } else {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                isSigned = true;
                            }
                            else{
                                isSigned = false;
                            }
                        }
                    });
        }
        return isSigned;
    }

*/

    /*
    private static final String TAG = "EmailPassword";

    private static FirebaseAuth mAuth;

    private static FirebaseAuth.AuthStateListener mAuthListener;

    static final String log1 = "User001";//заданный логин
    static final String pass1 = "123456";//заданный пароль
    static boolean checkValid(String log, String pass){


        boolean out;
        if(!log.equals(log1)||!pass.equals(pass1))
            out = false;
        else
            out = true;
        return out;

    }*/