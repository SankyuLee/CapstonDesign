package edu.skku.capstone.justpay;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RoomListAdapter extends BaseAdapter implements Filterable {
    ArrayList<RoomList_item> listViewItemList;
    ArrayList<RoomList_item> filteredItemList;
    Context context;
    Filter listFilter;

    public RoomListAdapter(Context context, ArrayList<RoomList_item> listViewItemList){
        this.context = context;
        this.listViewItemList = listViewItemList;
        this.filteredItemList = listViewItemList;
    }

    @Override
    public int getCount() {
        return filteredItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_room, null);
        }
        TextView room_name = (TextView)convertView.findViewById((R.id.room_name));
        TextView room_tag = (TextView)convertView.findViewById(R.id.room_tag);
        TextView room_state = (TextView)convertView.findViewById(R.id.room_state);

        room_name.setText(filteredItemList.get(position).getRoom_name());
        room_tag.setText(filteredItemList.get(position).getRoom_tag());
        room_state.setText("방금 수정됨");

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if(listFilter == null){
            listFilter = new ListFilter();
        }
        return listFilter;
    }

    private class ListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults() ;

            Log.d("test", "fiter");
            if (constraint == null || constraint.length() == 0) {
                results.values = listViewItemList ;
                results.count = listViewItemList.size() ;
            } else {
                Log.d("test", constraint.toString());
                ArrayList<RoomList_item> itemList = new ArrayList<RoomList_item>() ;
                for (RoomList_item item : listViewItemList) {

                    if (item.getRoom_name().toUpperCase().contains(constraint.toString().toUpperCase())) {
                        itemList.add(item) ;
                    }
                }

                results.values = itemList ;
                results.count = itemList.size() ;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredItemList = (ArrayList<RoomList_item>) results.values ;

            // notify
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
