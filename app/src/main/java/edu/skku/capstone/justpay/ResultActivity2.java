package edu.skku.capstone.justpay;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ResultActivity2 extends AppCompatActivity {

    Button shareBtn;

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

        ArrayList<ArrayList<items_person>> personlistbyitem = new ArrayList<>() ;

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
        customadapter.addPerson(5,new items_person("이지훈",1500,1));
        listview.setAdapter(customadapter);

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

                    }
                });
                builder.show();
            }
        });
    }

}