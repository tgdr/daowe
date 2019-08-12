package edu.buu.daowe.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import edu.buu.daowe.R;
import edu.buu.daowe.fragment.Register_one_Fragment;

public class RegisterActivity extends AppCompatActivity {
    FragmentManager manager;
    FragmentTransaction transaction;
    boolean smssuccess = false;
    TextView tvsmscall;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_register);
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.reg_frame, new Register_one_Fragment());
        transaction.commit();


    }


}