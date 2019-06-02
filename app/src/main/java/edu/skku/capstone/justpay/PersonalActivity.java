package edu.skku.capstone.justpay;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class PersonalActivity extends AppCompatActivity {
    FrameLayout container;

    //USER INFO
    int user_id;
    String user_email;
    String user_nickname;
    String user_pw;
    String user_phone;

    Context context;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private PwConfirmFragment pwConfirmFragment = new PwConfirmFragment();
    private ReviseInfoFragment reviseInfoFragment = new ReviseInfoFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        setUser();

        container = (FrameLayout)findViewById(R.id.container);
        context = this;

        fragmentManager.beginTransaction().add(R.id.container, pwConfirmFragment,"1").commit();
        fragmentManager.beginTransaction().add(R.id.container, reviseInfoFragment,"2").hide(reviseInfoFragment).commit();
    }

    public void reviseUserInfo(){
        fragmentManager.beginTransaction().hide(pwConfirmFragment).show(reviseInfoFragment).commit();
    }

    public void finishActivity(){
        Intent intent = new Intent(context, RoomListActivity.class);
        startActivity(intent);
        finish();
    }

    public void setUser(){
        JSONObject user_info = null;
        try{
            user_info = UserLoggedIn.getUser();

            user_id = user_info.getInt("id");
            user_email = user_info.getString("email");
            user_nickname = user_info.getString("nickname");
            user_pw = user_info.getString("password");
            user_phone = user_info.getString("phone");

        }catch(NullPointerException | JSONException e){
            e.printStackTrace();
        }
    }
}
