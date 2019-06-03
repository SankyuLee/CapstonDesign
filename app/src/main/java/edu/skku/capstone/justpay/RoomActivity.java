package edu.skku.capstone.justpay;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

// TODO: 이벤트 생성 버튼 
// TODO: 영수증 DB 연결
// TODO: 영수증-아이템 연결 해결

public class RoomActivity extends AppCompatActivity{

    private Integer userId;
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Database 에서 관리 해야하는 변수들
    //
    private Integer roomId;                             // 방의 고유 아이디
    private String roomName;                            // 방의 이름
    private ArrayList<Member> roomMembers;              // 방에 참여하고 있는 멤버 목록
    private ArrayList<Event> roomEvents;                // 방에 존재하는 이벤트 목록
    // 현재 이벤트
    // 현재 이벤트의 제목, 관리인, 이벤트의 상태, 등록된 영수증 목록, 등록된 항목 목록 포함
    private Event curEvent;
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private ArrayList<Integer> billIds;
    private int curReceiptIndex; // 사용자가 보고 있는 영수증 인덱스
    private int curJoinMemberNum; // 입력에 참여하고 있는 인원 수

    // 앨범에서 사진을 요청하는 Flag
    private static final int PICK_FROM_ALBUM = 1;

    // Layout components
    private TextView roomNameView, roomIdView;
    private ImageButton backBtn;
    private ImageButton banBtn, addBtn, changeBtn;

    private RecyclerView tabListView;
    private EventAdapter eventAdapter;

    private TextView payerResult;
    private Button payerBtn;

    private ImageView receiptImg;
    private ImageButton addReceiptBtn, prevReceiptBtn, nextReceiptBtn;
    private TextView addReceiptText, receiptStatus;
    private PhotoViewAttacher receiptAttacher;

    private TextView eventStatus1, eventStatus2, eventStatus3;

    private ListView chartItemListView;
    private RoomChartItemAdapter chartItemAdapter;

    private Button prevStatusBtn, nextStatusBtn;
    private Button chartConfirmBtn;

    private TextView typingStatus;

    private EditText itemNameEdit, itemCostEdit, itemCountEdit;
    private ImageButton addItemBtn;
    private Button confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        // 앨범에서 사진을 불러오기위한 권한 요청
        getPermission();

        // 레이아웃 초기화
        roomIdView = findViewById(R.id.room_id);
        roomNameView = findViewById(R.id.room_name);
        backBtn = findViewById(R.id.go_back_btn);
        banBtn = findViewById(R.id.ban_user_btn);
        addBtn = findViewById(R.id.add_user_btn);
        changeBtn = findViewById(R.id.change_user_btn);
        tabListView = findViewById(R.id.room_tab_list);
        payerResult = findViewById(R.id.payer_result);
        payerBtn = findViewById(R.id.payer_select_btn);
        receiptImg = findViewById(R.id.receipt_image);
        addReceiptBtn = findViewById(R.id.add_receipt_btn);
        addReceiptText = findViewById(R.id.add_receipt_text);
        receiptStatus = findViewById(R.id.receipt_status);
        prevReceiptBtn = findViewById(R.id.receipt_prev_btn);
        nextReceiptBtn = findViewById(R.id.receipt_next_btn);
        eventStatus1 = findViewById(R.id.event_status1);
        eventStatus2 = findViewById(R.id.event_status2);
        eventStatus3 = findViewById(R.id.event_status3);
        chartItemListView = findViewById(R.id.chart_list_view);
        prevStatusBtn = findViewById(R.id.prev_status_btn);
        nextStatusBtn = findViewById(R.id.next_status_btn);
        chartConfirmBtn = findViewById(R.id.chart_confirm_btn);
        typingStatus = findViewById(R.id.room_typing_status);

        initRoom();

        banBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> memberNames = new ArrayList<>();
                final ArrayList<Integer> memberIds = new ArrayList<>();
                for (int i = 0; i < roomMembers.size(); i++) {
                    if (roomMembers.get(i).getMemberId() != userId) {
                        memberNames.add(roomMembers.get(i).getMemberName());
                        memberIds.add(roomMembers.get(i).getMemberId());
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(RoomActivity.this);
                builder.setTitle("멤버 추방");
                builder.setItems(memberNames.toArray(new CharSequence[memberNames.size()]), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        // 방에서 멤버 추방
                        roomMembers.remove(findMemberIndex(memberIds.get(pos)));

                        // 추방된 멤버 DB에 반영
                        JSONObject sqlBanMember = new SQLSender().
                                sendSQL("DELETE FROM roomLists WHERE roomId=" + roomId +
                                        " AND userId=" + memberIds.get(pos));
                    }
                });
                builder.show();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 멤버 불러오기
                final ArrayList<String> memberNames = new ArrayList<>();
                final ArrayList<Integer> memberIds = new ArrayList<>();
                JSONObject sqlMember = new SQLSender().
                        sendSQL("SELECT * FROM users");
                try {
                    if (!sqlMember.getBoolean("isError")) {
                        JSONArray memberResult = sqlMember.getJSONArray("result");
                        for (int i = 0; i < memberResult.length(); i++) {
                            Boolean isIn = false;
                            JSONObject member = memberResult.getJSONObject(i);
                            for (int j = 0; j < roomMembers.size(); j++) {
                                if (roomMembers.get(j).getMemberId() == member.getInt("id")) {
                                    isIn = true;
                                    break;
                                }
                            }
                            if (!isIn) {
                                memberNames.add(member.getString("nickname"));
                                memberIds.add(member.getInt("id"));
                            }
                        }
                    }
                } catch (JSONException e) {
                    Log.e("Exception", "JSONException occurred in setting room members");
                    e.printStackTrace();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(RoomActivity.this);
                builder.setTitle("멤버 추가");
                builder.setItems(memberNames.toArray(new CharSequence[memberNames.size()]), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        // 새로운 멤버 추가
                        JSONObject sqlNewMember = new SQLSender().
                                sendSQL("SELECT * FROM users WHERE id="+memberIds.get(pos));
                        try {
                            if (!sqlNewMember.getBoolean("isError")) {
                                JSONArray newMemberResult = sqlNewMember.getJSONArray("result");
                                for (int i = 0; i < newMemberResult.length(); i++) {
                                    JSONObject newMember = newMemberResult.getJSONObject(i);
                                    roomMembers.add(new Member(
                                            newMember.getInt("id"),
                                            newMember.getString("nickname"),
                                            newMember.getString("email"),
                                            newMember.getString("phone")
                                    ));
                                }
                            }
                        } catch (JSONException e) {
                            Log.e("Exception", "JSONException occurred in setting room members");
                            e.printStackTrace();
                        }

                        // 새로운 멤버 DB에 반영
                        JSONObject sqlInsertMember = new SQLSender().
                                sendSQL("INSERT INTO roomLists (userId, roomId) VALUES (" +
                                        memberIds.get(pos) + ", " + roomId + ")");
                    }
                });
                builder.show();
            }
        });

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> memberNames = new ArrayList<>();
                final ArrayList<Integer> memberIds = new ArrayList<>();
                for (int i = 0; i < roomMembers.size(); i++) {
                    if (roomMembers.get(i).getMemberId() != userId) {
                        memberNames.add(roomMembers.get(i).getMemberName());
                        memberIds.add(roomMembers.get(i).getMemberId());
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(RoomActivity.this);
                builder.setTitle("입력멤버 변경");
                builder.setItems(memberNames.toArray(new CharSequence[memberNames.size()]), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        userId = memberIds.get(pos);
                        setEvent(curEvent.getEventId());
                    }
                });
                builder.show();
            }
        });

        payerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> memberNames = new ArrayList<>();
                final ArrayList<Integer> memberIds = new ArrayList<>();
                for (int i = 0; i < roomMembers.size(); i++) {
                    memberNames.add(roomMembers.get(i).getMemberName());
                    memberIds.add(roomMembers.get(i).getMemberId());
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(RoomActivity.this);
                builder.setTitle("결제자 선택");
                builder.setItems(memberNames.toArray(new CharSequence[memberNames.size()]), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        // 결제자 정보 반영
                        curEvent.setEventPayer(roomMembers.get(findMemberIndex(memberIds.get(pos))));
                        payerResult.setText(curEvent.getEventPayer().getMemberName());

                        // 결제자 DB에 반영
                        JSONObject sqlPayer = new SQLSender().
                                sendSQL("UPDATE events SET payerId=" + memberIds.get(pos) +
                                        " WHERE id = " + curEvent.getEventId());
                    }
                });
                builder.show();
            }
        });

        addReceiptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReceiptImg();
            }
        });

        prevReceiptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curReceiptIndex--;
                if (curReceiptIndex < 0) {
                    curReceiptIndex = 0;
                }
                setReceiptImg();
            }
        });

        nextReceiptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curReceiptIndex++;
                if (curReceiptIndex > curEvent.getReceiptList().size()) {
                    curReceiptIndex = curEvent.getReceiptList().size();
                }
                setReceiptImg();
            }
        });

        prevStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curEvent.getEventStatus() == Event.PERSONAL_CHECK) {
                    setEventStatus(Event.MAKE_LIST);
                    setBottomContainer();
                } else if (curEvent.getEventStatus() == Event.CONFIRM_RESULT) {
                    setEventStatus(Event.PERSONAL_CHECK);
                    setBottomContainer();
                }
                JSONObject sqlStatus = new SQLSender().
                        sendSQL("UPDATE events SET step=" + curEvent.getEventStatus() +
                                " WHERE id = " + curEvent.getEventId());
            }
        });

        nextStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curEvent.getEventStatus() == Event.MAKE_LIST) {
                    setEventStatus(Event.PERSONAL_CHECK);
                    setBottomContainer();
                } else if (curEvent.getEventStatus() == Event.PERSONAL_CHECK) {
                    if (saveResult()) {
                        setEventStatus(Event.CONFIRM_RESULT);
                        setBottomContainer();
                    }
                }
                JSONObject sqlStatus = new SQLSender().
                        sendSQL("UPDATE events SET step=" + curEvent.getEventStatus() +
                                " WHERE id = " + curEvent.getEventId());
            }
        });

        chartConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveResult();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // 갤러리에서 사진을 가지고 왔을 때의 행동 규정
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_FROM_ALBUM && data != null) {
            Uri photoUri = data.getData();
            Cursor cursor = null;
            try {
                String[] proj = {MediaStore.Images.Media.DATA};
                assert photoUri != null;
                cursor = getContentResolver().
                        query(photoUri, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();

                curEvent.getReceiptList().add(new File(cursor.getString(column_index)));
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        curReceiptIndex = curEvent.getReceiptList().size() - 1;
        setReceiptImg();
    }

    private void initRoom() {
        Intent intent = getIntent();
        // 사용자 아이디 설정
//        try {
//            userId = UserLoggedIn.getUser().getInt("id");
//        } catch (JSONException e) {
//            Log.e("Exception", "JSONException occurred in setting user");
//            e.printStackTrace();
//        }
        userId = 1;

        // 방 아이디 설정
        roomId = intent.getExtras().getInt("room_id");
        Toast.makeText(this, "roomid: "+roomId,Toast.LENGTH_LONG).show();

        // 방 이름 불러오기
        JSONObject sqlName = new SQLSender().
                sendSQL("SELECT roomname FROM rooms WHERE id="+roomId);
        try {
            if (!sqlName.getBoolean("isError"))
                roomName = sqlName.
                        getJSONArray("result").getJSONObject(0).getString("roomname");
        } catch (JSONException e) {
                Log.e("Exception", "JSONException occurred in setting room name");
                e.printStackTrace();
        }

        // 방 멤버 불러오기
        roomMembers = new ArrayList<>();
        JSONObject sqlMember = new SQLSender().
                sendSQL("SELECT * FROM users " +
                        "LEFT JOIN roomLists ON users.id = roomLists.userId " +
                        "WHERE roomId="+roomId);
        try {
            if (!sqlMember.getBoolean("isError")) {
                JSONArray memberResult = sqlMember.getJSONArray("result");
                for (int i = 0; i < memberResult.length(); i++) {
                    JSONObject member = memberResult.getJSONObject(i);
                    roomMembers.add(new Member(
                            member.getInt("id"),
                            member.getString("nickname"),
                            member.getString("email"),
                            member.getString("phone")
                    ));
                }
            }
        } catch (JSONException e) {
            Log.e("Exception", "JSONException occurred in setting room members");
            e.printStackTrace();
        }

        // 레이아웃에 방 정보 적용
        roomNameView.setText(roomName);
        roomIdView.setText("#"+roomId.toString());

        // 방 내부에 이벤트 목록 생성 및 기본 이벤트 설정
        // Integer initEventId = intent.getExtras().getInt("eventId");
        Integer initEventId = 1;
        initEvents();
        setEvent(initEventId);
    }

    private void initEvents() {
        // 방 내부 이벤트 목록 설정
        roomEvents = new ArrayList<>();
        JSONObject sqlEvents = new SQLSender().
                sendSQL("SELECT * FROM events WHERE roomId="+roomId);
        try {
            if (!sqlEvents.getBoolean("isError")) {
                JSONArray eventResult = sqlEvents.getJSONArray("result");
                for (int i = 0; i < eventResult.length(); i++) {
                    JSONObject event = eventResult.getJSONObject(i);
                    roomEvents.add(new Event(
                            event.getInt("id"),
                            event.getString("eventname"),
                            roomMembers.get(findMemberIndex(event.getInt("managerId"))),
                            roomMembers.get(findMemberIndex(event.getInt("payerId"))),
                            new ArrayList<File>(),
                            new ArrayList<RoomChartItem>(),
                            new HashMap<Integer, Integer>(),
                            event.getInt("step")
                    ));
                }
            }
        } catch (JSONException e) {
            Log.e("Exception", "JSONException occurred in setting room events");
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e("Exception", "Exception occurred in getting manager or payer");
            e.printStackTrace();
        }

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        tabListView.setLayoutManager(layoutManager);

        eventAdapter = new EventAdapter(roomEvents, new EventAdapter.TabOnClickListener() {
            @Override
            public void onTabClicked(int position) {
                setEvent(roomEvents.get(position).getEventId());
            }

            @Override
            public void onTabDeleteBtnClicked(int position) {
                eventAdapter.removeItem(position);
            }
        });
        tabListView.setAdapter(eventAdapter);

        RoomTabDecoration tabDecoration = new RoomTabDecoration();
        tabListView.addItemDecoration(tabDecoration);
    }

    private void setEvent(Integer eventId) {
        // 이벤트 정보 설정
        for (int i = 0; i < roomEvents.size(); i++) {
            if (roomEvents.get(i).getEventId() == eventId) {
                curEvent = roomEvents.get(i);
            }
        }

        // 결제자 불러오기
        payerResult.setText(curEvent.getEventPayer().getMemberName());

        // 영수증 불러오기
        // 영수증 아이디 목록 불러오기
        billIds = new ArrayList<>();
        JSONObject sqlBillIds = new SQLSender().
                sendSQL("SELECT id FROM bills WHERE eventId="+curEvent.getEventId());
        try {
            if (!sqlBillIds.getBoolean("isError")) {
                JSONArray billIdResult = sqlBillIds.getJSONArray("result");
                for (int i = 0; i < billIdResult.length(); i++) {
                    JSONObject billId = billIdResult.getJSONObject(i);
                    billIds.add(billId.getInt("id"));
                }
            }
        } catch (JSONException e) {
            Log.e("Exception", "JSONException occurred in getting bill ids");
            e.printStackTrace();
        }
        curReceiptIndex = 0;
        setReceiptImg();

        // 아이템 어댑터 적용
        chartItemAdapter = new RoomChartItemAdapter(curEvent.getChartItems(), new RoomChartItemAdapter.ChartItemOnClickListener() {
            @Override
            public void onChartItemDeleteBtnClick(int position) {
                // DB 에서 아이템 삭제
                JSONObject sqlDelItem = new SQLSender().
                        sendSQL("DELETE FROM items WHERE id=" +
                                chartItemAdapter.getItem(position).getItemId());

                // 레이아웃에 반영
                chartItemAdapter.removeItem(position);
            }
        });
        chartItemListView.setAdapter(chartItemAdapter);

        // 아이템 항목 불러오기
        chartItemAdapter.removeAll();
        for (int i = 0; i < billIds.size(); i++) {
            JSONObject sqlItems = new SQLSender().
                    sendSQL("SELECT * FROM items WHERE billId="+billIds.get(i));
            try {
                if (!sqlItems.getBoolean("isError")) {
                    JSONArray itemResult = sqlItems.getJSONArray("result");
                    for (int j = 0; j < itemResult.length(); j++) {
                        JSONObject item = itemResult.getJSONObject(j);
                        chartItemAdapter.addItem(new RoomChartItem(
                                item.getInt("id"),
                                item.getString("itemname"),
                                item.getInt("price"),
                                item.getInt("quantity")
                        ));
                    }
                }
            } catch (JSONException e) {
                Log.e("Exception", "JSONException occurred in getting items");
                e.printStackTrace();
            }
        }

        // 사용자 수준에 따른 레이아웃 설정
        if (userId == curEvent.getEventManager().getMemberId()) {
            nextStatusBtn.setVisibility(View.VISIBLE);
            prevStatusBtn.setVisibility(View.VISIBLE);
            banBtn.setVisibility(View.VISIBLE);
            payerBtn.setVisibility(View.VISIBLE);
        } else {
            nextStatusBtn.setVisibility(View.GONE);
            prevStatusBtn.setVisibility(View.GONE);
            banBtn.setVisibility(View.INVISIBLE);
            payerBtn.setVisibility(View.INVISIBLE);
        }

        // 현재 이벤트 상태에 따른 레이아웃 설정
        setEventStatus(curEvent.getEventStatus());

        // 아이템 항목별 사용 내역 불러오기
        if (curEvent.getEventStatus() == Event.CONFIRM_RESULT) {
            for (int i = 0; i < chartItemAdapter.getCount(); i++) {
                JSONObject sqlCheck = new SQLSender().
                        sendSQL("SELECT * FROM checkLists WHERE userId=" + userId +
                                " AND itemId=" + chartItemAdapter.getItem(i).getItemId());
                try {
                    if (!sqlCheck.getBoolean("isError")) {
                        JSONArray checkResult = sqlCheck.getJSONArray("result");
                        chartItemAdapter.getItem(i).
                                setItemResult(checkResult.getJSONObject(0).getInt("quantity"));
                    }
                } catch (JSONException e) {
                    Log.e("Exception", "JSONException occurred in getting check list");
                    e.printStackTrace();
                }
            }
            chartItemAdapter.notifyDataSetChanged();
        }

        // 하단바 설정
        setBottomContainer();
    }

    // 하단바 설정
    private void setBottomContainer() {
        LayoutInflater bottomInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (curEvent.getEventStatus() == Event.MAKE_LIST) {
            LinearLayout bottomContainer = findViewById(R.id.room_bottom_container);
            bottomContainer.removeAllViews();

            bottomInflater.inflate(R.layout.room_bottom_insertion, bottomContainer, true);
            itemNameEdit = findViewById(R.id.item_name_edit);
            itemCostEdit = findViewById(R.id.item_cost_edit);
            itemCountEdit = findViewById(R.id.item_count_edit);
            addItemBtn = findViewById(R.id.add_item_btn);

            addItemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int inputCount;
                    if (itemNameEdit.getText().toString().length() *
                            itemCostEdit.getText().toString().length() != 0) {
                        // handle null case
                        if (itemCountEdit.getText().toString().length() == 0) {
                            inputCount = 0;
                        } else {
                            inputCount = Integer.parseInt(itemCountEdit.getText().toString());
                        }
                        Integer itemId = 0;
                        String itemName = itemNameEdit.getText().toString();
                        Integer itemCost = Integer.parseInt(itemCostEdit.getText().toString());

                        // DB에 아이템 추가
                        JSONObject sqlItem = new SQLSender().
                                sendSQL("INSERT INTO items (itemname, quantity, price, billId)" +
                                        "VALUES (" + "'" + itemName + "'" + ", " +
                                        inputCount + ", " +
                                        itemCost + ", " +
                                        billIds.get(0) + ")");
                        try {
                            if (!sqlItem.getBoolean("isError")) {
                                itemId = sqlItem.getJSONObject("result").getInt("insertId");
                            }
                        } catch (JSONException e) {
                            Log.e("Exception", "JSONException occurred in add item");
                            e.printStackTrace();
                        }

                        // 레이아웃에 반영
                        RoomChartItem roomChartItem = new RoomChartItem(
                                // 항목 id 결정 필요
                                itemId,
                                itemName,
                                itemCost,
                                inputCount
                        );
                        chartItemAdapter.addItem(roomChartItem);
                        itemNameEdit.setText(null);
                        itemCostEdit.setText(null);
                        itemCountEdit.setText(null);
                    }
                }
            });

        } else {
            LinearLayout bottomContainer = findViewById(R.id.room_bottom_container);
            bottomContainer.removeAllViews();

            bottomInflater.inflate(R.layout.room_bottom_confirm, bottomContainer, true);
            confirmBtn = findViewById(R.id.room_result_confirm_btn);

            confirmBtn.setEnabled(true);
            if (curEvent.getEventStatus() == Event.PERSONAL_CHECK) {
                confirmBtn.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                confirmBtn.setEnabled(false);
            }

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
                            intent.putExtra("roomId", roomId);
                            intent.putExtra("eventId", curEvent.getEventId());
                            startActivity(intent);
                        }
                    });
                    builder.show();
                }
            });
        }
    }

    // 이벤트 상태에 따른 설정
    private void setEventStatus(int eventStatus) {
        switch (eventStatus) {
            case Event.MAKE_LIST:
                curEvent.setEventStatus(Event.MAKE_LIST);
                chartItemAdapter.setEventStatus(Event.MAKE_LIST);
                eventStatus1.setTextColor(getResources().getColor(R.color.colorJustPay));
                eventStatus1.setTypeface(eventStatus1.getTypeface(), Typeface.BOLD);
                eventStatus2.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
                eventStatus2.setTypeface(eventStatus1.getTypeface(), Typeface.NORMAL);
                eventStatus3.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
                eventStatus3.setTypeface(eventStatus1.getTypeface(), Typeface.NORMAL);
                if (userId == curEvent.getEventManager().getMemberId()) {
                    prevStatusBtn.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    nextStatusBtn.setBackgroundColor(getResources().getColor(R.color.colorJustPay));
                    chartConfirmBtn.setVisibility(View.GONE);
                }
                break;
            case Event.PERSONAL_CHECK:
                curEvent.setEventStatus(Event.PERSONAL_CHECK);
                chartItemAdapter.setEventStatus(Event.PERSONAL_CHECK);
                eventStatus1.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
                eventStatus1.setTypeface(eventStatus1.getTypeface(), Typeface.NORMAL);
                eventStatus2.setTextColor(getResources().getColor(R.color.colorJustPay));
                eventStatus2.setTypeface(eventStatus1.getTypeface(), Typeface.BOLD);
                eventStatus3.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
                eventStatus3.setTypeface(eventStatus1.getTypeface(), Typeface.NORMAL);
                if (userId == curEvent.getEventManager().getMemberId()) {
                    prevStatusBtn.setBackgroundColor(getResources().getColor(R.color.colorJustPay));
                    nextStatusBtn.setBackgroundColor(getResources().getColor(R.color.colorJustPay));
                    chartConfirmBtn.setVisibility(View.GONE);
                } else {
                    chartConfirmBtn.setVisibility(View.VISIBLE);
                }
                break;
            case Event.CONFIRM_RESULT:
                curEvent.setEventStatus(Event.CONFIRM_RESULT);
                chartItemAdapter.setEventStatus(Event.CONFIRM_RESULT);
                eventStatus1.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
                eventStatus1.setTypeface(eventStatus1.getTypeface(), Typeface.NORMAL);
                eventStatus2.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
                eventStatus2.setTypeface(eventStatus1.getTypeface(), Typeface.NORMAL);
                eventStatus3.setTextColor(getResources().getColor(R.color.colorJustPay));
                eventStatus3.setTypeface(eventStatus1.getTypeface(), Typeface.BOLD);
                if (userId == curEvent.getEventManager().getMemberId()) {
                    prevStatusBtn.setBackgroundColor(getResources().getColor(R.color.colorJustPay));
                    nextStatusBtn.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    chartConfirmBtn.setVisibility(View.GONE);
                }

                break;
        }
    }

    private void addReceiptImg() {
        goToAlbum();
    }

    private void getPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.photo_permission2))
                .setDeniedMessage(getResources().getString(R.string.photo_permission1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    // 영수증 레이아웃 설정
    private void setReceiptImg() {
        if (curReceiptIndex >= 0 && curReceiptIndex < curEvent.getReceiptList().size()) {
            receiptImg.setVisibility(View.VISIBLE);
            addReceiptBtn.setVisibility(View.INVISIBLE);
            addReceiptText.setVisibility(View.INVISIBLE);
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap imgBm = BitmapFactory.
                    decodeFile(curEvent.getReceiptList().get(curReceiptIndex).getAbsolutePath(), options);
            receiptImg.setImageBitmap(imgBm);
            receiptAttacher = new PhotoViewAttacher(receiptImg);
            receiptAttacher.setMaximumScale(10);
            receiptAttacher.update();
            receiptStatus.setText(new Integer(curReceiptIndex + 1).toString() + " / " +
                    new Integer(curEvent.getReceiptList().size()).toString());
        } else {
            addReceiptBtn.setVisibility(View.VISIBLE);
            addReceiptText.setVisibility(View.VISIBLE);
            receiptImg.setVisibility(View.INVISIBLE);
            receiptStatus.setText("+ / " + new Integer(curEvent.getReceiptList().size()).toString());
        }
    }

    // 사용자가 입력한 사용 내역 저장
    private Boolean saveResult() {
        if (!chartItemAdapter.getResult(this).first) {
            curEvent.setChartResult(chartItemAdapter.getResult(this).second);

            // 항목 결과 DB 반영
            for (int i = 0; i < chartItemAdapter.getCount(); i++) {
                JSONObject sqlReset = new SQLSender().
                        sendSQL("DELETE FROM checkLists" +
                                " WHERE itemId=" + chartItemAdapter.getItem(i).getItemId() +
                                " AND userId=" + userId);
                if (chartItemAdapter.getItem(i).getItemResult() != 0) {
                    JSONObject sqlResult = new SQLSender().
                            sendSQL("INSERT INTO checkLists (itemId, userId, quantity)" +
                                    "VALUES (" + chartItemAdapter.getItem(i).getItemId() + ", " +
                                    userId + ", " +
                                    chartItemAdapter.getItem(i).getItemResult() + ")");
                }
            }
            return true;
        } else {
            return false;
        }
    }

    // Member 목록에서 id를 검색해 member 목록 인덱스 반환
    private int findMemberIndex(Integer memberId) {
        for (int i = 0; i < roomMembers.size(); i++) {
            if (roomMembers.get(i).getMemberId() == memberId) {
                return i;
            }
        }
        return 0;
    }

    // Event 값 확인
    private void logCurEvent() {
        Log.d("event", curEvent.getEventId().toString());
        Log.d("event", curEvent.getEventTitle());
        Log.d("event", curEvent.getEventManager().getMemberName());
        if (curEvent.getEventPayer() != null) {
            Log.d("event", curEvent.getEventPayer().getMemberName());
        }
        for (int i = 0; i < curEvent.getChartItems().size(); i++) {
            Log.d("event", curEvent.getChartItems().get(i).getItemName());
        }
        Log.d("event", curEvent.getChartResult().keySet().toString());
        Log.d("event", curEvent.getChartResult().values().toString());
        Log.d("event", new Integer(curEvent.getEventStatus()).toString());
    }
}
