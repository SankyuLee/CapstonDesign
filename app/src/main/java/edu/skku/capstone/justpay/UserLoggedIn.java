package edu.skku.capstone.justpay;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 로그인한 사용자 정보를 담고 있는 static 클래스입니다. 생성하실 필요는 없고 해당 클래스의 LoginCheck(username, password),
 * isLoggedIn(), getUser() 3개의 메소드를 이용하실 수 있습니다.
 */
@SuppressWarnings("unused")
class UserLoggedIn {
    private static JSONObject result;
    private static int id;
    private static String email;
    private static String password;
    private static String phone;
    private static String nickname;
    private static boolean isLoggedIn = false;

    private UserLoggedIn() { }

    /**
     * 사용자의 로그인 여부를 boolean 값으로 반환하는 메소드입니다.
     * @return 사용가가 로그인한 상태이면 true, 외에는 false를 반환합니다.
     */
    boolean isLoggedIn() {
        return isLoggedIn;
    }

    /**
     * 로그인 화면의 필드에 입력한 아이디, 비밀번호를 웹 서버에 GET 방식으로 보내 검증합니다. 그리고 로그인 성공 여부를
     * 반환합니다.
     * @param email 아이디를 의미합니다.
     * @param password 비밀번호를 의미합니다.
     * @return 로그인 성공 여부를 boolean 값으로 반환합니다.
     */
    static boolean LoginCheck(String email, String password) {
        Thread ct = new ConnectionTask(email, password);
        try {
            ct.start();
            ct.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e("Exception", "InterruptedException occurred in UserLoggedIn.java");
        }
        try {
            if (result.getBoolean("loginSuccess")) {
                isLoggedIn = true;
                JSONObject user = result.getJSONObject("user");
                id = user.getInt("id");
                UserLoggedIn.email = user.getString("email");
                UserLoggedIn.password = user.getString("password");
                phone = user.getString("phone");
                nickname = user.getString("nickname");
            } else {
                isLoggedIn = false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Exception", "JSONException occurred in UserLoggedIn.java");
        }
        return isLoggedIn;
    }

    /**
     * 기본 구조는 SQLSender의 ConnectionTask와 동일합니다. 다만 parameter로 아이디와 비밀번호를 받으며
     * 상위 클래스에 의해 메소드가 호출되고 직접 사용하실 수 없습니다.
     */
    private static class ConnectionTask extends Thread {
        String email;
        String password;

        private ConnectionTask(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        public void run() {
            HttpURLConnection con = null;
            try {
                URL url = new URL("http://"+Constant.SERVER_IP+"/login?email="+email+"&password="+password);
                con = (HttpURLConnection)url.openConnection();

                con.setRequestProperty("Accept-Charset", "UTF-8");
                con.setRequestProperty("Accept", "application/json");

                InputStream is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                result = new JSONObject(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Exception", "IOException occurred in UserLoggedIn.java");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("Exception", "JSONException occurred in UserLoggedIn.java");
            } finally {
                if (con != null)
                    con.disconnect();
            }
        }
    }

    /**
     * 로그인한 사용자의 정보를 반환합니다. 따라서 로그인한 사용자의 정보를 다시 서버에 요청하실 필요 없이
     * SQL문을 수월하게 작성하실 수 있습니다.
     * @return 사용자가 로그인한 상태라면 사용자의 고유번호, 아이디, 비밀번호, 핸드폰번호, 닉네임을 JSONObeject로 반환합니다.
     * 사용자가 로그인하지 않았다면 null을 반환합니다. 따라서 NullPointerException에 대해 예외 처리를 해주셔야 합니다.
     */
    static JSONObject getUser() {
        if (!isLoggedIn) {
            return null;
        } else {
            JSONObject user = null;
            try {
                user = new JSONObject().put("id", id)
                        .put("email", email)
                        .put("password", password)
                        .put("phone", phone)
                        .put("nickname", nickname);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("Exception", "JSONException occurred in UserLoggedIn.java");
            }
            return user;
        }
    }
}
