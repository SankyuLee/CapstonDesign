package edu.skku.capstone.justpay;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity{

    private TextView roomTitle, roomID;
    private ImageButton banBtn, addBtn, changeBtn;

    private RecyclerView tabListView;
    private RoomTabAdapter tabAdapter;

    private ImageView receiptImg;
    private ImageButton prevReceiptBtn, nextReceiptBtn;

    private TextView eventStatus1, eventStatus2, eventStatus3;

    private ListView chartItemListView;
    private RoomChartItemAdapter chartItemAdapter;

    private Button prevStatusBtn, nextStatusBtn;

    private TextView typingStatus;

    private LinearLayout bottomContainer;
    private EditText itemNameEdit, itemCostEdit, itemCountEdit;
    private ImageButton addItemBtn;
    private Button confirmBtn;
    
    private EventStatus eventStatus;
    private enum EventStatus { MAKE_LIST,  PERSONAL_CHECK, CONFIRM_RESULT }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        roomID = findViewById(R.id.room_id);
        roomTitle = findViewById(R.id.room_id);
        banBtn = findViewById(R.id.ban_user_btn);
        addBtn = findViewById(R.id.add_user_btn);
        changeBtn = findViewById(R.id.change_user_btn);
        tabListView = findViewById(R.id.room_tab_list);
        receiptImg = findViewById(R.id.receipt_image);
        prevReceiptBtn = findViewById(R.id.receipt_prev_btn);
        nextReceiptBtn = findViewById(R.id.receipt_next_btn);
        eventStatus1 = findViewById(R.id.event_status1);
        eventStatus2 = findViewById(R.id.event_status2);
        eventStatus3 = findViewById(R.id.event_status3);
        chartItemListView = findViewById(R.id.chart_list_view);
        prevStatusBtn = findViewById(R.id.prev_status_btn);
        nextStatusBtn = findViewById(R.id.next_status_btn);
        typingStatus = findViewById(R.id.room_typing_status);
        bottomContainer = findViewById(R.id.room_bottom_container);

        // Make Room with title, id, focused event name and room status
        initRoom("5지조", "1234", "05/09 커피");

        banBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"Member1", "Member2", "Member3", "Member4"};

                AlertDialog.Builder builder = new AlertDialog.Builder(RoomActivity.this);
                builder.setTitle("멤버 추방");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        //멤버 추방시 액션
                    }
                });
                builder.show();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"Member1", "Member2", "Member3", "Member4"};

                AlertDialog.Builder builder = new AlertDialog.Builder(RoomActivity.this);
                builder.setTitle("멤버 추가");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        //멤버 추가시 액션
                    }
                });
                builder.show();
            }
        });

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"Member1", "Member2", "Member3", "Member4"};

                AlertDialog.Builder builder = new AlertDialog.Builder(RoomActivity.this);
                builder.setTitle("입력멤버 변경");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        //입력멤버 변경시 액션
                    }
                });
                builder.show();
            }
        });
    }

    private void initRoom(String title, String id, String eventName) {
        // Set room's title and id
        roomTitle.setText(title);
        roomID.setText(id);

        // Init tabs and focusing target event
        initTabs();
        setTab(eventName);
    }

    private void initTabs() {
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        tabListView.setLayoutManager(layoutManager);

        final ArrayList<RoomTabItem> tabList = new ArrayList<>();
        tabList.add(new RoomTabItem("05/09 커피"));
        tabList.add(new RoomTabItem("05/08 점심"));

        tabAdapter = new RoomTabAdapter(tabList, new RoomTabAdapter.TabOnClickListener() {
            @Override
            public void onTabClicked(int position) {
                /* Change Tab */
                setTab(tabList.get(position).getTabTitle());
            }

            @Override
            public void onTabDeleteBtnClicked(int position) {
                /* Remove Tab */
                tabAdapter.removeItem(position);
            }
        });
        tabListView.setAdapter(tabAdapter);

        RoomTabDecoration tabDecoration = new RoomTabDecoration();
        tabListView.addItemDecoration(tabDecoration);
    }

    private void setTab(String eventName) {
        Toast.makeText(getApplicationContext(), "Open " + eventName, Toast.LENGTH_SHORT).show();
        // Get event info from db
        int eventStatusInt = 0; // Status
        ArrayList<RoomChartItem> itemList = new ArrayList<>(); // Item List
        itemList.add(new RoomChartItem("커피", new Integer(3000), new Integer(2)));
        itemList.add(new RoomChartItem("쿠키", new Integer(5400), new Integer(3)));
        itemList.add(new RoomChartItem("설탕", new Integer(5400), new Integer(3)));

        // Set event status
        switch (eventStatusInt) {
            case 0:
                eventStatus = EventStatus.MAKE_LIST;
                eventStatus1.setTextColor(getResources().getColor(R.color.colorJustPay));
                eventStatus1.setTypeface(eventStatus1.getTypeface(), Typeface.BOLD);
                break;
            case 1:
                eventStatus = EventStatus.PERSONAL_CHECK;
                eventStatus2.setTextColor(getResources().getColor(R.color.colorJustPay));
                eventStatus2.setTypeface(eventStatus1.getTypeface(), Typeface.BOLD);
                break;
            case 2:
                eventStatus = EventStatus.CONFIRM_RESULT;
                eventStatus3.setTextColor(getResources().getColor(R.color.colorJustPay));
                eventStatus3.setTypeface(eventStatus1.getTypeface(), Typeface.BOLD);
                break;
        }

        // Set item chart
        chartItemAdapter = new RoomChartItemAdapter(itemList, new RoomChartItemAdapter.ChartItemOnClickListener() {
            @Override
            public void onChartItemDeleteBtnClick(int position) {
                /* Remove item */
                chartItemAdapter.removeItem(position);
            }
        });
        chartItemListView.setAdapter(chartItemAdapter);

        // Set bottom container
        setBottomContainer();
    }

    private void setBottomContainer() {
        LayoutInflater bottomInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (eventStatus == eventStatus.MAKE_LIST) {
            bottomInflater.inflate(R.layout.room_bottom_insertion, bottomContainer, true);
            itemNameEdit = findViewById(R.id.item_name_edit);
            itemCostEdit = findViewById(R.id.item_cost_edit);
            itemCountEdit = findViewById(R.id.item_count_edit);
            addItemBtn = findViewById(R.id.add_item_btn);
        } else {
            bottomInflater.inflate(R.layout.room_bottom_confirm, bottomContainer, true);
            confirmBtn = findViewById(R.id.room_result_confirm_btn);

            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CharSequence[] items = {"사용자별 결과 확인", "항목별 결과 확인"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(RoomActivity.this);
                    builder.setTitle("결과 확인");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int pos) {  // pos 0: 사용자별 결과 확인, pos 1: 항목별 결과 확인
                            Intent intent;
                            intent = new Intent(RoomActivity.this, ResultActivity1.class);
                            startActivity(intent);
                        }
                    });
                    builder.show();
                }
            });
        }
    }
}
