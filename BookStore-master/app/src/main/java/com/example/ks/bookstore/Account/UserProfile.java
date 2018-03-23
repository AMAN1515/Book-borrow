package com.example.ks.bookstore.Account;

import android.content.Context;


public class UserProfile {
    private String name;
    private String phone;
    private static UserProfile user=null;
    private static Address userAddress=null;

    UserProfile(String name, String phone){
        this.name=name;
        this.phone=phone;
    }

    public static UserProfile getUser(Context context) {
        if (user!=null)
            return user;
        if (UserData.isProfileSetUp(context))
            user=UserData.getUserProfile(context);
        if (UserData.iPermanentAddressProvided(context))
            userAddress=UserData.getUserAddress(context);
        return user;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public static Address getUserAddress() {
        return userAddress;
    }

}
