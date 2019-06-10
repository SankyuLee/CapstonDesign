package edu.skku.capstone.justpay;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.helper.log.Logger;


public class KakaoSignupActivity extends Activity {
    String username = null;
    private SharedPreferences appData;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestMe();
    }

    protected void requestMe() {
        UserManagement.getInstance().me(new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "Failed to get user info. msg = " + errorResult;
                Logger.d(message);
                Log.v("fail", "fail");

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    finish();
                }
                else {
                    redirectLoginActivity(); // when failed
                }
            }
            @Override
            public void onSessionClosed(ErrorResult errorResult) {redirectLoginActivity();}

            @Override
            public void onSuccess(MeV2Response response) {
                username = response.getNickname();
                redirectSignupActivity(); // when success
            }
        });

    }
    protected void showSignup() {
        redirectLoginActivity();
    }
    private void redirectLoginActivity() {
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    private void redirectSignupActivity() {
        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra("nickname",username);
        appData = getSharedPreferences("appData",MODE_PRIVATE);
        SharedPreferences.Editor editor = appData.edit();
        editor.putString("nn",username);
        editor.putBoolean("kakao",true);
        editor.commit();
        startActivity(new Intent(this, SignupActivity.class));
        finish();
    }
}
