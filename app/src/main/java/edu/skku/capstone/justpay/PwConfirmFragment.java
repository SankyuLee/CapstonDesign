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
import android.widget.Toast;

public class PwConfirmFragment extends Fragment {
    PersonalActivity activity;
    Context context;

    Button btn_pw_cancel;
    Button btn_pw_confirm;

    EditText pw_input;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_pw_confirm, container, false);

        activity = (PersonalActivity) getActivity();
        context = activity.context;

        btn_pw_cancel = rootView.findViewById(R.id.btn_pw_cancel);
        btn_pw_confirm = rootView.findViewById(R.id.btn_pw_confirm);

        pw_input = rootView.findViewById(R.id.pw_input);

        btn_pw_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finishActivity();
            }
        });

        btn_pw_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //비밀번호 확인
                String input_text = pw_input.getText().toString();

                if(activity.user_pw.equals(input_text))
                    activity.reviseUserInfo();
                else Toast.makeText(context, "비밀번호가 올바르지 않습니다",Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }
}
