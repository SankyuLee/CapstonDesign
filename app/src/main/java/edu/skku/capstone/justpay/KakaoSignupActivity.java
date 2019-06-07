package edu.skku.capstone.justpay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

public class KakaoSignupActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestMe();
    }

    protected void requestMe() {
        UserManagement.getInstance().requestMe(new MeResponseCallback() {
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
            public void onSessionClosed(ErrorResult errorResult) {redirectLoginActivity();}

            @Override
            public void onNotSignedUp() {
                showSignup();
            }
            @Override
            public void onSuccess(UserProfile result) {
                Logger.d("UserProfile : " + result);
                Log.v("user ",result.toString());
                redirectMainActivity(); // when success
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

    private void redirectMainActivity() {
        startActivity(new Intent(this, RoomListActivity.class));
        finish();
    }
}
