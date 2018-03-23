package com.example.ks.bookstore.Networking;

import android.os.AsyncTask;
import android.util.Log;

import com.example.ks.bookstore.Book;
import com.example.ks.bookstore.Utility.ServerContacts;
import com.example.ks.bookstore.Utility.Utility;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class AddBookTask extends AsyncTask<String ,Void,String> {
    private OnBookAddListener onBookAddListener=null;
    private Book book;
    public AddBookTask(OnBookAddListener listener, Book b){
        onBookAddListener=listener;
        book=b;
    }

    @Override
    protected String doInBackground(String... params) {
        String serverId="";
        try {
            URL sendOTPURL=new URL(ServerContacts.ADD_BOOK_URL);
            HttpURLConnection sendOTPConnection= (HttpURLConnection) sendOTPURL.openConnection();


            sendOTPConnection.setDoOutput(true);
            sendOTPConnection.setDoInput(true);
            sendOTPConnection.setRequestMethod("POST");

            sendOTPConnection.setRequestProperty("Content-Type", "application/json");

            OutputStream os=sendOTPConnection.getOutputStream();
            BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));

            String keys[]={"name","author","tags","userid"};
            String values[]={book.getName(),book.getAuthor(),book.getTag(),params[0]};
            //noinspection ConstantConditions
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

            serverId=data.toString();
            Log.e("debug",data.toString());
        }  catch (IOException e) {
            return serverId;
        }
        return serverId;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        onBookAddListener=null;
        book=null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (onBookAddListener==null)
            return;
        if (s != null){
            book.setServerId(s);
            onBookAddListener.onBookAdded(book);
        }
        else onBookAddListener.onBookAddFailed();
        onBookAddListener=null;
    }

    @Override
    protected void onCancelled(String s) {
        onBookAddListener=null;
    }

    public interface OnBookAddListener{
        void onBookAdded(Book b);
        void onBookAddFailed();
    }
}