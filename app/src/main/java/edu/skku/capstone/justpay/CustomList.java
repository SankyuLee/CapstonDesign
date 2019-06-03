package edu.skku.capstone.justpay;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.skku.capstone.justpay.resultlist_item;
public class CustomList extends BaseExpandableListAdapter {
    Context mContext;
    ArrayList<resultlist_item> personlists;
    ArrayList<ArrayList<personal_item>> itemlists;
    private JSONObject loginUser;
    private String userName;
    @Override
    public int getGroupCount() {
        return personlists.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return itemlists.get(groupPosition).size();
    }

    //리스트의 아이템 반환
    @Override
    public resultlist_item getGroup(int groupPosition) {

        return personlists.get(groupPosition);
    }

    @Override
    public personal_item getChild(int groupPosition, int childPosition) {
        return itemlists.get(groupPosition).get(childPosition);
    }

    //리스트 아이템의 id 반환
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    //동일한 id가 항상 동일한 개체를 참조하는지 여부를 반환
    @Override
    public boolean hasStableIds() {
        return true;
    }

    //리스트 각각의 row에 view를 설정
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = convertView;
        Context context = parent.getContext();

        //convertView가 비어있을 경우 xml파일을 inflate 해줌
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item, parent, false);
        }

        //View들은 반드시 아이템 레이아웃을 inflate한 뒤에 작성할 것
        ImageView arrowIcon = (ImageView) v.findViewById(R.id.imageView);
        TextView name = (TextView) v.findViewById(R.id.textView1);
        TextView pay = (TextView) v.findViewById(R.id.textView2);
        try{
            loginUser = UserLoggedIn.getUser();
            if(loginUser == null)
                userName = "";
            else
                userName = loginUser.getString("nickname");


        }
        catch (JSONException e) {
            Log.e("Exception", "JSONException occurred in ResultActivity1.java");
            e.printStackTrace();
        }
        //그룹 펼쳐짐 여부에 따라 아이콘 변경
        if (getGroup(groupPosition).getName() == userName)
            arrowIcon.setImageResource(R.drawable.check_mark);
        else
           arrowIcon.setImageResource(R.drawable.empty_background);

        //리스트 아이템의 내용 설정
        name.setText(getGroup(groupPosition).getName());
        pay.setText(String.valueOf(getGroup(groupPosition).getPay()));
        //
        return v;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v = convertView;
        Context context = parent.getContext();

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.childitem, parent, false);
        }

        TextView name = (TextView) v.findViewById(R.id.textView1);
        TextView pay = (TextView) v.findViewById(R.id.textView2);
        TextView number = (TextView) v.findViewById(R.id.textView3);


        name.setText(getChild(groupPosition, childPosition).getName());
        pay.setText(String.valueOf(getChild(groupPosition, childPosition).getPay()) + " X " + String.valueOf(getChild(groupPosition, childPosition).getNumber()));
        number.setText(String.valueOf(getChild(groupPosition, childPosition).getNumber()*getChild(groupPosition, childPosition).getPay()));



        return v;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    //리스트에 새로운 아이템을 추가
    public void addItem(int groupPosition, personal_item item) {
        itemlists.get(groupPosition).add(item);
    }
    public void addPerson(resultlist_item item) {
        personlists.add(item);
    }
    //리스트 아이템을 삭제
    //public void removeChild(int groupPosition, int childPosition) {
    //    childItems.get(groupPosition).remove(childPosition);
    //}
}