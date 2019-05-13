package com.pro.android.justyle;

public class UserInformation{
    private String name;
    private String address;


    public UserInformation(String name, String address){
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
