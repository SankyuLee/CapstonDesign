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
    private Integer eventStatus;

    public RoomChartItemAdapter(ArrayList<RoomChartItem> chartItemList, ChartItemOnClickListener chartItemOnClickListener) {
        this.chartItemList = chartItemList;
        this.chartItemOnClickListener = chartItemOnClickListener;
        this.eventStatus = Event.MAKE_LIST;
    }

    public Integer getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(Integer eventStatus) {
        this.eventStatus = eventStatus;
        notifyDataSetChanged();
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

        final RoomChartItem chartItem = chartItemList.get(position);
        TextView itemNameText = convertView.findViewById(R.id.item_category1_text1);
        TextView itemCostText = convertView.findViewById(R.id.item_category1_text2);
        TextView itemCountText = convertView.findViewById(R.id.item_category1_text3);
        EditText editCount = convertView.findViewById(R.id.item_count_check);
        TextView countText = convertView.findViewById(R.id.item_count_txt);
        ImageButton delButton = convertView.findViewById(R.id.delete_chart_item_btn);
        CheckBox checkBox = convertView.findViewById(R.id.item_check_box);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chartItem.getChecked()) {
                    chartItem.setChecked(false);
                } else {
                    chartItem.setChecked(true);
                }
                notifyDataSetChanged();
            }
        });

        clearTextListener(editCount);
        if (chartItem.getItemResult() == 0) {
            editCount.setText("");
        } else {
            editCount.setText(chartItem.getItemResult().toString());
        }
        editCount.addTextChangedListener(chartItem.getTextWatcher());
        checkBox.setChecked(chartItem.getChecked());

        if (checkBox.isChecked()) {
            chartItem.setItemResult(1);
        } else if (editCount.getText().toString().getBytes().length > 0) {
            Integer result = Integer.parseInt(editCount.getText().toString());
            chartItem.setItemResult(result);
        } else {
            chartItem.setItemResult(0);
        }

        switch (eventStatus) {
            case Event.MAKE_LIST:
                if (chartItem.getItemCount() != 0) {
                    LinearLayout itemChart = convertView.findViewById(R.id.item_chart);
                    itemChart.setBackgroundResource(android.R.color.transparent);

                    delButton.setVisibility(View.VISIBLE);
                    LinearLayout checkBoxContainer = convertView.findViewById(R.id.item_check_box_container);
                    checkBoxContainer.setVisibility(View.INVISIBLE);
                    LinearLayout editContainer = convertView.findViewById(R.id.item_count_check_container);
                    editContainer.setVisibility(View.INVISIBLE);
                    LinearLayout countTextContainer = convertView.findViewById(R.id.item_count_txt_container);
                    countTextContainer.setVisibility(View.INVISIBLE);
                    itemCountText.setVisibility(View.VISIBLE);
                    itemCountText.setText(chartItem.getItemCount().toString());
                } else {
                    LinearLayout itemChart = convertView.findViewById(R.id.item_chart);
                    itemChart.setBackgroundResource(android.R.color.transparent);

                    delButton.setVisibility(View.VISIBLE);
                    LinearLayout checkBoxContainer = convertView.findViewById(R.id.item_check_box_container);
                    checkBoxContainer.setVisibility(View.INVISIBLE);
                    LinearLayout editContainer = convertView.findViewById(R.id.item_count_check_container);
                    editContainer.setVisibility(View.INVISIBLE);
                    LinearLayout countTextContainer = convertView.findViewById(R.id.item_count_txt_container);
                    countTextContainer.setVisibility(View.INVISIBLE);
                    itemCountText.setVisibility(View.INVISIBLE);
                }
                break;
            case Event.PERSONAL_CHECK:
                if (chartItem.getItemCount() != 0) {
                    LinearLayout itemChart = convertView.findViewById(R.id.item_chart);
                    itemChart.setBackgroundResource(android.R.color.transparent);

                    delButton.setVisibility(View.INVISIBLE);
                    LinearLayout checkBoxContainer = convertView.findViewById(R.id.item_check_box_container);
                    checkBoxContainer.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams checkBoxParams = (LinearLayout.LayoutParams) checkBoxContainer.getLayoutParams();
                    checkBoxParams.weight = 0;
                    checkBoxContainer.setLayoutParams(checkBoxParams);
                    checkBox.setEnabled(true);

                    LinearLayout editContainer = convertView.findViewById(R.id.item_count_check_container);
                    editContainer.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams containerParams = (LinearLayout.LayoutParams) editContainer.getLayoutParams();
                    containerParams.weight = 2;
                    editContainer.setLayoutParams(containerParams);

                    LinearLayout countTextContainer = convertView.findViewById(R.id.item_count_txt_container);
                    countTextContainer.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams txtContainerParams = (LinearLayout.LayoutParams) countTextContainer.getLayoutParams();
                    txtContainerParams.weight = 0;
                    countTextContainer.setLayoutParams(txtContainerParams);

                    itemCountText.setVisibility(View.VISIBLE);
                    itemCountText.setText(chartItem.getItemCount().toString());
                } else {
                    LinearLayout itemChart = convertView.findViewById(R.id.item_chart);
                    itemChart.setBackgroundResource(android.R.color.transparent);

                    delButton.setVisibility(View.INVISIBLE);
                    LinearLayout checkBoxContainer = convertView.findViewById(R.id.item_check_box_container);
                    checkBoxContainer.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams checkBoxParams = (LinearLayout.LayoutParams) checkBoxContainer.getLayoutParams();
                    checkBoxParams.weight = 2;
                    checkBoxContainer.setLayoutParams(checkBoxParams);
                    checkBox.setEnabled(true);

                    LinearLayout editContainer = convertView.findViewById(R.id.item_count_check_container);
                    editContainer.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams containerParams = (LinearLayout.LayoutParams) editContainer.getLayoutParams();
                    containerParams.weight = 0;
                    editContainer.setLayoutParams(containerParams);

                    LinearLayout countTextContainer = convertView.findViewById(R.id.item_count_txt_container);
                    countTextContainer.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams txtContainerParams = (LinearLayout.LayoutParams) countTextContainer.getLayoutParams();
                    txtContainerParams.weight = 0;
                    countTextContainer.setLayoutParams(txtContainerParams);

                    itemCountText.setVisibility(View.INVISIBLE);
                }
                break;
            case Event.CONFIRM_RESULT:
                if (chartItem.getItemCount() != 0) {
                    delButton.setVisibility(View.INVISIBLE);
                    LinearLayout checkBoxContainer = convertView.findViewById(R.id.item_check_box_container);
                    checkBoxContainer.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams checkBoxParams = (LinearLayout.LayoutParams) checkBoxContainer.getLayoutParams();
                    checkBoxParams.weight = 0;
                    checkBoxContainer.setLayoutParams(checkBoxParams);
                    checkBox.setEnabled(false);

                    LinearLayout editContainer = convertView.findViewById(R.id.item_count_check_container);
                    editContainer.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams containerParams = (LinearLayout.LayoutParams) editContainer.getLayoutParams();
                    containerParams.weight = 0;
                    editContainer.setLayoutParams(containerParams);

                    LinearLayout countTextContainer = convertView.findViewById(R.id.item_count_txt_container);
                    countTextContainer.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams txtContainerParams = (LinearLayout.LayoutParams) countTextContainer.getLayoutParams();
                    txtContainerParams.weight = 2;
                    countTextContainer.setLayoutParams(txtContainerParams);
                    if (chartItem.getItemResult() != 0) {
                        countText.setText(chartItem.getItemResult().toString());
                        LinearLayout itemChart = convertView.findViewById(R.id.item_chart);
                        itemChart.setBackgroundResource(R.color.colorJustPaySub);
                    } else {
                        countText.setText("");
                        LinearLayout itemChart = convertView.findViewById(R.id.item_chart);
                        itemChart.setBackgroundResource(android.R.color.transparent);
                    }

                    itemCountText.setVisibility(View.VISIBLE);
                    itemCountText.setText(chartItem.getItemCount().toString());
                } else {
                    delButton.setVisibility(View.INVISIBLE);
                    LinearLayout checkBoxContainer = convertView.findViewById(R.id.item_check_box_container);
                    checkBoxContainer.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams checkBoxParams = (LinearLayout.LayoutParams) checkBoxContainer.getLayoutParams();
                    checkBoxParams.weight = 2;
                    checkBoxContainer.setLayoutParams(checkBoxParams);
                    checkBox.setEnabled(false);
                    if (checkBox.isChecked()) {
                        LinearLayout itemChart = convertView.findViewById(R.id.item_chart);
                        itemChart.setBackgroundResource(R.color.colorJustPaySub);
                    } else {
                        LinearLayout itemChart = convertView.findViewById(R.id.item_chart);
                        itemChart.setBackgroundResource(android.R.color.transparent);
                    }

                    LinearLayout editContainer = convertView.findViewById(R.id.item_count_check_container);
                    editContainer.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams containerParams = (LinearLayout.LayoutParams) editContainer.getLayoutParams();
                    containerParams.weight = 0;
                    editContainer.setLayoutParams(containerParams);

                    LinearLayout countTextContainer = convertView.findViewById(R.id.item_count_txt_container);
                    countTextContainer.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams txtContainerParams = (LinearLayout.LayoutParams) countTextContainer.getLayoutParams();
                    txtContainerParams.weight = 0;
                    countTextContainer.setLayoutParams(txtContainerParams);

                    itemCountText.setVisibility(View.INVISIBLE);
                }
                break;
        }

        itemNameText.setText(chartItem.getItemName());
        itemCostText.setText(chartItem.getItemCost().toString());

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

    public void removeAll() {
        chartItemList.clear();
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
