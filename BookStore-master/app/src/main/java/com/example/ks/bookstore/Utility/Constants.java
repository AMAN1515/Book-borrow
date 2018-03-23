package com.example.ks.bookstore.Utility;

import android.os.Environment;


public class Constants {
    private static final String MainDirectory=Environment.getExternalStorageDirectory().getAbsolutePath() + "/GET BOOK/";
    public static final String ImageDirectory= MainDirectory + "/Images/";

    public static final String RESULT_SUCCESS="success";
    public static final String RESULT_FAILURE="failed";


    public static final String RESOLVE_BOOK_TYPE="resolveBookType";

    public static final int ADD_BOOK=1;
    public static final int UPDATE_BOOK=2;
    public static final int VIEW_BOOK=3;

    public static final int MY_BOOKS =4;
    public static final int WISH_LIST_BOOKS =5;
    public static final int ONLINE_BOOKS =6;
    public static final int NO_CONTENT=7;
    public static final int NO_BOOK=-1;
    public static final int SEARCH_RESULTS =8;

    public static final int MIN_PING_INTERVAL =1000*60;//1 min
    public static final int MY_PROFILE = 1;
    public static final int OTHER_PROFILE = 2;
}
