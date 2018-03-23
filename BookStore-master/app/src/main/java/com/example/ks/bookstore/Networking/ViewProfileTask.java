package com.example.ks.bookstore.Networking;

import android.os.AsyncTask;

import com.example.ks.bookstore.Book;
import com.example.ks.bookstore.Utility.Constants;
import com.example.ks.bookstore.Utility.ServerContacts;
import com.example.ks.bookstore.Utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by pankaj kumar on 23-02-2018.
 */

public class ViewProfileTask extends AsyncTask<String,String,String[]> {
    private ProfileListener profileListener;

    public ViewProfileTask(ProfileListener profileListener) {
        this.profileListener = profileListener;
    }

    @Override
    protected String[] doInBackground(String... params) {
        String result[] = null;
        try {
            URL getProfile=new URL(ServerContacts.PROFILE_URL);
            HttpURLConnection connection= (HttpURLConnection) getProfile.openConnection();

            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("content-type","Application/json");

            OutputStream os=connection.getOutputStream();
            BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
            writer.write(Utility.getJsonObject(new String[]{"_id"}, new String[]{params[0]}).toString());
            writer.close();
            os.close();

            InputStream in = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in,"iso-8859-1");
            BufferedReader reader=new BufferedReader(inputStreamReader);

            StringBuilder data= new StringBuilder();
            String temp;
            while ((temp=reader.readLine())!=null)
                data.append(temp);
            inputStreamReader.close();
            in.close();

            JSONObject response= new JSONObject(data.toString());
            if (response.getString("status").equals(Constants.RESULT_SUCCESS)){
                result=new String[2];
                result[0]=response.getString("name");
                result[1]=response.getString("phoneNumber");
            }
        }  catch (IOException e) {
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    protected void onPostExecute(String[] results) {
        if (isCancelled())
            return;
        if (results==null)
            profileListener.profileNotReceived();
        else profileListener.profileReceived(results[0],results[1]);;
        profileListener=null;
    }

    @Override
    protected void onCancelled() {
        profileListener.profileNotReceived();
        profileListener=null;
    }

    public interface ProfileListener{
        void profileReceived(String result, String s);
        void profileNotReceived();
    }
}
