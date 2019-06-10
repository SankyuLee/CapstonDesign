package edu.skku.capstone.justpay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.kakao.auth.Session;
import com.kakao.usermgmt.LoginButton;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.transform.Result;


public class LoginActivity extends AppCompatActivity {
    private boolean saveLoginData;
    private String str_id;
    private String str_ps;
    private CheckBox checkBox;
    private SharedPreferences appData;
    private Button btn_login;
    private EditText etid;
    private EditText etps;
    private Context mContext;
    private LoginButton btn_kakao_login;
    private ISessionCallback callback;
    private int eventId;
    private int roomId;
    private boolean KakaoPath = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = getApplicationContext();

        // kakao login -> default = false
        //SharedPreferences.Editor editor  = appData.edit();
        //editor.putBoolean("kakao",false);
        try{

            Intent intent = getIntent(); /*데이터 수신*/
            KakaoPath = intent.getBooleanExtra("KakaoPath",false);
            roomId = intent.getExtras().getInt("roomId"); /*int형*/
            eventId = intent.getExtras().getInt("eventId"); /*int형*/
        }
        catch (NullPointerException e) {
            Log.e("Exception", "NullPointerException occurred in LoginAcitivity.java");
            e.printStackTrace();
        }


        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String some = new String(Base64.encode(md.digest(),0));
                Log.e("Hash : ",some);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Kakao login
        btn_kakao_login = (LoginButton) findViewById(R.id.btn_kakao_login);
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);




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
                    if(KakaoPath)
                    {
                        intent = new Intent(LoginActivity.this, ResultActivity1.class);
                        intent.putExtra("roomId", roomId);
                        intent.putExtra("eventId", eventId);
                    }
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

    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback{

        @Override
        public void onSessionOpened() {
            redirectSignupActivity();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Logger.e(exception);
            }
            setContentView(R.layout.activity_login);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void redirectSignupActivity() {
        final Intent intent = new Intent(this, KakaoSignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

}