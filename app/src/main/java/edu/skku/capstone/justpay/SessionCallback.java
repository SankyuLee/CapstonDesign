package edu.skku.capstone.justpay;

import android.util.Log;
import android.widget.Toast;

import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

public class SessionCallback implements ISessionCallback {
    // 로그인 성공
    @Override
    public void onSessionOpened() {
        requestMe();
    }

    // 로그인 실패
    @Override
    public void onSessionOpenFailed(KakaoException exception) {
        Log.e("SessionCallback::" , "onSessionOpenFailed : " + exception.getMessage());
    }

    // 유저 정보 요청
    public void requestMe() {
        UserManagement.getInstance().requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.e("SessionCallback ::","onSessionClosed : " + errorResult.getErrorMessage());
            }

            @Override
            public void onNotSignedUp() {
                Log.e("SessionCallback::", "onNotSignedUp");
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Log.e("SessionCallback ::","onSuccess");

                String nickname = userProfile.getNickname();
                String email = userProfile.getEmail();
                String profileImagePath = userProfile.getProfileImagePath();
                String thumnailPath = userProfile.getThumbnailImagePath();
                String UUID = userProfile.getUUID();
                long id = userProfile.getId();

                

                Log.e("Profile : ", nickname + "");
                Log.e("Profile : ", email + "");
                Log.e("Profile : ", profileImagePath  + "");
                Log.e("Profile : ", thumnailPath + "");
                Log.e("Profile : ", UUID + "");
                Log.e("Profile : ", id + "");
            }
        });
    }
}
