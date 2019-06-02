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

import org.w3c.dom.Text;


public class LoginActivity extends AppCompatActivity {
    private boolean saveLoginData;
    private String str_id;
    private String str_ps;
    private CheckBox checkBox;
    private SharedPreferences appData;
    private Button btn_login;
    private EditText etid;
    private EditText etps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        appData = getSharedPreferences("appData",MODE_PRIVATE);
        saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA",false);
        str_id = appData.getString("id","");
        str_ps = appData.getString("ps","");

        checkBox = findViewById(R.id.option_auto_login);
        etid = findViewById(R.id.input_id);
        etps = findViewById(R.id.input_pw);

        //etid.requestFocus();

       if (saveLoginData) {
           etid.setText(str_id);
           etps.setText(str_ps);
           checkBox.setChecked(saveLoginData);
       }


        btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor  = appData.edit();

                editor.putBoolean("SAVE_LOGIN_DATA",checkBox.isChecked());
                editor.putString("id",etid.getText().toString().trim());
                editor.putString("ps",etps.getText().toString().trim());
                editor.commit();

                if (UserLoggedIn.LoginCheck(etid.getText().toString(), etps.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "로그인에 성공하였습니다."+UserLoggedIn.getUser(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, RoomListActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "아이디 또는 비밀번호가 잘못되었습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

        TextView link_find = findViewById(R.id.link_find_id);
        link_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, FindInfo.class);
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
