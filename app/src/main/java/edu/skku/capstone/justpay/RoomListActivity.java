package edu.skku.capstone.justpay;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class RoomListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText search_editText;

    Button open_btn;
    Button search_btn;
    Button add_btn;
    Button close_btn;
    FloatingActionButton room_fab;
    FloatingActionButton room_search_fab;
    FloatingActionButton room_add_fab;

    NavigationView personal_menu;
    DrawerLayout drawerLayout;
    View drawerView;
    ListView roomList;
    ConstraintLayout room_list_layout;

    private boolean isMenuCollapsed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        search_editText = (EditText)findViewById(R.id.search_editText);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (View)findViewById(R.id.drawer);

        add_btn = (Button)findViewById(R.id.add_btn);
        search_btn = (Button)findViewById(R.id.search_btn);
        open_btn = (Button)findViewById(R.id.open_btn);
        close_btn = (Button)findViewById(R.id.close_btn);
        room_fab = (FloatingActionButton)findViewById(R.id.fab_room);
        room_search_fab = (FloatingActionButton)findViewById(R.id.fab_room_search);
        room_add_fab = (FloatingActionButton)findViewById(R.id.fab_room_add);

        personal_menu = (NavigationView)findViewById(R.id.personal_menu);
        personal_menu.setNavigationItemSelectedListener(this);

        roomList = (ListView)findViewById(R.id.roomList);
        room_list_layout = (ConstraintLayout)findViewById(R.id.room_list_layout);

        final ArrayList<RoomList_item> list = new ArrayList<RoomList_item>();
        final RoomListAdapter adapter = new RoomListAdapter(this, list);

        roomList.setTextFilterEnabled(true);
        roomList.setAdapter(adapter);

        final ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(room_list_layout);
        constraintSet.connect(room_search_fab.getId(), ConstraintSet.BOTTOM, room_fab.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(room_add_fab.getId(), ConstraintSet.BOTTOM, room_fab.getId(), ConstraintSet.BOTTOM);


        room_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMenuCollapsed) {
                    constraintSet.connect(room_search_fab.getId(), ConstraintSet.BOTTOM, room_fab.getId(), ConstraintSet.TOP);
                    constraintSet.connect(room_add_fab.getId(), ConstraintSet.BOTTOM, room_search_fab.getId(), ConstraintSet.TOP);
                    constraintSet.setMargin(room_search_fab.getId(),ConstraintSet.BOTTOM,40);
                    constraintSet.setMargin(room_add_fab.getId(),ConstraintSet.BOTTOM,40);
                } else {
                    constraintSet.connect(room_search_fab.getId(), ConstraintSet.BOTTOM, room_fab.getId(), ConstraintSet.BOTTOM);
                    constraintSet.connect(room_add_fab.getId(), ConstraintSet.BOTTOM, room_fab.getId(), ConstraintSet.BOTTOM);
                    constraintSet.setMargin(room_search_fab.getId(),ConstraintSet.BOTTOM,0);
                    constraintSet.setMargin(room_add_fab.getId(),ConstraintSet.BOTTOM,0);
                }
                AutoTransition transition = new AutoTransition();
                transition.setDuration(300);
                transition.setInterpolator(new AccelerateDecelerateInterpolator());

                TransitionManager.beginDelayedTransition(room_list_layout, transition);
                constraintSet.applyTo(room_list_layout);

                isMenuCollapsed = !isMenuCollapsed;
            }
        });

        //방 생성 버튼
        room_add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View alertLayoutView = inflater.inflate(R.layout.dialog_create_room, null);
                final EditText roomName = alertLayoutView.findViewById(R.id.roomNameEditText);
                final EditText roomPW = alertLayoutView.findViewById(R.id.roomPWEditText);
                final EditText eventName = alertLayoutView.findViewById(R.id.eventNameEditText);
                final CheckBox checkBox = alertLayoutView.findViewById(R.id.roomCreateCheckBox);

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (isChecked) {
                            // 세부 항목 정산
                        } else {
                            // 세부 항목 정산 X
                        }
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(RoomListActivity.this); // context 변경 (-Activity.this -> this)
                builder.setTitle("방 생성하기");
                builder.setView(alertLayoutView);
                builder.setCancelable(false); // 바깥 클릭해도 안꺼지게
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 방 생성 취소시 액션
                    }
                });
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //리스트에 항목 추가
                        String roomName_s = roomName.getText().toString();
                        String roomPW_s = roomPW.getText().toString();
                        String eventName_s = eventName.getText().toString();
                        int tag = 1000;

                        RoomList_item item = new RoomList_item("#"+tag,roomName_s);
                        list.add(item);
                        adapter.notifyDataSetChanged();

                        Intent intent;
                        intent = new Intent(RoomListActivity.this, RoomActivity.class);
                        startActivity(intent);
                    }
                });
                builder.show();
            }
        });

        //전체 방 검색 버튼
        room_search_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //방 삭제
        roomList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                LayoutInflater inflater = getLayoutInflater();
                View alertLayoutView = inflater.inflate(R.layout.dialog_delete_room, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(RoomListActivity.this);

                builder.setView(alertLayoutView);
                builder.setCancelable(true);
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      list.remove(position);
                      adapter.notifyDataSetChanged();
                    }
                });
                builder.show();

                return true;
            }
        });

        search_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = s.toString();
                ((RoomListAdapter)roomList.getAdapter()).getFilter().filter(searchText);
            }
        });

        //검색 버튼
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //drawer 여는 버튼
        open_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerLayout.isDrawerOpen(drawerView)) {
                    drawerLayout.openDrawer(drawerView);
                }
            }
        });

        //drawer 닫는 버튼
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(drawerView)) {
                    drawerLayout.closeDrawer(drawerView);
                }
            }
        });

        drawerLayout.setDrawerListener(myDrawerListener);
        drawerView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return true;
            }
        });
    }

    DrawerLayout.DrawerListener myDrawerListener = new DrawerLayout.DrawerListener() {
        public void onDrawerClosed(View drawerView) {
        }

        public void onDrawerOpened(View drawerView) {
        }

        public void onDrawerSlide(View drawerView, float slideOffset) {
        }

        public void onDrawerStateChanged(int newState) {
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_modify) {
            Toast.makeText(this, "touched", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
