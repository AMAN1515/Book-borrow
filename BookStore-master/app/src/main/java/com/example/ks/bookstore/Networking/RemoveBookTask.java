package com.example.ks.bookstore.Networking;

import android.os.AsyncTask;
import android.util.Log;

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

/**
 * Created by pankaj kumar on 23-02-2018.
 */

public class RemoveBookTask extends AsyncTask<String,Void,Void>{
    @Override
    protected Void doInBackground(String... strings) {
        try {
            URL deleteUrl =new URL(ServerContacts.REMOVE_BOOK);
            HttpURLConnection connection= (HttpURLConnection) deleteUrl.openConnection();


            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            OutputStream os=connection.getOutputStream();
            BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));

            String keys[]={"id","userId"};
            String values[]={strings[0],strings[1]};
            writer.write(Utility.getJsonObject(keys,values).toString());
            writer.close();
            os.close();

            InputStream in = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in,"iso-8859-1");
            BufferedReader response=new BufferedReader(inputStreamReader);

            StringBuilder data= new StringBuilder();
            String temp;
            while ((temp=response.readLine())!=null)
                data.append(temp);
            inputStreamReader.close();
            in.close();

            Log.e("debug",data.toString());
        }  catch (IOException ignored) {
        }
        return null;
    }
}
