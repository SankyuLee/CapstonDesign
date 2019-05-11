package edu.skku.capstone.justpay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RoomChartItemAdapter extends BaseAdapter {
    private ArrayList<RoomChartItem> chartItemList = new ArrayList<>();

    public RoomChartItemAdapter(ArrayList<RoomChartItem> chartItemList) {
        this.chartItemList = chartItemList;
    }

    @Override
    public int getCount() {
        return chartItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return chartItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_chart, parent, false);
        }

        TextView itemNameText = convertView.findViewById(R.id.item_category1_text1);
        TextView itemCostText = convertView.findViewById(R.id.item_category1_text2);
        TextView itemCountText = convertView.findViewById(R.id.item_category1_text3);

        RoomChartItem chartItem = chartItemList.get(position);

        itemNameText.setText(chartItem.getItemName());
        itemCostText.setText(chartItem.getItemCost().toString());
        itemCountText.setText(chartItem.getItemCount().toString());

        return convertView;
    }

    public void addItem(String name, Integer cost, Integer count) {
        RoomChartItem item = new RoomChartItem(name, cost, count);

        item.setItemName(name);
        item.setItemCost(cost);
        item.setItemCount(count);
    }
}
