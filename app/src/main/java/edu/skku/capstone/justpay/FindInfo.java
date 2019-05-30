package edu.skku.capstone.justpay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class FindInfo extends AppCompatActivity {
    EditText textph, textem;
    Button findid, findps, returnLogin;

    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_findinfo);

        Toolbar tb = findViewById(R.id.signup_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        textph = findViewById(R.id.find_id);
        textem = findViewById(R.id.find_ps);
        findid = findViewById(R.id.btn_id);
        findps = findViewById(R.id.btn_ps);
        returnLogin = findViewById(R.id.btn_find_cancel);

        findid.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String phone = textph.getText().toString();
                JSONObject sql_find_by_ph = new SQLSender().sendSQL("select email from users where phone='" +phone +"';");

                try {
                    if(sql_find_by_ph.getBoolean("isError")) {
                        Toast.makeText(FindInfo.this, "해당하는 아이디가 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(FindInfo.this,"회원님의 아이디는 "+sql_find_by_ph.getJSONArray("result").getJSONObject(0).getString("email")+" 입니다.",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        findps.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String email = textem.getText().toString();
                JSONObject sql_find_by_ph = new SQLSender().sendSQL("select password from users where email='" +email +"';");

                try {
                    if(sql_find_by_ph.getBoolean("isError")) {
                        Toast.makeText(FindInfo.this, "해당하는 계정이 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(FindInfo.this,"회원님의 비밀번호는 "+sql_find_by_ph.getJSONArray("result").getJSONObject(0).getString("password")+" 입니다.",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
        returnLogin.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v){
                finish();
            }
        });


    }
}
