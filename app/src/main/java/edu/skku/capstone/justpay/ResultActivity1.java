package edu.skku.capstone.justpay;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.drawable.ColorDrawable;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.ArrayList;

public class ResultActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //ListView listview ;
        CustomList customadapter;
        customadapter = new CustomList() ;

        // 리스트뷰 참조 및 Adapter달기
        ExpandableListView listview = (ExpandableListView)findViewById(R.id.listView);
        customadapter.personlists = new ArrayList<> ();

        ArrayList<ArrayList<personal_item>> itemlists = new ArrayList<>() ;

        //create Data
        customadapter.addPerson(new resultlist_item("김사무엘",1000));
        customadapter.personlists.add(new resultlist_item("오승민",3000));
        customadapter.personlists.add(new resultlist_item("이상규",2000));
        customadapter.personlists.add(new resultlist_item("이지훈",7000));
        customadapter.personlists.add(new resultlist_item("이창훈",8000));
        customadapter.personlists.add(new resultlist_item("조현진",3000));
        customadapter.itemlists = itemlists;
        customadapter.itemlists.add(new ArrayList<personal_item>());
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
        customadapter.addItem(5,new personal_item("군고구라떼",1000,1));
        listview.setAdapter(customadapter);

        /*listview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(getApplicationContext(), customadapter.getChild(groupPosition, childPosition).getName(), Toast.LENGTH_SHORT).show();

                return true;
            }
        });*/

    }

}