package edu.skku.capstone.justpay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {

    Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username_et = findViewById(R.id.input_id);
                EditText password_et = findViewById(R.id.input_ps);
                if (UserLoggedIn.LoginCheck(username_et.getText().toString(), password_et.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "로그인에 성공하였습니다."+UserLoggedIn.getUser(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, RoomListActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "아이디 또는 비밀번호가 잘못되었습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onClick(View view) {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }
}
