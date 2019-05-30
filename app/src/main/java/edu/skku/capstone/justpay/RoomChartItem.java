package edu.skku.capstone.justpay;

import android.text.Editable;
import android.text.TextWatcher;

public class RoomChartItem {
    private Integer itemId;
    private String itemName;
    private Integer itemCost;
    private Integer itemCount;
    private Integer itemResult;
    private TextWatcher textWatcher;

    public RoomChartItem(Integer itemId, String itemName, Integer itemCost, Integer itemCount) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemCost = itemCost;
        this.itemCount = itemCount;
        this.itemResult = 0;
        this.textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    setItemResult(Integer.parseInt(s.toString()));
                } else {
                    setItemResult(0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    setItemResult(Integer.parseInt(s.toString()));
                } else {
                    setItemResult(0);
                }
            }
        };
    }

    public void setItemName(String name) {
        itemName = name;
    }

    public void setItemCost(Integer cost) {
        itemCost = cost;
    }

    public void setItemCount(Integer count) {
        itemCount = count;
    }

    public String getItemName() {
        return this.itemName;
    }

    public Integer getItemCost() {
        return this.itemCost;
    }

    public Integer getItemCount() {
        return this.itemCount;
    }

    public Integer getItemResult() {
        return itemResult;
    }

    public void setItemResult(Integer itemResult) {
        this.itemResult = itemResult;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public TextWatcher getTextWatcher() {
        return textWatcher;
    }

    public void setTextWatcher(TextWatcher textWatcher) {
        this.textWatcher = textWatcher;
    }
}
