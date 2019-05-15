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
 * SQL문을 요청하고 그에 대한 결과를 얻기 위한 클래스입니다.
 * <p>사용 가능 메소드:
 * <pre>
 *     JSONObject sendSQL(String sql);
 * </pre>
 * <p>활용 예시: 처음으로 가입한 사용자의 이름을 얻어 TextView에 적용
 * <pre>
 *     TextView firstUserName = (TextView)findViewById(R.id.firstUserName);
 *     String sql = "SELECT username from users where id=1";
 *     JSONObject sql_result = new SQLSender().sendSQL(sql);
 *     try {
 *         if (!sql_result.getBoolean("isError"))
 *             firstUserName.setText(sql_result.getJSONArray("result").getJSONObject(0).getString("username"));
 *     } catch (JSONException e) {
 *         Log.e("Exception", "JSONException occurred in ExampleActivity.java");
 *         e.printStackTrace();
 *     }
 * </pre>
 */
@SuppressWarnings("unused")
class SQLSender {
    private JSONObject result;

    SQLSender() { result = null; }

    /**
     * SQL문을 우리 웹 서버에 GET 방식으로 요청합니다.
     * <p>DML로 SELECT, UPDATE, INSERT, DELETE를 사용 가능합니다.
     * <p>활용 예시: 처음으로 가입한 사용자의 이름을 얻어 TextView에 적용
     * <pre>
     *     TextView firstUserName = (TextView)findViewById(R.id.firstUserName);
     *     String sql = "SELECT username from users where id=1";
     *     JSONObject sql_result = new SQLSender().sendSQL(sql);
     *     try {
     *         if (!sql_result.getBoolean("isError"))
     *             firstUserName.setText(sql_result.getJSONArray("result").getJSONObject(0).getString("username"));
     *     } catch (JSONException e) {
     *         Log.e("Exception", "JSONException occurred in ExampleActivity.java");
     *         e.printStackTrace();
     *     }
     * </pre>
     * @param sql 있는 그대로의 SQL문을 넣어주시면 됩니다.
     * <p>주의: 문자열을 따옴표로 감쌀 때는 작은 따옴표(')만 사용해야 합니다. Escape sequence를 이용하여 큰 따옴표(")를 넣는 것도 안 됩니다.
     * <p>예시: "SELECT * from users where username='홍길동';"
     *
     * @return JSONObeject 형식의 결과값을 반환합니다. isError와 result를 key로 가지고 있습니다.
     * <p>isError - boolean 값(true or false)
     * <p>result - isError가 false인 경우에는 에러를 나타내는 JSONObject
     * <p>isError가 true이며 DML이 UPDATE, INSERT, DELETE인 경우에는 처리 결과를 나타내는 JSONObject
     * <p>isError가 true이며 DML이 SELECT인 경우 선택된 tuple들이 JSONObject로 담긴 JSONArray, 즉
     * <p>{"isError": false, "result": [ { tup1e 1 } , { tuple 2 } , ...]}
     */
    JSONObject sendSQL(String sql) {
        Thread ct = new ConnectionTask(sql);
        try {
            ct.start();
            ct.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e("Exception", "InterruptedException occurred in SQLSender.java");
        }
        return result;
    }

    private class ConnectionTask extends Thread {
        String sql;

        private ConnectionTask(String sql) {
            this.sql = sql;
        }

        @Override
        public void run() {
            HttpURLConnection con = null;
            try {
                URL url = new URL("http://"+Constant.SERVER_IP+"/sql?query="+sql);
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
                Log.e("Exception", "IOException occurred in SQLSender.java");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("Exception", "JSONException occurred in SQLSender.java");
            } finally {
                if (con != null)
                    con.disconnect();
            }
        }
    }
}
