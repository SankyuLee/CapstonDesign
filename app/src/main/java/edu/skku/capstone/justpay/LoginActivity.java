package edu.skku.capstone.justpay;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();




    }
    public void onClick(View view) {
        TextView b = findViewById(R.id.link_signup);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(),SignupActivity.class);
                startActivity(intent);
            }

        });
    }


}
