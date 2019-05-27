package edu.skku.capstone.justpay;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Event {

    private Integer eventId;                            // 이벤트 아이디
    private String eventTitle;                          // 제목
    private Member eventManager;                        // 관리인
    private Member eventPayer;                          // 지불자
    private ArrayList<File> receiptList;                // 등록된 영수증 목록
    private ArrayList<RoomChartItem> chartItems;        // 등록된 항목 목록
    private HashMap<Integer, Integer> chartResult;      // 사용자의 항목별 참여 결과 <항목 이름, 수량>
    private int eventStatus;                            // 상태

    // 이벤트의 상태를 나타내는 상수
    public static final int MAKE_LIST = 0;
    public static final int PERSONAL_CHECK = 1;
    public static final int CONFIRM_RESULT = 2;

    public Event(Integer eventId, String eventTitle) {
        this.eventManager = null;
        this.eventPayer = null;
        this.receiptList = new ArrayList<>();
        this.chartItems = new ArrayList<>();
        this.chartResult = new HashMap<>();
        this.eventStatus = MAKE_LIST;
    }

    public Event(Integer eventId, String eventTitle, Member eventManager, Member eventPayer,
                 ArrayList<File> receiptList, ArrayList<RoomChartItem> chartItems,
                 HashMap<Integer, Integer> chartResult, int eventStatus) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventManager = eventManager;
        this.eventPayer = eventPayer;
        this.receiptList = receiptList;
        this.chartItems = chartItems;
        this.chartResult = chartResult;
        this.eventStatus = eventStatus;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public Member getEventManager() {
        return eventManager;
    }

    public void setEventManager(Member eventManager) {
        this.eventManager = eventManager;
    }

    public ArrayList<File> getReceiptList() {
        return receiptList;
    }

    public void setReceiptList(ArrayList<File> receiptList) {
        this.receiptList = receiptList;
    }

    public ArrayList<RoomChartItem> getChartItems() {
        return chartItems;
    }

    public void setChartItems(ArrayList<RoomChartItem> chartItems) {
        this.chartItems = chartItems;
    }

    public int getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(int eventStatus) {
        this.eventStatus = eventStatus;
    }

    public HashMap<Integer, Integer> getChartResult() {
        return chartResult;
    }

    public void setChartResult(HashMap<Integer, Integer> chartResult) {
        this.chartResult = chartResult;
    }

    public Member getEventPayer() {
        return eventPayer;
    }

    public void setEventPayer(Member eventPayer) {
        this.eventPayer = eventPayer;
    }
}
