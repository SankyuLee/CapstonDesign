package edu.skku.capstone.justpay;

public class Member {

    private Integer memberId;
    private String memberName;
    private String memberEmail;
    private String memberPhoneNum;

    public Member() { }

    public Member(Integer id, String name, String email, String phoneNum) {
        this.memberId = id;
        this.memberName = name;
        this.memberEmail = email;
        this.memberPhoneNum = phoneNum;
    }

    public String getMemberPhoneNum() {
        return memberPhoneNum;
    }

    public void setMemberPhoneNum(String memberPhoneNum) {
        this.memberPhoneNum = memberPhoneNum;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }
}
