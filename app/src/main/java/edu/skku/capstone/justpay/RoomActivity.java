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

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity{

    private TextView roomTitle, roomID;
    private ImageButton banBtn, addBtn, changeBtn;

    private RecyclerView tabListView;
    private RoomTabAdapter tabAdapter;

    private ImageView receiptImg;
    private ImageButton addReceiptBtn, prevReceiptBtn, nextReceiptBtn;
    private TextView addReceiptText, receiptStatus;

    private TextView eventStatus1, eventStatus2, eventStatus3;

    private ListView chartItemListView;
    private RoomChartItemAdapter chartItemAdapter;

    private Button prevStatusBtn, nextStatusBtn;

    private TextView typingStatus;

    private EditText itemNameEdit, itemCostEdit, itemCountEdit;
    private ImageButton addItemBtn;
    private Button confirmBtn;

    private EventStatus eventStatus;
    private enum EventStatus { MAKE_LIST,  PERSONAL_CHECK, CONFIRM_RESULT }

    private ArrayList<File> receiptList;
    private Integer curReceiptIndex;
    private static final int PICK_FROM_ALBUM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        getPermission();

        roomID = findViewById(R.id.room_id);
        roomTitle = findViewById(R.id.room_id);
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
                if (curReceiptIndex > receiptList.size()) {
                    curReceiptIndex = receiptList.size();
                }
                setReceiptImg();
            }
        });

        prevStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventStatus == EventStatus.PERSONAL_CHECK) {
                    setStatus(EventStatus.MAKE_LIST);
                    setBottomContainer();
                } else if (eventStatus == EventStatus.CONFIRM_RESULT) {
                    setStatus(EventStatus.PERSONAL_CHECK);
                    setBottomContainer();
                }
            }
        });

        nextStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventStatus == EventStatus.MAKE_LIST) {
                    setStatus(EventStatus.PERSONAL_CHECK);
                    setBottomContainer();
                } else if (eventStatus == EventStatus.PERSONAL_CHECK) {
                    setStatus(EventStatus.CONFIRM_RESULT);
                    setBottomContainer();
                }
            }
        });
    }

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

                receiptList.add(new File(cursor.getString(column_index)));
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        curReceiptIndex = receiptList.size() - 1;
        setReceiptImg();
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
                setTab(tabList.get(position).getTabTitle());
            }

            @Override
            public void onTabDeleteBtnClicked(int position) {
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
        receiptList = new ArrayList<>(); // Receipt
        int eventStatusInt = 0; // Status
        ArrayList<RoomChartItem> itemList = new ArrayList<>(); // Item List
        itemList.add(new RoomChartItem("커피", new Integer(3000), new Integer(2)));
        itemList.add(new RoomChartItem("쿠키", new Integer(5400), new Integer(3)));
        itemList.add(new RoomChartItem("설탕", new Integer(5400), new Integer(3)));
        itemList.add(new RoomChartItem("공기", new Integer(100), new Integer(0)));

        // Set receipt images
        if (receiptList.size() > 0) {
            curReceiptIndex = receiptList.size() - 1;
        } else {
            curReceiptIndex = 0;
        }
        setReceiptImg();

        // Set event status
        switch (eventStatusInt) {
            case 0:
                setStatus(EventStatus.MAKE_LIST);
                break;
            case 1:
                setStatus(EventStatus.PERSONAL_CHECK);
                break;
            case 2:
                setStatus(EventStatus.CONFIRM_RESULT);
                break;
        }

        // Set item chart
        chartItemAdapter = new RoomChartItemAdapter(itemList, new RoomChartItemAdapter.ChartItemOnClickListener() {
            @Override
            public void onChartItemDeleteBtnClick(int position) {
                chartItemAdapter.removeItem(position);
            }
        });
        chartItemListView.setAdapter(chartItemAdapter);

        // Set bottom container
        setBottomContainer();
    }

    private void setBottomContainer() {
        LayoutInflater bottomInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (eventStatus == EventStatus.MAKE_LIST) {
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
                                itemNameEdit.getText().toString(),
                                Integer.parseInt(itemCostEdit.getText().toString()),
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
            if (eventStatus == EventStatus.PERSONAL_CHECK) {
                confirmBtn.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
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
                            startActivity(intent);
                        }
                    });
                    builder.show();
                }
            });
        }
    }

    private void setStatus(EventStatus eventStatus) {
        switch (eventStatus) {
            case MAKE_LIST:
                this.eventStatus = EventStatus.MAKE_LIST;
                eventStatus1.setTextColor(getResources().getColor(R.color.colorJustPay));
                eventStatus1.setTypeface(eventStatus1.getTypeface(), Typeface.BOLD);
                eventStatus2.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
                eventStatus2.setTypeface(eventStatus1.getTypeface(), Typeface.NORMAL);
                eventStatus3.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
                eventStatus3.setTypeface(eventStatus1.getTypeface(), Typeface.NORMAL);
                prevStatusBtn.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                nextStatusBtn.setBackgroundColor(getResources().getColor(R.color.colorJustPay));
                break;
            case PERSONAL_CHECK:
                this.eventStatus = EventStatus.PERSONAL_CHECK;
                eventStatus1.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
                eventStatus1.setTypeface(eventStatus1.getTypeface(), Typeface.NORMAL);
                eventStatus2.setTextColor(getResources().getColor(R.color.colorJustPay));
                eventStatus2.setTypeface(eventStatus1.getTypeface(), Typeface.BOLD);
                eventStatus3.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
                eventStatus3.setTypeface(eventStatus1.getTypeface(), Typeface.NORMAL);
                prevStatusBtn.setBackgroundColor(getResources().getColor(R.color.colorJustPay));
                nextStatusBtn.setBackgroundColor(getResources().getColor(R.color.colorJustPay));
                break;
            case CONFIRM_RESULT:
                this.eventStatus = EventStatus.CONFIRM_RESULT;
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

    private void setReceiptImg() {
        if (curReceiptIndex >= 0 && curReceiptIndex < receiptList.size()) {
            receiptImg.setVisibility(View.VISIBLE);
            addReceiptBtn.setVisibility(View.INVISIBLE);
            addReceiptText.setVisibility(View.INVISIBLE);
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap imgBm = BitmapFactory.
                    decodeFile(receiptList.get(curReceiptIndex).getAbsolutePath(), options);
            receiptImg.setImageBitmap(imgBm);
            receiptStatus.setText(new Integer(curReceiptIndex + 1).toString() + " / " +
                    new Integer(receiptList.size()).toString());
        } else {
            addReceiptBtn.setVisibility(View.VISIBLE);
            addReceiptText.setVisibility(View.VISIBLE);
            receiptImg.setVisibility(View.INVISIBLE);
            receiptStatus.setText("+ / " + new Integer(receiptList.size()).toString());
        }
    }
}
