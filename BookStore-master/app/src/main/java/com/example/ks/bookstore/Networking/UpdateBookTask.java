package com.example.ks.bookstore.Networking;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

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

/**
 * Created by pankaj kumar on 12-02-2018.
 */

public class UpdateBookTask extends AsyncTask<Void,Void,String>{

    private BookUpdateListener bookUpdateListener=null;
    private Book updated=null;
    public UpdateBookTask(@NonNull BookUpdateListener listener, @NonNull Book update){
        bookUpdateListener=listener;
        this.updated=update;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String result="";
        try {
            URL sendOTPURL=new URL(ServerContacts.UPDATE_BOOK_URL);
            HttpURLConnection sendOTPConnection= (HttpURLConnection) sendOTPURL.openConnection();


            sendOTPConnection.setDoOutput(true);
            sendOTPConnection.setDoInput(true);
            sendOTPConnection.setRequestMethod("POST");

            sendOTPConnection.setRequestProperty("Content-Type", "application/json");

            OutputStream os=sendOTPConnection.getOutputStream();
            BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));

            String keys[]={"name","author","tag","serverId"};
            String values[]={updated.getName(),updated.getAuthor(),updated.getTag(),updated.getServerId()};
            writer.write(Utility.getJsonObject(keys,values).toString());
            writer.close();
            os.close();

            InputStream in = sendOTPConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in,"iso-8859-1");
            BufferedReader OTPSendResponse=new BufferedReader(inputStreamReader);

            StringBuilder data= new StringBuilder();
            String temp;
            while ((temp=OTPSendResponse.readLine())!=null)
                data.append(temp);
            inputStreamReader.close();
            in.close();

            result=data.toString();
            Log.e("debug",data.toString());
        }  catch (IOException e) {
            return result;
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        if (isCancelled()){
            bookUpdateListener=null;
            updated=null;
            return;
        }
        if (s.equals(Constants.RESULT_SUCCESS))
            bookUpdateListener.onUpdate();
        else bookUpdateListener.onUpdateFailed();
    }

    @Override
    protected void onCancelled() {
        bookUpdateListener=null;
    }

    public interface BookUpdateListener{
        void onUpdate();
        void onUpdateFailed();
    }
}
