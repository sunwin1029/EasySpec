package com.example.easyspec;

import java.util.HashMap;
import java.util.Map;

public class UsersOfTheProduct {
    private int itCollegeUsers;         // IT대학 사용자 수
    private int businessCollegeUsers;  // 경영대학 사용자 수
    private int scienceCollegeUsers;   // 자연과학대학 사용자 수
    private int economicsCollegeUsers; // 경제통상대학 사용자 수
    private int lawCollegeUsers;       // 법과대학 사용자 수
    private int socialScienceCollegeUsers; // 사회과학대학 사용자 수

    // 기본 생성자
    public UsersOfTheProduct() {
        this.itCollegeUsers = 0;
        this.businessCollegeUsers = 0;
        this.scienceCollegeUsers = 0;
        this.economicsCollegeUsers = 0;
        this.lawCollegeUsers = 0;
        this.socialScienceCollegeUsers = 0;
    }

    // Getter & Setter 메서드
    public int getItCollegeUsers() {
        return itCollegeUsers;
    }

    public void setItCollegeUsers(int itCollegeUsers) {
        this.itCollegeUsers = itCollegeUsers;
    }

    public int getBusinessCollegeUsers() {
        return businessCollegeUsers;
    }

    public void setBusinessCollegeUsers(int businessCollegeUsers) {
        this.businessCollegeUsers = businessCollegeUsers;
    }

    public int getScienceCollegeUsers() {
        return scienceCollegeUsers;
    }

    public void setScienceCollegeUsers(int scienceCollegeUsers) {
        this.scienceCollegeUsers = scienceCollegeUsers;
    }

    public int getEconomicsCollegeUsers() {
        return economicsCollegeUsers;
    }

    public void setEconomicsCollegeUsers(int economicsCollegeUsers) {
        this.economicsCollegeUsers = economicsCollegeUsers;
    }

    public int getLawCollegeUsers() {
        return lawCollegeUsers;
    }

    public void setLawCollegeUsers(int lawCollegeUsers) {
        this.lawCollegeUsers = lawCollegeUsers;
    }

    public int getSocialScienceCollegeUsers() {
        return socialScienceCollegeUsers;
    }

    public void setSocialScienceCollegeUsers(int socialScienceCollegeUsers) {
        this.socialScienceCollegeUsers = socialScienceCollegeUsers;
    }

    // 총 사용자 수 반환 메서드
    public int getTotalUsers() {
        return itCollegeUsers + businessCollegeUsers + scienceCollegeUsers
                + economicsCollegeUsers + lawCollegeUsers + socialScienceCollegeUsers;
    }
}