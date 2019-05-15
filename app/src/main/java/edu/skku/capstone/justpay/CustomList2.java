package edu.skku.capstone.justpay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomList2 extends BaseExpandableListAdapter {
    Context mContext;
    ArrayList<personal_item> itemlists;//personal_item의 형식을 들고옴
    ArrayList<ArrayList<items_person>> personlists;

    @Override
    public int getGroupCount() {
        return itemlists.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return personlists.get(groupPosition).size();
    }

    //리스트의 아이템 반환
    @Override
    public personal_item getGroup(int groupPosition) {

        return itemlists.get(groupPosition);
    }

    @Override
    public items_person getChild(int groupPosition, int childPosition) {
        return personlists.get(groupPosition).get(childPosition);
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
            v = inflater.inflate(R.layout.item_items, parent, false);
        }

        //View들은 반드시 아이템 레이아웃을 inflate한 뒤에 작성할 것
        //ImageView arrowIcon = (ImageView) v.findViewById(R.id.imageView);
        TextView name = (TextView) v.findViewById(R.id.textView1);
        TextView pay = (TextView) v.findViewById(R.id.textView2);
        TextView number = (TextView) v.findViewById(R.id.textView3);
        //그룹 펼쳐짐 여부에 따라 아이콘 변경
        //if (getGroup(groupPosition).getName() == "오승민")
        //    arrowIcon.setImageResource(R.drawable.green_circle_filled);
        //else
        //   arrowIcon.setImageResource(R.drawable.empty_background);

        //리스트 아이템의 내용 설정
        name.setText(getGroup(groupPosition).getName());
        pay.setText(String.valueOf(getGroup(groupPosition).getPay()));
        number.setText(String.valueOf(getGroup(groupPosition).getNumber()));
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
        pay.setText(String.valueOf(getChild(groupPosition, childPosition).getPay()));
        number.setText(String.valueOf(getChild(groupPosition, childPosition).getNumber()));

        return v;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    //리스트에 새로운 아이템을 추가
    public void addPerson(int groupPosition, items_person item) {
        personlists.get(groupPosition).add(item);
    }
    public void addItem(personal_item item) {
        itemlists.add(item);
    }
    //리스트 아이템을 삭제
    //public void removeChild(int groupPosition, int childPosition) {
    //    childItems.get(groupPosition).remove(childPosition);
    //}
}