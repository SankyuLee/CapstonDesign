package edu.skku.capstone.justpay;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {

    Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etid = findViewById(R.id.input_id);
        EditText etps = findViewById(R.id.input_pw);
        CheckBox idSave = findViewById(R.id.option_id_store);
        CheckBox autolog = findViewById(R.id.option_auto_login);

        etid.requestFocus();

        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        String id = pref.getString("id_save","");
        String pw = pref.getString("pw_save","");
        Boolean chk_id = pref.getBoolean("chk_id",false);
        Boolean chk_auto = pref.getBoolean("chk_auto",false);

        if (chk_id == true) {
            etid.setText(id);
            idSave.setChecked(chk_id);
        }
        if (chk_auto == true) {
            etid.setText(id);
            etps.setText(pw);
            autolog.setChecked(chk_auto);
        }

        btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email_et = findViewById(R.id.input_id);
                EditText password_et = findViewById(R.id.input_pw);
/*
                if (UserLoggedIn.LoginCheck(email_et.getText().toString(), password_et.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "로그인에 성공하였습니다."+UserLoggedIn.getUser(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, RoomListActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "아이디 또는 비밀번호가 잘못되었습니다.", Toast.LENGTH_LONG).show();
                }
*/
                Intent intent = new Intent(LoginActivity.this, RoomListActivity.class);
                startActivity(intent);
            }
        });

        TextView link_signup = findViewById(R.id.link_signup);
        link_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }


}
