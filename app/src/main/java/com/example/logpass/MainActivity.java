package com.example.logpass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
/*
    EditText editLog;
    EditText editPass;
    Button btn;
    TextView msg;
    Button goRegBtn;
    private DatabaseReference mDataBase;
    private final String USER_KEY = "Пользователи";
    Button regBtn;
    private EditText et_name;
    private EditText et_surname;
    private EditText et_post;
    private EditText et_password;
    private TextView tv_err;

 */

    @SuppressLint("StaticFieldLeak")
    protected static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("EasyTodo: Задачи");
        AccountFragment.user = AccountFragment.mAuth.getCurrentUser();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();

        BottomNavigationView bottonNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottonNav.setOnNavigationItemSelectedListener(navListener);
        context = getApplicationContext();


    }

    @SuppressLint("NonConstantResourceId")
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        int anim = 0, anim1 = 0;
        Fragment selectedFragment = null;
        switch (item.getItemId()){
            case R.id.page_1:
                setTitle("EasyTodo: Задачи");
                selectedFragment =  new MainFragment();
                anim = R.anim.slide_in_left;
                anim1 = R.anim.slide_out_right;
                break;
            case R.id.page_2:
                if(!AccountFragment.isSign()) {
                    setTitle("EasyTodo: Вход в аккаунт");
                    selectedFragment = new AccountFragment();
                }
                else {
                    setTitle("EasyTodo: Личный кабинет");
                    selectedFragment = new InActivity();
                }
                anim = R.anim.slide_in_right;
                anim1 = R.anim.slide_out_left;
                break;
        }
        assert selectedFragment != null;
        getSupportFragmentManager().beginTransaction().setCustomAnimations(anim, anim1).replace(R.id.fragment_container, selectedFragment).commit();
        return  true;
    };
    public static boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }
}