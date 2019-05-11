package edu.skku.capstone.justpay;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity {

    private ListView chartItemListView;
    private RecyclerView tabListView;

    private RoomTabAdapter tabAdapter;
    private RoomChartItemAdapter chartItemAdapter;

    private LinearLayout bottomContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        setTabs();
        setChartItems();

        bottomContainer = findViewById(R.id.room_bottom_container);
        LayoutInflater bottomInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        bottomInflater.inflate(R.layout.room_bottom_insertion, bottomContainer, true);
    }

    private void setTabs() {
        tabListView = findViewById(R.id.room_tab_list);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        tabListView.setLayoutManager(layoutManager);

        ArrayList<String> tabList = new ArrayList<>();
        tabList.add("05/09 커피");
        tabList.add("05/08 점심");

        tabAdapter = new RoomTabAdapter(this, tabList, onClickTab);
        tabListView.setAdapter(tabAdapter);

        RoomTabDecoration tabDecoration = new RoomTabDecoration();
        tabListView.addItemDecoration(tabDecoration);
    }

    private void setChartItems() {
        chartItemListView = findViewById(R.id.chart_list_view);

        ArrayList<RoomChartItem> chartItemList = new ArrayList<>();
        chartItemList.add(new RoomChartItem("커피", new Integer(3000), new Integer(2)));
        chartItemList.add(new RoomChartItem("쿠키", new Integer(5400), new Integer(3)));
        chartItemList.add(new RoomChartItem("설탕", new Integer(5400), new Integer(3)));

        RoomChartItemAdapter chartItemAdapter = new RoomChartItemAdapter(chartItemList);
        chartItemListView.setAdapter(chartItemAdapter);
    }

    private View.OnClickListener onClickTab = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String str = (String) v.getTag();
        }
    };
}
