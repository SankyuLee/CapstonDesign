package edu.skku.capstone.justpay;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    EditText et_pw, et_pw_chk, et_ph, et_nn;
    TextView s_em, s_pw, s_pw_chk;

    int bit1 = 1;
    int bit2 = 1;
    int bit3 = 1;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Toolbar tb = findViewById(R.id.signup_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        et_nn = findViewById(R.id.input_nickname);
        et_ph = findViewById(R.id.input_phone);
        et_ph.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
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
                if(isValidPassword(text)) {
                    s_pw.setText("올바른 비밀번호 형식입니다");
                    s_pw.setTextColor(Color.BLACK);
                    bit1 = 0;

                }
                else{
                    s_pw.setText("올바르지 못한 비밀번호 형식입니다");
                    s_pw.setTextColor(Color.RED);
                    bit1 = 1;
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
                    bit2 = 0;
                }
                else{
                    s_pw_chk.setText("비밀번호가 일치하지 않습니다");
                    s_pw_chk.setTextColor(Color.RED);
                    bit2 = 1;
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
                    bit3 = 0;

                }
                else {
                    EmailOutput.setText("올바른 이메일 형식이 아닙니다");
                    EmailOutput.setTextColor(Color.RED);
                    bit3 = 1;
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
                //입력한 항목 모두 검증
                if (bit1 == 0 && bit2 == 0 && bit3 ==0 && !et_nn.getText().toString().matches("")) {
                    String email = ((EditText) findViewById(R.id.input_email)).getText().toString();
                    String pw = et_pw.getText().toString();
                    String nickname = ((EditText) findViewById(R.id.input_nickname)).getText().toString();
                    String phone = ((EditText) findViewById(R.id.input_phone)).getText().toString();

                    JSONObject sql_result = new SQLSender().sendSQL("INSERT into users(email, password, phone, nickname) values('"
                            +email+"','"+pw+"','"+phone+"','"+nickname+"');");

                    try {
                        if (sql_result.getBoolean("isError")) {
                            if (sql_result.getJSONObject("result").getString("code").equals(Constant.ER_DUP_ENTRY)) {
                                EmailOutput.setText("이메일이 중복됩니다.");
                                EmailOutput.setTextColor(Color.RED);
                            } else {
                                // 알 수 없는 오류
                                Toast.makeText(SignupActivity.this, "알 수 없는 오류가 발생하였습니다."+sql_result.getJSONObject("result"), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // 회원가입 성공
                            Toast.makeText(SignupActivity.this, "회원가입에 성공하였습니다. 다시 로그인해 주세요.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast toast =Toast.makeText(getApplicationContext(),"항목을 모두 올바르게 입력해주세요",Toast.LENGTH_LONG);
                    toast.show();
                }


            }
        });
    }
    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+!`~*=])(?=\\S+$).{8,16}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();

    }

}
