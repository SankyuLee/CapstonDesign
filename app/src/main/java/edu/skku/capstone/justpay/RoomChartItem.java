package edu.skku.capstone.justpay;

public class RoomChartItem {
    private String itemName;
    private Integer itemCost;
    private Integer itemCount;

    public RoomChartItem(String itemName, Integer itemCost, Integer itemCount) {
        this.itemName = itemName;
        this.itemCost = itemCost;
        this.itemCount = itemCount;
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
}
