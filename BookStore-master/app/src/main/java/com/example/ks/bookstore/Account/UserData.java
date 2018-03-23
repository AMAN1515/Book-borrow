package com.example.ks.bookstore.Account;

import android.content.Context;
import android.content.SharedPreferences;


public class UserData {

    private static final String USER_DATA="user_data";
    private static final String isProfileSetup="profile_status";
    private static final String nameKey="name";
    private static final String phoneKey="phone";

    private static final String  streetNoKey= "streetNo";
    private static final String  landMarkKey= "landmark";
    private static final String  cityKey= "city";
    private static final String  zipCodeKey= "ZipCode";
    private static final String  stateKey= "state";
    private static final String  countryKey= "country";

    static boolean isProfileSetUp(Context context){
        SharedPreferences appData=context.getSharedPreferences(USER_DATA,Context.MODE_PRIVATE);
        return appData.getBoolean(isProfileSetup,false);
    }

    public static void setUpProfile(Context context, String name, String phone){
        SharedPreferences.Editor e=context.getSharedPreferences(USER_DATA,Context.MODE_PRIVATE).edit();
        e.putString(nameKey,name);
        e.putString(phoneKey,phone);
        e.putBoolean(isProfileSetup,true);
        e.apply();
    }

    public static UserProfile getUserProfile(Context context) {
        SharedPreferences userData=context.getSharedPreferences(USER_DATA,Context.MODE_PRIVATE);
        String name= userData.getString(nameKey,null);
        String phone=userData.getString(phoneKey,null);
        return new UserProfile(name,phone);
    }



    static boolean iPermanentAddressProvided(Context context){
        SharedPreferences userData=context.getSharedPreferences(USER_DATA,Context.MODE_PRIVATE);
        return userData.getString(cityKey,null)!=null;
    }

    public static void updateUserAddress(Context context,String  streetNo,String landMark, String zipCode,String city,String state,String country){
        SharedPreferences.Editor e=context.getSharedPreferences(USER_DATA,Context.MODE_PRIVATE).edit();
        e.putString(streetNoKey,streetNo);
        e.putString(landMarkKey,landMark);
        e.putString(zipCodeKey,zipCode);
        e.putString(cityKey,city);
        e.putString(stateKey,state);
        e.putString(countryKey,country);
        e.apply();
    }

    static Address getUserAddress(Context context){
        SharedPreferences userData=context.getSharedPreferences(USER_DATA,Context.MODE_PRIVATE);
        String street=userData.getString(streetNoKey,null);
        String landmark=userData.getString(landMarkKey,null);
        String city=userData.getString(cityKey,null);
        String zipCode=userData.getString(zipCodeKey,null);
        String state=userData.getString(stateKey,null);
        String country=userData.getString(countryKey,null);
        return new Address(street,landmark,city,zipCode,state,country);
    }
}
