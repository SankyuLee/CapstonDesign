package edu.skku.capstone.justpay;


public class RoomList_item {
    private String room_tag;
    private String room_name;

    public RoomList_item(String tag, String name){
        this.room_tag = tag;
        this.room_name = name;
    }

    public String getRoom_tag(){return room_tag;}
    public String getRoom_name(){return room_name;}
}
