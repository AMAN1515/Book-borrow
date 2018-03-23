package com.example.ks.bookstore.Account;


import android.content.Context;
import android.content.SharedPreferences;

public class AppData {
    private static final String APP_DATA="APP_DATA";

    private static final String isLogIn ="logInStatus";
    private static final String emailKey="username";
    private static final String passwordKey="password";
    private static final String serverIDKey="serverId";
    private static String email=null;
    private static String password=null;
    private static String serverId=null;


    public static boolean isLogIn(Context context){
        SharedPreferences appPrefs=context.getSharedPreferences(APP_DATA,Context.MODE_PRIVATE);
        return appPrefs.getBoolean(isLogIn,false);
    }

    static String getPassword(Context context){
        if (password==null)
            initialiseCredentials(context);
        return password;
    }

    static String getEmail(Context context){
        if (email==null)
            initialiseCredentials(context);
        return email;
    }

    public static String getServerId(Context context) {
        if (serverId==null)
            initialiseCredentials(context);
        return serverId;
    }

    private static void initialiseCredentials(Context context){
        SharedPreferences appPrefs=context.getSharedPreferences(APP_DATA,Context.MODE_PRIVATE);
        email=appPrefs.getString(emailKey,null);
        password=appPrefs.getString(passwordKey,null);
        serverId=appPrefs.getString(serverIDKey,null);
    }

    public static void logIn(Context c, String email, String password, String serverId){
        SharedPreferences.Editor e=c.getSharedPreferences(APP_DATA,Context.MODE_PRIVATE).edit();
        e.putString(emailKey,email);
        e.putString(passwordKey,password);
        e.putString(serverIDKey,serverId);
        e.putBoolean(isLogIn,true);
        e.apply();
    }

    public static void logOut(Context context){
        SharedPreferences.Editor e=context.getSharedPreferences(APP_DATA,Context.MODE_PRIVATE).edit();
        e.putString(emailKey,null);
        e.putString(passwordKey,null);
        e.putBoolean(isLogIn,false);
        e.apply();
    }

}
