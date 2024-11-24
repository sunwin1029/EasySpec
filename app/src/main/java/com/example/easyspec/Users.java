package com.example.easyspec;

public class Users {
    private String email;
    private String university;
    private String laptop;
    private String tablet;
    private String phone;

    // 기본 생성자 필요
    public Users() {}

    public Users(String email, String university, String laptop, String tablet, String phone) {
        this.email = email;
        this.university = university;
        this.laptop = laptop;
        this.tablet = tablet;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public String getUniversity() {
        return university;
    }

    public String getLaptop() {
        return laptop;
    }

    public String getTablet() {
        return tablet;
    }

    public String getPhone() {
        return phone;
    }
}

