package edu.skku.capstone.justpay;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class RoomChartItemAdapter extends BaseAdapter {
    private ArrayList<RoomChartItem> chartItemList;
    private ChartItemOnClickListener chartItemOnClickListener;

    public RoomChartItemAdapter(ArrayList<RoomChartItem> chartItemList, ChartItemOnClickListener chartItemOnClickListener) {
        this.chartItemList = chartItemList;
        this.chartItemOnClickListener = chartItemOnClickListener;
    }

    public interface ChartItemOnClickListener {
        void onChartItemDeleteBtnClick(int position);
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

        ImageButton delButton = convertView.findViewById(R.id.delete_chart_item_btn);
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chartItemOnClickListener.onChartItemDeleteBtnClick(pos);
            }
        });

        return convertView;
    }

    public void removeItem(int position) {
        chartItemList.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(int position, RoomChartItem chartItem) {
        chartItemList.add(position, chartItem);
        notifyDataSetChanged();
    }
}
