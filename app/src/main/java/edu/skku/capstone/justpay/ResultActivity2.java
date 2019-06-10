package edu.skku.capstone.justpay;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.SocialObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ResultActivity2 extends AppCompatActivity {

    Button shareBtn;
    int roomId;//현재 방 번호
    int userId;
    int itemId;
    String userName;
    JSONObject loginUser;
    int loginUserId;// 현재 로그인한 유
    int loginTotalPay;
    int eventId; // 현재 event
    int payerId;
    int totalPay = 0; // 유저의 전체 금액
    String payerName;
    String itemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result2);

        //ListView listview ;
        final CustomList2 customadapter;
        customadapter = new CustomList2() ;

        // 리스트뷰 참조 및 Adapter달기
        ExpandableListView listview = (ExpandableListView)findViewById(R.id.listView);
        customadapter.itemlists = new ArrayList<> ();
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

        }
        catch (NullPointerException e) {
            Log.e("Exception", "NullPointerException occurred in ResultActivity1.java");
            e.printStackTrace();
        }

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
        int countup = 0;
        ArrayList<ArrayList<items_person>> personlistbyitem = new ArrayList<>() ;
        customadapter.personlists = personlistbyitem;
        String sql = "SELECT * from bills where eventId = " + eventId;
        JSONObject billLists = new SQLSender().sendSQL(sql); //일단 이벤트에 해당하는 bill의 아이디 휙득
        try {

            for(int i = 0;i<billLists.getJSONArray("result").length();i++)
            {
                int billId = billLists.getJSONArray("result").getJSONObject(i).getInt("id");
                sql = "SELECT * from items where billId = " + billId;//빌 아이디를 가진 item들 휙득
                JSONObject itemLists = new SQLSender().sendSQL(sql);

                    for(int j = 0;j < itemLists.getJSONArray("result").length();j++)
                    {
                        itemId = itemLists.getJSONArray("result").getJSONObject(j).getInt("id");//item들의 id를 하나씩 가져옴

                        itemName = itemLists.getJSONArray("result").getJSONObject(j).getString("itemname");//이름도
                        customadapter.addItem(new personal_item(itemName,itemLists.getJSONArray("result").getJSONObject(j).getInt("price"),itemLists.getJSONArray("result").getJSONObject(j).getInt("quantity")));
                        JSONObject userLists = new SQLSender().sendSQL("SELECT * from checkLists where itemId=" + String.valueOf(itemId)); //아이템 유저 목록 휙득
                        //Toast.makeText(getApplicationContext(), String.valueOf(userLists.getJSONArray("result").length()), Toast.LENGTH_SHORT).show();
                        for(int k = 0;k < userLists.getJSONArray("result").length();k++) {//체크리스트의 아이템들 검사
                            JSONObject usertemp = new SQLSender().sendSQL("SELECT * from users where id="+userLists.getJSONArray("result").getJSONObject(k).getInt("userId")); //user의 정보 다 가져오기
                            customadapter.personlists.add(new ArrayList<items_person>());
                            String userName = usertemp.getJSONArray("result").getJSONObject(0).getString("nickname");//유저이름
                            int itempay = itemLists.getJSONArray("result").getJSONObject(j).getInt("price");//
                            int itemnum = userLists.getJSONArray("result").getJSONObject(k).getInt("quantity");


                            customadapter.addPerson(j+countup, new items_person(userName,itempay,itemnum));

                        }

                    }
                    countup +=itemLists.getJSONArray("result").length();

                }

        } catch (JSONException e) {
            Log.e("Exception", "JSONException occurred in ResultActivity2.java");
            e.printStackTrace();
        }
