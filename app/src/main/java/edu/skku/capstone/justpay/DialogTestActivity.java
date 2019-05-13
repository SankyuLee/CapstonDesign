package edu.skku.capstone.justpay;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

public class DialogTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_test);

        Button button1 = (Button) findViewById(R.id.dialogButton1);
        button1.setOnClickListener(new View.OnClickListener() { // 멤버 추가
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"Member1", "Member2", "Member3", "Member4"};  // DB연동 필요

                AlertDialog.Builder builder = new AlertDialog.Builder(DialogTestActivity.this);  // context 변경 (-Activity.this -> this)
                builder.setTitle("멤버 추가");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        //멤버 추가시 액션
                    }
                });
                builder.show();
            }
        });

        Button button2 = (Button) findViewById(R.id.dialogButton2);
        button2.setOnClickListener(new View.OnClickListener() { // 멤버 추방
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"Member1", "Member2", "Member3", "Member4"};  // DB연동 필요

                AlertDialog.Builder builder = new AlertDialog.Builder(DialogTestActivity.this);  // context 변경 (-Activity.this -> this)
                builder.setTitle("멤버 추방");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        //멤버 추방시 액션
                    }
                });
                builder.show();
            }
        });

        Button button3 = (Button) findViewById(R.id.dialogButton3);
        button3.setOnClickListener(new View.OnClickListener() {
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

                AlertDialog.Builder builder = new AlertDialog.Builder(DialogTestActivity.this); // context 변경 (-Activity.this -> this)
                builder.setTitle("방 생성하기");
                builder.setView(alertLayoutView);
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 방 생성 취소시 액션
                    }
                });
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 방 생성시 액션
                    }
                });
                builder.show();
            }
        });

        Button button4 = (Button) findViewById(R.id.dialogButton4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View alertLayoutView = inflater.inflate(R.layout.dialog_enter_room, null);
                final EditText roomName = alertLayoutView.findViewById(R.id.roomNameEditText);
                final EditText roomPW = alertLayoutView.findViewById(R.id.roomPWEditText);

                AlertDialog.Builder builder = new AlertDialog.Builder(DialogTestActivity.this); // context 변경 (-Activity.this -> this)
                builder.setTitle("방 입장하기");
                builder.setView(alertLayoutView);
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 방 입장 취소시 액션
                    }
                });
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 방 입장시 액션
                    }
                });
                builder.show();
            }
        });

        Button button5 = (Button) findViewById(R.id.dialogButton5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"Member1", "Member2", "Member3", "Member4"};  // DB연동 필요

                AlertDialog.Builder builder = new AlertDialog.Builder(DialogTestActivity.this);  // context 변경 (-Activity.this -> this)
                builder.setTitle("입력멤버 변경");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        //입력멤버 변경시 액션
                    }
                });
                builder.show();
            }
        });

        Button button6 = (Button) findViewById(R.id.dialogButton6);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"사용자별 결과 확인", "항목별 결과 확인"};

                AlertDialog.Builder builder = new AlertDialog.Builder(DialogTestActivity.this);  // context 변경 (-Activity.this -> this)
                builder.setTitle("결과 확인");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {  // pos 0: 사용자별 결과 확인, pos 1: 항목별 결과 확인

                    }
                });
                builder.show();
            }
        });

        Button button7 = (Button) findViewById(R.id.dialogButton7);
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"카카오톡 메시지로 공유", "엑셀 파일로 공유", "URL로 공유"};

                AlertDialog.Builder builder = new AlertDialog.Builder(DialogTestActivity.this);  // context 변경 (-Activity.this -> this)
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
