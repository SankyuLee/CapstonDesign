package edu.skku.capstone.justpay;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

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
    public RoomChartItem getItem(int position) {
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

        RoomChartItem chartItem = chartItemList.get(position);
        TextView itemNameText = convertView.findViewById(R.id.item_category1_text1);
        TextView itemCostText = convertView.findViewById(R.id.item_category1_text2);
        TextView itemCountText = convertView.findViewById(R.id.item_category1_text3);
        CheckBox checkBox = convertView.findViewById(R.id.item_check_box);
        EditText editCount = convertView.findViewById(R.id.item_count_check);
        ImageButton delButton = convertView.findViewById(R.id.delete_chart_item_btn);

        clearTextListener(editCount);
        if (chartItem.getItemResult() == 0) {
            editCount.setText("");
        } else {
            editCount.setText(chartItem.getItemResult().toString());
        }
        editCount.addTextChangedListener(chartItem.getTextWatcher());

        if (chartItem.getItemCount() != 0) {
            LinearLayout checkBoxContainer = convertView.findViewById(R.id.item_check_box_container);
            LinearLayout.LayoutParams checkBoxParams = (LinearLayout.LayoutParams) checkBoxContainer.getLayoutParams();
            checkBoxParams.weight = 0;
            checkBoxContainer.setLayoutParams(checkBoxParams);

            LinearLayout editContainer = convertView.findViewById(R.id.item_count_check_container);
            LinearLayout.LayoutParams containerParams = (LinearLayout.LayoutParams) editContainer.getLayoutParams();
            containerParams.weight = 2;
            editContainer.setLayoutParams(containerParams);
            itemCountText.setVisibility(View.VISIBLE);
            itemCountText.setText(chartItem.getItemCount().toString());
        } else {
            LinearLayout checkBoxContainer = convertView.findViewById(R.id.item_check_box_container);
            LinearLayout.LayoutParams checkBoxParams = (LinearLayout.LayoutParams) checkBoxContainer.getLayoutParams();
            checkBoxParams.weight = 2;
            checkBoxContainer.setLayoutParams(checkBoxParams);

            LinearLayout editContainer = convertView.findViewById(R.id.item_count_check_container);
            LinearLayout.LayoutParams containerParams = (LinearLayout.LayoutParams) editContainer.getLayoutParams();
            containerParams.weight = 0;
            editContainer.setLayoutParams(containerParams);
            itemCountText.setVisibility(View.INVISIBLE);
        }

        if (checkBox.isChecked()) {
            chartItem.setItemResult(1);
        } else if (editCount.getText().toString().getBytes().length > 0) {
            Integer result = Integer.parseInt(editCount.getText().toString());
            chartItem.setItemResult(result);
        } else {
            chartItem.setItemResult(0);
        }

        itemNameText.setText(chartItem.getItemName());
        itemCostText.setText(chartItem.getItemCost().toString());

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyDataSetChanged();
            }
        });

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

    public void addItem(RoomChartItem chartItem) {
        chartItemList.add(chartItem);
        notifyDataSetChanged();
    }

    public Pair<Boolean, HashMap<Integer, Integer>> getResult(Context context) {
        Boolean isError = false;
        HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();
        for (RoomChartItem item : chartItemList) {
            if (item.getItemCount() == 0) {
                result.put(item.getItemId(), item.getItemResult());
            } else if (item.getItemResult() > item.getItemCount()) {
                Toast.makeText(context,
                        item.getItemName() + context.getResources().getString(R.string.room_item_alert),
                        Toast.LENGTH_SHORT).show();
                isError = true;
                break;
            } else {
                result.put(item.getItemId(), item.getItemResult());
            }
        }

        return new Pair<>(isError, result);
    }

    private void clearTextListener(EditText editText) {
        int count = getCount();
        for (int i = 0; i < count; i++) {
            editText.removeTextChangedListener(getItem(i).getTextWatcher());
        }
    }
}
