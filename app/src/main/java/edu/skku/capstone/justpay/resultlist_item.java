package edu.skku.capstone.justpay;

public class resultlist_item {
    private String nickname;
    private int pay;

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

}
