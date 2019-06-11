package edu.skku.capstone.justpay;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.drawable.ColorDrawable;
import com.kakao.auth.KakaoSDK;
import com.kakao.auth.Session;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.SocialObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.exception.KakaoException;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.kakao.kakaolink.v2.KakaoLinkService;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import android.widget.TextView;

import com.kakao.util.KakaoParameterException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResultActivity1 extends AppCompatActivity {

    Button shareBtn;
    String test;
    int roomId;//현재 방 번호
    int userId;
    String userName;
    JSONObject loginUser;
    int loginUserId;// 현재 로그인한 유
    int loginTotalPay;
    int eventId; // 현재 event
    int payerId;
    int totalPay = 0; // 유저의 전체 금액
    String payerName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        //ListView listview ;
        final CustomList customadapter;
        customadapter = new CustomList() ;

        ExpandableListView listview = (ExpandableListView)findViewById(R.id.listView);
        customadapter.personlists = new ArrayList<> ();

        ArrayList<ArrayList<personal_item>> itemlists = new ArrayList<>() ;
        customadapter.itemlists = itemlists;

        Intent intent = getIntent(); /*데이터 수신*/

        try{
            roomId = intent.getExtras().getInt("roomId"); /*int형*/
            eventId = intent.getExtras().getInt("eventId"); /*int형*/
        }
        catch (NullPointerException e) {
            Log.e("Exception", "NullPointerException occurred in ResultActivity1.java");
            e.printStackTrace();
        }
       try{
            eventId = intent.getData().toString().indexOf("eventId=");
            eventId = Integer.parseInt(intent.getData().toString().substring(eventId+8, eventId+9));
            roomId = intent.getData().toString().indexOf("roomId=");
            roomId = Integer.parseInt(intent.getData().toString().substring(roomId+7, roomId+8));
           intent = new Intent(this, LoginActivity.class);
           intent.putExtra("roomId", roomId);
           intent.putExtra("eventId", eventId);
           intent.putExtra("KakaoPath", true);
           intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
           startActivity(intent);
        }
        catch (NullPointerException e) {
            Log.e("Exception", "NullPointerException occurred in ResultActivity1.java");
            e.printStackTrace();
        }
        //roomId = 1;
        //eventId = 1;
        try{
            loginUser = UserLoggedIn.getUser();
            if(loginUser == null)
                loginUserId = 0;
            else
                loginUserId = loginUser.getInt("id");


        }
        catch (JSONException e) {
            Log.e("Exception", "JSONException occurred in ResultActivity1.java");
            e.printStackTrace();
        }
        /*        JSONObject roomlists = new SQLSender().sendSQL( "SELECT userId from roomLists"); //방의 유저목록 휙득
        try {
        } catch (JSONException e) {
            Log.e("Exception", "JSONException occurred in ExampleActivity.java");
            e.printStackTrace();
        }*/
        loginTotalPay = 0;
        // Get data from Database
         String sql = "SELECT * from roomLists where roomId = "+roomId;
         JSONObject userLists = new SQLSender().sendSQL(sql); //방의 유저목록 휙득
          try {
              if (!userLists.getBoolean("isError")) {
                  for(int i = 0;i < userLists.getJSONArray("result").length();i++)// room의 user수만큼 반
                  {

                      userId = userLists.getJSONArray("result").getJSONObject(i).getInt("userId");

                      JSONObject usertemp = new SQLSender().sendSQL("SELECT nickname from users where id ="+userId); //유저 이름 알아내기
                      userName = usertemp.getJSONArray("result").getJSONObject(0).getString("nickname");

                      //Toast.makeText(getApplicationContext(), userName, Toast.LENGTH_SHORT).show();

                      customadapter.personlists.add(new resultlist_item(userName, totalPay));
                      customadapter.itemlists.add(new ArrayList<personal_item>());
                      int billId;
                      JSONObject billLists = new SQLSender().sendSQL("SELECT * from bills where eventId="+String.valueOf(eventId)); //현재 이벤트의 bill들;
                      JSONObject itemLists = new SQLSender().sendSQL("SELECT * from checkLists where userId=" + String.valueOf(userId)); //유저의 아이템 목록 휙득

                      for(int j = 0;j < itemLists.getJSONArray("result").length();j++) {
                          JSONObject billtemp = new SQLSender().sendSQL("SELECT * from items where id="+itemLists.getJSONArray("result").getJSONObject(j).getInt("itemId")); //item의 bill;
                          int itemBillId = billtemp.getJSONArray("result").getJSONObject(0).getInt("billId");//item의 billId

                          for(int k = 0;k<billLists.length();k++) {
                              billId = billLists.getJSONArray("result").getJSONObject(k).getInt("id");

                              if (itemBillId == billId) { // 해당 billId를 가진 bill이 속한 이벤트에 있는지 확인
                                  String itemName = billtemp.getJSONArray("result").getJSONObject(0).getString("itemname");
                                  int itempay = billtemp.getJSONArray("result").getJSONObject(0).getInt("price");
                                  int itemnum = itemLists.getJSONArray("result").getJSONObject(j).getInt("quantity");
                                  totalPay = totalPay + itempay * itemnum;
                                  customadapter.personlists.set(i,new resultlist_item(userName,this.totalPay));
                                  customadapter.addItem(i, new personal_item(itemName, itempay, itemnum));

                              }

                          }

                      }


                      if(userId == loginUserId)
                          loginTotalPay = totalPay;
                      totalPay = 0;
                  }

              }
          } catch (JSONException e) {
              Log.e("Exception", "JSONException occurred in ExampleActivity.java");
              e.printStackTrace();
          }
        JSONObject payertemp = new SQLSender().sendSQL("SELECT * from events where id = "+eventId); //방의 돈낸사람 휙득
        try {
            payerId = payertemp.getJSONArray("result").getJSONObject(0).getInt("payerId");
            JSONObject usertemp = new SQLSender().sendSQL("SELECT nickname from users where id ="+payerId); //유저 이름 알아내기
            payerName = usertemp.getJSONArray("result").getJSONObject(0).getString("nickname");
            TextView payerResult = (TextView)findViewById(R.id.textView5);
            if(loginTotalPay != 0)
                payerResult.setText(payerName+"님께 "+loginTotalPay+"원을 전송해 주세요");
            else
                payerResult.setText(payerName+"님께서 결제하셨습니다");

        } catch (JSONException e) {
            Log.e("Exception", "JSONException occurred in ExampleActivity.java");
            e.printStackTrace();
        }
        //create Data
        /*customadapter.addPerson(new resultlist_item(test,1000));
        customadapter.personlists.add(new resultlist_item("오승민",3000));
        customadapter.personlists.add(new resultlist_item("이상규",2000));
        customadapter.personlists.add(new resultlist_item("이지훈",7000));
        customadapter.personlists.add(new resultlist_item("이창훈",8000));
        customadapter.personlists.add(new resultlist_item("조현진",3000));

        customadapter.itemlists.add(new ArrayList<personal_item>());
        customadapter.itemlists.add(new ArrayList<personal_item>());
        customadapter.itemlists.add(new ArrayList<personal_item>());
        customadapter.itemlists.add(new ArrayList<personal_item>());
        customadapter.itemlists.add(new ArrayList<personal_item>());
        customadapter.addItem(0,new personal_item("아메리카노",1000,1));
        customadapter.addItem(0,new personal_item("샌드위치",1000,4));
        customadapter.addItem(1,new personal_item("카페라떼",3000,1));
        customadapter.addItem(2,new personal_item("프라프치노",1000,1));
        customadapter.addItem(3,new personal_item("고구마튀김",1000,1));
        customadapter.addItem(4,new personal_item("짬뽕라떼",1000,1));
        customadapter.addItem(5,new personal_item("군고구라떼",1000,1));*/
        listview.setAdapter(customadapter);

        Button buttonRule = (Button) findViewById(R.id.toolbar_button);//사용자별 -> 항목
        buttonRule.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(ResultActivity1.this, ResultActivity2.class);
                intent.putExtra("roomId", roomId);
                intent.putExtra("eventId", eventId);
                startActivity(intent);
            }

        });

        Button buttonNoAsc = (Button) findViewById(R.id.orderbutton) ;
        buttonNoAsc.setOnClickListener(new Button.OnClickListener() {
            int state=0;
            @Override
            public void onClick(View v) {
                Comparator<resultlist_item> moneyAsc = new Comparator<resultlist_item>() {
                    @Override
                    public int compare(resultlist_item item1, resultlist_item item2) {
                        int ret ;

                        if (item1.getPay() < item2.getPay())
                            ret = -1 ;
                        else if (item1.getPay() == item2.getPay())
                            ret = 0 ;
                        else
                            ret = 1 ;

                        return ret ;


                    }
                } ;
                Comparator<resultlist_item> nameAsc = new Comparator<resultlist_item>() {
                    @Override
                    public int compare(resultlist_item item1, resultlist_item item2) {
                        int ret;
                        ret = item2.getName().compareTo(item1.getName()) ;
                        return -ret;


                    }
                } ;
                if(state == 1) {
                    Collections.sort(customadapter.personlists, moneyAsc);
                    state = 0;
                }
                else {
                    Collections.sort(customadapter.personlists, nameAsc);
                    state = 1;
                }
                customadapter.notifyDataSetChanged() ;
            }
        });
        /*listview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(getApplicationContext(), customadapter.getChild(groupPosition, childPosition).getName(), Toast.LENGTH_SHORT).show();

                return true;
            }
        });*/

        JSONObject eventtemp = new SQLSender().sendSQL("SELECT * from events where id = "+eventId); //툴바 제목 변경
        try {
            String eventName = eventtemp.getJSONArray("result").getJSONObject(0).getString("eventname");
            TextView payerResult = findViewById(R.id.toolbar_title);
            payerResult.setText(eventName);

        } catch (JSONException e) {
            Log.e("Exception", "JSONException occurred in ExampleActivity.java");
            e.printStackTrace();
        }
        shareBtn = findViewById(R.id.button);

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"카카오톡 메시지로 공유", "엑셀 파일로 공유", "URL로 공유"};

                AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity1.this);  // context 변경 (-Activity.this -> this)
                builder.setTitle("공유하기");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {  // pos 0: 카카오톡, pos 1: 엑셀, pos 2: URL
                        if(pos == 0)
                        {

                            FeedTemplate params = FeedTemplate
                                    .newBuilder(ContentObject.newBuilder("정산결과",
                                            "https://ifh.cc/g/2wQ5V.png",
                                            LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
                                                    .setMobileWebUrl("https://developers.kakao.com").build())
                                            .setDescrption(payerName+"님께서 정산 결과를 공유하셨습니다. \n Justpay로 접속해 지금 바로 \n결과를 확인하세요!!" )
                                            .build())
                                    .setSocial(SocialObject.newBuilder().setLikeCount(1008).setCommentCount(1008)
                                            .setSharedCount(1008).setViewCount(1008).build())
                                    //.addButton(new ButtonObject("웹에서 보기", LinkObject.newBuilder().setWebUrl("'https://developers.kakao.com").setMobileWebUrl("'https://developers.kakao.com").build()))
                                    .addButton(new ButtonObject("앱에서 보기", LinkObject.newBuilder()
                                            .setWebUrl("'https://developers.kakao.com")
                                            .setMobileWebUrl("'market://details?id=edu.skku.capstone.justpay")
                                            .setAndroidExecutionParams("eventId="+eventId +"&" +"roomId="+roomId)
                                            .setIosExecutionParams("eventId="+eventId +"&" +"roomId="+roomId)
                                            .build()))
                                    .build();

                            Map<String, String> serverCallbackArgs = new HashMap<String, String>();
                            serverCallbackArgs.put("user_id", "${current_user_id}");
                            serverCallbackArgs.put("product_id", "${shared_product_id}");

                            KakaoLinkService.getInstance().sendDefault(ResultActivity1.this, params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
                                @Override
                                public void onFailure(ErrorResult errorResult) {
                                    Logger.e(errorResult.toString());
                                }

                                @Override
                                public void onSuccess(KakaoLinkResponse result) {
                                    // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
                                }
                            });
                        } else if (pos == 1) { // Excel
                            Uri uri = Uri.parse("http://52.68.56.145/excel?eventId="+eventId);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        } else if (pos == 2) { // URL
                            Uri uri = Uri.parse("http://52.68.56.145/result?eventId="+eventId);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText("resultURL", "http://52.68.56.145/result?eventId="+eventId);
                            clipboardManager.setPrimaryClip(clipData);
                            Toast.makeText(getApplication(), "URL이 복사되었습니다.",Toast.LENGTH_LONG).show();


                        }
                    }
                });
                builder.show();
            }
        });

    }

}