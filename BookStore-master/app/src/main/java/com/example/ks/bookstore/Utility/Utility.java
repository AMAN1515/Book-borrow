package com.example.ks.bookstore.Utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.ks.bookstore.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Utility {

    public static JSONObject getJsonObject(String[] keys,String []values){
        if (keys.length!=values.length)
            return null;
        int size=keys.length;
        JSONObject result=new JSONObject();
        for (int i=0;i<size;i++){
            try {
                result.put(keys[i],values[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static ArrayList<Book> getBooksFromJsonArray(String jsonArrString){
        ArrayList<Book>result;
        int l;
        try {
            JSONArray array=new JSONArray(jsonArrString);
            result=new ArrayList<>();
            l=array.length();
            JSONObject t;
            for (int i=0;i<l;i++){
                t=(JSONObject) array.get(i);
                result.add(getBookFromJson(t));
            }
        } catch (JSONException ignored) {
            return null;
        }
        return result;
    }

    public static Book getBookFromJson(JSONObject jsonObject){
        String name,author,tag,serverId,userId;
        try {
            name=jsonObject.getString("name");
            author=jsonObject.getString("author");
            tag=jsonObject.getJSONArray("tags").getString(0);
            serverId=jsonObject.getString("_id");
            userId=jsonObject.getString("userid");
        } catch (JSONException e) {
            return null;
        }
        return new Book(-1,name,author,tag,serverId, userId);
    }

    public static boolean isInternetAvailable(Context c){
        ConnectivityManager connectivityManager= (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo= null;
        if (connectivityManager != null) networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo!=null&&networkInfo.isConnected();
    }
}
