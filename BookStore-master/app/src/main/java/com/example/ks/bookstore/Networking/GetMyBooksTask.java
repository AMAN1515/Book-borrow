package com.example.ks.bookstore.Networking;

import android.os.AsyncTask;

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
import java.util.ArrayList;


public class GetMyBooksTask extends AsyncTask<String,Void,ArrayList<Book>>{

    private GetMyBooksListener myBooksListener=null;

    public GetMyBooksTask(GetMyBooksListener myBooksListener){
        this.myBooksListener=myBooksListener;
    }

    @Override
    protected ArrayList<Book> doInBackground(String... params) {
        ArrayList<Book>result;
        try {
            URL getBooksUrl=new URL(ServerContacts.GET_MY_BOOKS_URL);
            HttpURLConnection connection= (HttpURLConnection) getBooksUrl.openConnection();

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

            String response=data.toString();
            result= Utility.getBooksFromJsonArray(response);
        }  catch (IOException e) {
            return null;
        }
        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<Book> books) {
        if (isCancelled())
            return;
        if (books==null||books.size()==0)
            myBooksListener.onMyBooksNotReceived();
        else myBooksListener.onMyBooksReceived(books);
        myBooksListener=null;
    }

    @Override
    protected void onCancelled() {
        myBooksListener.onMyBooksNotReceived();
        myBooksListener=null;
    }

    public interface GetMyBooksListener{
        void onMyBooksReceived(ArrayList<Book>result);
        void onMyBooksNotReceived();
    }
}
