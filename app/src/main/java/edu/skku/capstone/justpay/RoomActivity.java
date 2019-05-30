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

// 영수증 DB 연결
// 상태별 chart layout 변경
// 버튼 기능 추가

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

    private ImageView receiptImg;
    private ImageButton addReceiptBtn, prevReceiptBtn, nextReceiptBtn;
    private TextView addReceiptText, receiptStatus;
    private PhotoViewAttacher receiptAttacher;

    private TextView eventStatus1, eventStatus2, eventStatus3;

    private ListView chartItemListView;
    private RoomChartItemAdapter chartItemAdapter;

    private Button prevStatusBtn, nextStatusBtn;

    private TextView typingStatus;

    private EditText itemNameEdit, itemCostEdit, itemCountEdit;
    private ImageButton addItemBtn;
    private Button confirmBtn;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Test Data
    //
    private ArrayList<Member> members = new ArrayList<>(
            Arrays.asList(
                    new Member(0, "이지훈",
                            "ulla4571@g.skku.edu", "01032104571"),
                    new Member(1, "조현진",
                            "guswh11@skku.edu", "01000000000"),
                    new Member(2, "오승민",
                            "xxxx@gmail.com", "01000000000")));
    private ArrayList<RoomChartItem> items1 = new ArrayList<>(
            Arrays.asList(
                    new RoomChartItem(0, "커피", new Integer(3000), new Integer(2)),
                    new RoomChartItem(1, "쿠키", new Integer(5400), new Integer(3)),
                    new RoomChartItem(2, "설탕", new Integer(5400), new Integer(3)),
                    new RoomChartItem(3, "공기", new Integer(100), new Integer(0))));
    private ArrayList<RoomChartItem> items2 = new ArrayList<>(
            Arrays.asList(
                    new RoomChartItem(0, "커피", new Integer(3000), new Integer(2)),
                    new RoomChartItem(1, "쿠키", new Integer(5400), new Integer(3)),
                    new RoomChartItem(2, "설탕", new Integer(5400), new Integer(3)),
                    new RoomChartItem(3, "공기", new Integer(100), new Integer(0))));
    private ArrayList<Event> events = new ArrayList<>(
            Arrays.asList(
                    new Event(0, "05/07", members.get(0), null,
                            new ArrayList<File>(), items1, new HashMap<Integer, Integer>(), Event.MAKE_LIST),
                    new Event(0, "05/08", members.get(0),  null,
                            new ArrayList<File>(), items2, new HashMap<Integer, Integer>(), Event.PERSONAL_CHECK)));
    ///////////////////////////////////////////////////////////////////////////////////////////////

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
        typingStatus = findViewById(R.id.room_typing_status);

        initRoom();

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
                logCurEvent();
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
        // roomId = intent.getExtras().getInt("roomId");
        roomId = 1;

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

        // 영수증 불러오기
        // 영수증 아이디 목록 불러오기
        ArrayList<Integer> billIds = new ArrayList<>();
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

        // 현재 이벤트 상태에 따른 레이아웃 설정
        setEventStatus(curEvent.getEventStatus());

        // 아이템 어댑터 적용
        chartItemAdapter = new RoomChartItemAdapter(curEvent.getChartItems(), new RoomChartItemAdapter.ChartItemOnClickListener() {
            @Override
            public void onChartItemDeleteBtnClick(int position) {
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

        // 아이템 항목별 사용 내역 불러오기
        if (curEvent.getEventStatus() == Event.CONFIRM_RESULT) {
            for (int i = 0; i < chartItemAdapter.getCount(); i++) {
                JSONObject sqlCheck = new SQLSender().
                        sendSQL("SELECT * FROM checkLists WHERE userId=" + userId +
                                "AND itemId=" + chartItemAdapter.getItem(i).getItemId());
                try {
                    if (!sqlCheck.getBoolean("isError")) {
                        JSONArray checkResult = sqlCheck.getJSONArray("result");
                        for (int j = 0; j < checkResult.length(); j++) {
                            JSONObject checkItem = checkResult.getJSONObject(j);
                            curEvent.getChartResult().
                                    put(chartItemAdapter.getItem(i).getItemId(), checkItem.getInt("quantity"));
                        }
                    }
                } catch (JSONException e) {
                    Log.e("Exception", "JSONException occurred in getting check list");
                    e.printStackTrace();
                }
            }
            // 사용 내역 설정
            applyResult();
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
                        RoomChartItem roomChartItem = new RoomChartItem(
                                // 항목 id 결정 필요
                                curEvent.getChartItems().size(),
                                itemNameEdit.getText().toString(),
                                Integer.parseInt(itemCostEdit.getText().toString()),
                                inputCount
                        );
                        chartItemAdapter.addItem(roomChartItem);
                        itemNameEdit.setText(null);
                        itemCostEdit.setText(null);
                        itemCountEdit.setText(null);
                    }
                    logCurEvent();
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
                    // 결제자 선택
                    curEvent.setEventPayer(members.get(0));
                    logCurEvent();

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

    // 이벤트 상태에 따른 레이아웃 설정
    private void setEventStatus(int eventStatus) {
        switch (eventStatus) {
            case Event.MAKE_LIST:
                curEvent.setEventStatus(Event.MAKE_LIST);
                eventStatus1.setTextColor(getResources().getColor(R.color.colorJustPay));
                eventStatus1.setTypeface(eventStatus1.getTypeface(), Typeface.BOLD);
                eventStatus2.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
                eventStatus2.setTypeface(eventStatus1.getTypeface(), Typeface.NORMAL);
                eventStatus3.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
                eventStatus3.setTypeface(eventStatus1.getTypeface(), Typeface.NORMAL);
                prevStatusBtn.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                nextStatusBtn.setBackgroundColor(getResources().getColor(R.color.colorJustPay));
                break;
            case Event.PERSONAL_CHECK:
                curEvent.setEventStatus(Event.PERSONAL_CHECK);
                eventStatus1.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
                eventStatus1.setTypeface(eventStatus1.getTypeface(), Typeface.NORMAL);
                eventStatus2.setTextColor(getResources().getColor(R.color.colorJustPay));
                eventStatus2.setTypeface(eventStatus1.getTypeface(), Typeface.BOLD);
                eventStatus3.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
                eventStatus3.setTypeface(eventStatus1.getTypeface(), Typeface.NORMAL);
                prevStatusBtn.setBackgroundColor(getResources().getColor(R.color.colorJustPay));
                nextStatusBtn.setBackgroundColor(getResources().getColor(R.color.colorJustPay));
                break;
            case Event.CONFIRM_RESULT:
                curEvent.setEventStatus(Event.CONFIRM_RESULT);
                eventStatus1.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
                eventStatus1.setTypeface(eventStatus1.getTypeface(), Typeface.NORMAL);
                eventStatus2.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
                eventStatus2.setTypeface(eventStatus1.getTypeface(), Typeface.NORMAL);
                eventStatus3.setTextColor(getResources().getColor(R.color.colorJustPay));
                eventStatus3.setTypeface(eventStatus1.getTypeface(), Typeface.BOLD);
                prevStatusBtn.setBackgroundColor(getResources().getColor(R.color.colorJustPay));
                nextStatusBtn.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
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
            return true;
        } else {
            return false;
        }
    }

    // 사용 내역 적용
    private void applyResult() {
        for (int i = 0; i < chartItemAdapter.getCount(); i++) {
            chartItemAdapter.getItem(i).
                    setItemResult(curEvent.getChartResult().get(chartItemAdapter.getItem(i).getItemId()));
        }
    }

    // Member 목록에서 id를 검색해 member 목록 인덱스 반환
    private Integer findMemberIndex(Integer memberId) {
        for (int i = 0; i < roomMembers.size(); i++) {
            if (roomMembers.get(i).getMemberId() == memberId) {
                return i;
            }
        }
        return -1;
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
