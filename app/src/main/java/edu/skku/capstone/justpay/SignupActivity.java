package edu.skku.capstone.justpay;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    EditText et_pw, et_pw_chk;
    TextView s_em, s_pw, s_pw_chk;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        /*assert actionBar != null;
        actionBar.show();
        actionBar.setTitle("회원가입");*/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#06194e")));




        et_pw = findViewById(R.id.input_ps);
        et_pw_chk = findViewById(R.id.input_ps_check);

        s_em = findViewById(R.id.text_email_val);
        s_pw = findViewById(R.id.text_ps_val);
        s_pw_chk = findViewById(R.id.text_ps_check_val);

        // Password check
        et_pw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String text = et_pw.getText().toString();
                if(text==null || text.length() <8 || text.length() > 16){
                    s_pw.setText("비밀번호 형식을 지켜주세요");
                    s_pw.setTextColor(Color.RED);
                }
                else{
                    s_pw.setText("올바른 비밀번호 형식입니다");
                    s_pw.setTextColor(Color.BLACK);
                }
            }
        });
        // Password rewritten check
        et_pw_chk.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text1 = et_pw_chk.getText().toString();
                String text2 = et_pw.getText().toString();
                if(text1.equals(text2)){
                    s_pw_chk.setText("비밀번호가 일치합니다");
                    s_pw_chk.setTextColor(Color.BLACK);
                }
                else{
                    s_pw_chk.setText("비밀번호가 일치하지 않습니다");
                    s_pw_chk.setTextColor(Color.RED);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        final EditText emailval = (EditText) findViewById(R.id.input_email);
        final TextView EmailOutput = findViewById(R.id.text_email_val);


        //email check
        emailval.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text=s.toString();
                if (Patterns.EMAIL_ADDRESS.matcher(text).matches()){
                    EmailOutput.setText("올바른 이메일 형식입니다");
                    EmailOutput.setTextColor(Color.BLACK);

                }
                else {
                    EmailOutput.setText("올바른 이메일 형식이 아닙니다");
                    EmailOutput.setTextColor(Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //When cancel
        Button btn1 = (Button)findViewById(R.id.btn_signup_cancel);
        btn1.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });
        // When confirm
        Button btn2 = (Button)findViewById(R.id.btn_signup_confirm);
        btn2.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });
    }

}
