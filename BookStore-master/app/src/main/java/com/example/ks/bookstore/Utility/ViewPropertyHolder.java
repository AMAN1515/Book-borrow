package com.example.ks.bookstore.Utility;

import android.graphics.Typeface;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Gravity;

import static com.example.ks.bookstore.Utility.ViewPropertyHolder.ViewProperty.GRAVITY;
import static com.example.ks.bookstore.Utility.ViewPropertyHolder.ViewProperty.INVALID_PROPERTY;
import static com.example.ks.bookstore.Utility.ViewPropertyHolder.ViewProperty.INVALID_VALUE;
import static com.example.ks.bookstore.Utility.ViewPropertyHolder.ViewProperty.MAX_LINES;
import static com.example.ks.bookstore.Utility.ViewPropertyHolder.ViewProperty.TEXT_DISPLAY_UNIT;
import static com.example.ks.bookstore.Utility.ViewPropertyHolder.ViewProperty.TEXT_SIZE;
import static com.example.ks.bookstore.Utility.ViewPropertyHolder.ViewProperty.TYPE_FACE;

public class ViewPropertyHolder {
    private SparseIntArray propertiesHash=new SparseIntArray();

    public void addProperty(int viewProperty,int value){
        if (viewProperty>=GRAVITY&&viewProperty<=TEXT_DISPLAY_UNIT){
            propertiesHash.put(viewProperty,value);
        }else
            throw new RuntimeException("not view valid property");
    }

    int getProperty(int viewProperty){
        switch (viewProperty){
            case TEXT_SIZE:
                return propertiesHash.get(TEXT_SIZE,16);

            case TYPE_FACE:
                return propertiesHash.get(TYPE_FACE, Typeface.NORMAL);

            case GRAVITY:
                return propertiesHash.get(GRAVITY, Gravity.CENTER);

            case TEXT_DISPLAY_UNIT:
                return propertiesHash.get(TEXT_DISPLAY_UNIT,TypedValue.COMPLEX_UNIT_SP);

            case MAX_LINES:
                return propertiesHash.get(MAX_LINES,INVALID_VALUE);

            default:
                    return INVALID_PROPERTY;
        }
    }

    public static class ViewProperty{
        public static final int GRAVITY=0;
        public static final int TEXT_SIZE =1;
        public static final int TYPE_FACE=2;
        public static final int MAX_LINES=3;
        public static final int TEXT_DISPLAY_UNIT=4;

        public static final int INVALID_PROPERTY=-1;
        public static final int INVALID_VALUE=-2;
    }

}
