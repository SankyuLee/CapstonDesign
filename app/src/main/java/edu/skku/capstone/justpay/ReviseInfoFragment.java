package edu.skku.capstone.justpay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class ReviseInfoFragment extends Fragment {
    PersonalActivity activity;
    Context context;

    Button btn_revise_confirm;
    Button btn_revise_cancel;

    TextView email_text;

    EditText pw_input;
    EditText pw_chk_input;
    EditText nickname_input;
    EditText phone_input;

    String pw_text;
    String pw_chK_text;
    String nickname_text;
    String phone_text;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_revise_info, container, false);

        activity = (PersonalActivity) getActivity();
        context = activity.context;

        btn_revise_cancel = rootView.findViewById(R.id.btn_revise_cancel);
        btn_revise_confirm = rootView.findViewById(R.id.btn_revise_confirm);

        email_text = rootView.findViewById(R.id.email);

        pw_input = rootView.findViewById(R.id.revise_input_pw);
        pw_chk_input = rootView.findViewById(R.id.revise_pw_check);
        nickname_input = rootView.findViewById(R.id.revise_nickname);
        phone_input = rootView.findViewById(R.id.revise_phone);

        email_text.setText(activity.user_email);

        btn_revise_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw_text = pw_input.getText().toString();
                pw_chK_text = pw_chk_input.getText().toString();
                nickname_text = nickname_input.getText().toString();
                phone_text = phone_input.getText().toString();

                if((pw_text.equals(""))&&(pw_chK_text.equals(""))){
                    storeInfo();
                    activity.finishActivity();
                }else {
                    if(chkPw()) {
                        storeInfo();
                        Toast.makeText(context, "다시 로그인해 주세요",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                        activity.finish();

                    }
                }
            }
        });

        btn_revise_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finishActivity();
            }
        });

        return rootView;
    }

    private boolean chkPw(){
        //조건검사도 넣기
        if(pw_text.equals(pw_chK_text)){
            JSONObject update_pw = new SQLSender().sendSQL("UPDATE users set password = '"+pw_text+"' where id ='"+activity.user_id+"';");
            return true;
        }
        else{
            Toast.makeText(context,"비밀번호 입력값이 다릅니다",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void storeInfo(){
        if(!nickname_text.equals("")){
            JSONObject update_nickname = new SQLSender().sendSQL("UPDATE users set nickname = '" +nickname_text+
                    "' where id ='"+activity.user_id+"';");
        }

        if(!phone_text.equals("")){
            JSONObject update_phone = new SQLSender().sendSQL("UPDATE users set phone = '" +phone_text+
                    "' where id ='"+activity.user_id+"';");
        }

        Toast.makeText(context, "회원정보가 수정되었습니다",Toast.LENGTH_LONG).show();
    }
}
