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

    boolean isLoggedIn() {
        return isLoggedIn;
    }

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

    static JSONObject getUser() {
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
        } finally {
            return user;
        }
    }
}
