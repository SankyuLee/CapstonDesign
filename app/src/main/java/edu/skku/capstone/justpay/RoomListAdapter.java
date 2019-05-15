package edu.skku.capstone.justpay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RoomListAdapter extends BaseAdapter {
    ArrayList<RoomList_item> data = new ArrayList<>();
    Context context;

    public RoomListAdapter(Context context, ArrayList<RoomList_item> data){
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_room, null);
        }
        TextView rooom_name = (TextView)convertView.findViewById((R.id.room_name));
        TextView room_tag = (TextView)convertView.findViewById(R.id.room_tag);
        TextView room_state = (TextView)convertView.findViewById(R.id.room_state);

        rooom_name.setText(data.get(position).getRoom_name());
        room_tag.setText(data.get(position).getRoom_tag());

        return convertView;
    }
}
