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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class SearchTask extends AsyncTask<String,Void,ArrayList<Book>> {
    private static final String METHOD="POST";
    private SearchResponseListener listener=null;

    public SearchTask(SearchResponseListener listener) {
        this.listener = listener;
    }

    @Override
    protected ArrayList<Book> doInBackground(String... queries) {
        String response;
        try {
            URL searchUrl=new URL(ServerContacts.SEARCH_URL);
            HttpURLConnection connection= (HttpURLConnection) searchUrl.openConnection();

            connection.setRequestMethod(METHOD);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("content-Type","application/json");

            OutputStream os=connection.getOutputStream();
            BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));

            //noinspection ConstantConditions
            writer.write(Utility.getJsonObject(new String[]{"query"},new String[]{queries[0]}).toString());
            writer.close();
            os.close();
            InputStream in=connection.getInputStream();
            BufferedReader reader=new BufferedReader(new InputStreamReader(in,"iso-8859-1"));
            String temp;
            StringBuilder builder=new StringBuilder();
            while ((temp=reader.readLine())!=null){
                builder.append(temp);
            }
            reader.close();
            in.close();
            response=builder.toString();
            Log.e("debug",response);
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return Utility.getBooksFromJsonArray(response);
    }

    @Override
    protected void onPostExecute(ArrayList<Book> books) {
        if (listener==null)
            return;
        if (!isCancelled()&&books != null)
            listener.onSearchSuccess(books);
        else listener.onSearchFailed();
        listener=null;
    }

    @Override
    protected void onCancelled() {
        listener=null;
    }

    public void removeRefs() {
        listener=null;
    }

    public interface SearchResponseListener{
        void onSearchSuccess(ArrayList<Book> books);
        void onSearchFailed();
    }
}