/*
        //create Data
        customadapter.addItem(new personal_item("아메리카노",1000,1));
        customadapter.addItem(new personal_item("프라프치노",2000,1));
        customadapter.addItem(new personal_item("무슨치노",3000,2));
        customadapter.addItem(new personal_item("무슨카노",4000,5));
        customadapter.addItem(new personal_item("무슨라떼",5000,3));
        customadapter.addItem(new personal_item("무슨빵",1500,2));
        customadapter.personlists = personlistbyitem;
        customadapter.personlists.add(new ArrayList<items_person>());
        customadapter.personlists.add(new ArrayList<items_person>());
        customadapter.personlists.add(new ArrayList<items_person>());
        customadapter.personlists.add(new ArrayList<items_person>());
        customadapter.personlists.add(new ArrayList<items_person>());
        customadapter.personlists.add(new ArrayList<items_person>());
        customadapter.addPerson(0,new items_person("김사무엘",1000,1));
        customadapter.addPerson(1,new items_person("오승민",2000,1));
        customadapter.addPerson(2,new items_person("이상규",6000,2));
        customadapter.addPerson(3,new items_person("오승민",20000,4));
        customadapter.addPerson(3,new items_person("김사무엘",5000,1));
        customadapter.addPerson(4,new items_person("이지훈",15000,3));
        customadapter.addPerson(5,new items_person("조현진",1500,1));
        customadapter.addPerson(5,neew items_prson("이지훈",1500,1));
*/
        listview.setAdapter(customadapter);
        Button buttonRule = (Button) findViewById(R.id.toolbar_button);//항목별 -> 사용자별
        buttonRule.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent;intent = new Intent(ResultActivity2.this, ResultActivity1.class);
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
                Comparator<personal_item> moneyAsc = new Comparator<personal_item>() {
                    @Override
                    public int compare(personal_item item1, personal_item item2) {
                        int ret ;

                        if (item1.getPay()*item1.getNumber() < item2.getPay()*item2.getNumber())
                            ret = 1 ;
                        else if (item1.getPay()*item1.getNumber() == item2.getPay()*item2.getNumber())
                            ret = 0 ;
                        else
                            ret = -1 ;

                        return ret ;


                    }
                } ;
                Comparator<personal_item> nameAsc = new Comparator<personal_item>() {
                    @Override
                    public int compare(personal_item item1, personal_item item2) {
                        int ret;
                        ret = item2.getName().compareTo(item1.getName()) ;
                        return -ret;


                    }
                } ;
                if(state == 1) {
                    Collections.sort(customadapter.itemlists, moneyAsc);
                    state = 0;
                }
                else {
                    Collections.sort(customadapter.itemlists, nameAsc);
                    state = 1;
                }
                customadapter.notifyDataSetChanged() ;
            }
        });
        JSONObject payertemp = new SQLSender().sendSQL("SELECT * from events where id = "+eventId); //방의 돈낸사람 휙득
        try {
            payerId = payertemp.getJSONArray("result").getJSONObject(0).getInt("payerId");
            JSONObject usertemp = new SQLSender().sendSQL("SELECT nickname from users where id ="+payerId); //유저 이름 알아내기
            payerName = usertemp.getJSONArray("result").getJSONObject(0).getString("nickname");
            TextView payerResult = (TextView)findViewById(R.id.textView6);
            if(loginTotalPay != 0)
                payerResult.setText(payerName+"님께 "+loginTotalPay+"원을 전송해 주세요");
            else
                payerResult.setText(payerName+"님께서 결제하셨습니다");

        } catch (JSONException e) {
            Log.e("Exception", "JSONException occurred in ExampleActivity.java");
            e.printStackTrace();
        }

        JSONObject eventtemp = new SQLSender().sendSQL("SELECT * from events where id = "+eventId); //툴바 제목 변경
        try {
            String eventName = eventtemp.getJSONArray("result").getJSONObject(0).getString("eventname");
            TextView payerResult = findViewById(R.id.toolbar_title);
            payerResult.setText(eventName);

        } catch (JSONException e) {
            Log.e("Exception", "JSONException occurred in ExampleActivity.java");
            e.printStackTrace();
        }
        /*listview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(getApplicationContext(), customadapter.getChild(groupPosition, childPosition).getName(), Toast.LENGTH_SHORT).show();

                return true;
            }
        });*/

        shareBtn = findViewById(R.id.button);

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"카카오톡 메시지로 공유", "엑셀 파일로 공유", "URL로 공유"};

                AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity2.this);  // context 변경 (-Activity.this -> this)
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
                                            .setDescrption(payerName+"님께 "+loginTotalPay+"원을 전송해 주세요!!")
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

                            KakaoLinkService.getInstance().sendDefault(ResultActivity2.this, params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
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
                        }
                    }
                });
                builder.show();
            }
        });
    }

}