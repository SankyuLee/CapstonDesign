package edu.skku.capstone.justpay;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class RoomListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button open_btn;
    Button search_btn;
    Button add_btn;
    Button close_btn;

    NavigationView personal_menu;
    DrawerLayout drawerLayout;
    View drawerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        //final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (View)findViewById(R.id.drawer);

        add_btn = (Button)findViewById(R.id.add_btn);
        search_btn = (Button)findViewById(R.id.search_btn);
        open_btn = (Button)findViewById(R.id.open_btn);
        close_btn = (Button)findViewById(R.id.close_btn);

        personal_menu = (NavigationView)findViewById(R.id.personal_menu);

        //방 생성 다이얼로그
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View create_room = View.inflate(RoomListActivity.this,R.layout.dialog_create_room, null);

                AlertDialog.Builder dialog = new AlertDialog.Builder(RoomListActivity.this);
                dialog.setView(create_room);

                dialog.show();
                dialog.setCancelable(false);


            }
        });

        //검색 버튼
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        open_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerLayout.isDrawerOpen(drawerView)) {
                    drawerLayout.openDrawer(drawerView);
                }
            }
        });

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

        ListView roomList;
        roomList = (ListView)findViewById(R.id.roomList);

        ArrayList<RoomList_item> list = new ArrayList<RoomList_item>();
        RoomList_item item = new RoomList_item("#0202","종설프");
        list.add(item);

        RoomListAdapter adapter = new RoomListAdapter(this, list);
        roomList.setAdapter(adapter);
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


    // TODO: 2019-05-19 navigation view 없애고 그냥 linear layout으로 바꿔서 메뉴 만들기
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_modify) {
            Toast.makeText(RoomListActivity.this, "touched", Toast.LENGTH_LONG);
        } else if (id == R.id.nav_logout) {

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
