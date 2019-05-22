package edu.skku.capstone.justpay;


import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.drawable.ColorDrawable;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResultActivity1 extends AppCompatActivity {

    Button shareBtn;
    String test;
    int roomId;//현재 방 번호
    int userId;
    String userName;
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
        roomId = 1;
        // Get data from Database
         String sql = "SELECT userId from roomLists";
         JSONObject userLists = new SQLSender().sendSQL(sql); //방의 유저목록 휙득
          try {
              if (!userLists.getBoolean("isError")) {
                  for(int i = 0;i < userLists.getJSONArray("result").length();i++)
                  {
                      userId = userLists.getJSONArray("result").getJSONObject(i).getInt("userId");

                      JSONObject usertemp = new SQLSender().sendSQL("SELECT nickname from users where id ="+userId); //유저 이름 알아내기
                      userName = usertemp.getJSONArray("result").getJSONObject(0).getString("nickname");
                      int totalPay = 0; // 유저의 전체 금액
                      int billId;
                      JSONObject billLists = new SQLSender().sendSQL("SELECT id from bills where roomId="+String.valueOf(roomId)); //현재 bill;
                      billId = billLists.getJSONArray("result").getJSONObject(0).getInt("id"); // 해당 event의 아이디
                      JSONObject itemLists = new SQLSender().sendSQL("SELECT * from checkLists where userId=" + String.valueOf(userId)); //유저의 아이템 목록 휙득
                      for(int j = 0;j < itemLists.getJSONArray("result").length();j++) {
                          JSONObject billtemp = new SQLSender().sendSQL("SELECT * from items where id="+itemLists.getJSONArray("result").getJSONObject(j).getInt("itemId")); //item의 bill;
                          int itemBillId = billtemp.getJSONArray("result").getJSONObject(j).getInt("billId");
                          if(itemBillId == billId) {
                              customadapter.itemlists.add(new ArrayList<personal_item>());
                              String itemName = billtemp.getJSONArray("result").getJSONObject(j).getString("itemname");
                              int itempay = billtemp.getJSONArray("result").getJSONObject(j).getInt("price");
                              int itemnum = billtemp.getJSONArray("result").getJSONObject(j).getInt("quantity");
                              totalPay = totalPay + itempay * itemnum;
                              customadapter.addItem(0,new personal_item(itemName,itempay,itemnum));

                              customadapter.personlists.add(new resultlist_item(userName,totalPay));
                          }
                      }

                  }

              }
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

        shareBtn = findViewById(R.id.button);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"카카오톡 메시지로 공유", "엑셀 파일로 공유", "URL로 공유"};

                AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity1.this);  // context 변경 (-Activity.this -> this)
                builder.setTitle("공유하기");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {  // pos 0: 카카오톡, pos 1: 엑셀, pos 2: URL

                    }
                });
                builder.show();
            }
        });
    }

}