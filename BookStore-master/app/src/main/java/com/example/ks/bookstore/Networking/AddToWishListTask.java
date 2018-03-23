package com.example.ks.bookstore.Networking;

import android.os.AsyncTask;

import com.example.ks.bookstore.Utility.Constants;
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

public class AddToWishListTask extends AsyncTask<String,Void,Boolean> {
    private OnAddWishListListener saveListener=null;
    public AddToWishListTask(OnAddWishListListener listener){
        saveListener=listener;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        boolean result=false;
        String response=null;
        try {
            URL sendOTPURL=new URL(ServerContacts.ADD_TO_WISH_LIST_URL);
            HttpURLConnection sendOTPConnection= (HttpURLConnection) sendOTPURL.openConnection();


            sendOTPConnection.setDoOutput(true);
            sendOTPConnection.setDoInput(true);
            sendOTPConnection.setRequestMethod("POST");

            sendOTPConnection.setRequestProperty("Content-Type", "application/json");

            OutputStream os=sendOTPConnection.getOutputStream();
            BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));

            writer.write(Utility.getJsonObject(new String[]{"id","userId"}, new String[]{params[0],params[1]}).toString());
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

            response=data.toString();
            if (response.equals(Constants.RESULT_SUCCESS))
                result=true;
        }  catch (IOException e) {
            return false;
        }
        return result;
    }

    @Override
    protected void onCancelled() {
        saveListener=null;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (saveListener==null)
            return;
        if (aBoolean){
            saveListener.onAddedToWishList();
        }
        else saveListener.onWishListFailed();
        saveListener=null;
    }


    public interface OnAddWishListListener {
        void onAddedToWishList();
        void onWishListFailed();
    }
}
