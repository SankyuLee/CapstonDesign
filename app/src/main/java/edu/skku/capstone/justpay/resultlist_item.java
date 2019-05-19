package edu.skku.capstone.justpay;

import java.util.ArrayList;

public class resultlist_item {
    private String nickname;
    private int pay;
    private ArrayList<personal_item> itemList = new ArrayList<>();

    public resultlist_item(String name, int pay)
    {
        this.nickname = name;       // Must refer to instance variables that have
        this.pay = pay;         // the same name as constructor parameters

    }
    public String getName() {
           return nickname;
    }
    public int getPay() {
        return pay;
    }
    public ArrayList<personal_item> getItem() {
        return itemList;
    }


    public ArrayList<personal_item> addItem(String name, int pay, int number) {
        itemList.add(new personal_item(name, pay ,number));
        return itemList;
    }
}