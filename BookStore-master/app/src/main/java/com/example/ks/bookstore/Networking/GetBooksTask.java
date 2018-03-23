package com.example.ks.bookstore.Networking;

import android.os.AsyncTask;

import com.example.ks.bookstore.Book;
import com.example.ks.bookstore.Utility.ServerContacts;
import com.example.ks.bookstore.Utility.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class GetBooksTask extends AsyncTask<Void,Void,ArrayList<Book>> {
    private GetBooksListener getBooksListener=null;

    public GetBooksTask(GetBooksListener getBooksListener){
        this.getBooksListener=getBooksListener;
    }

    @Override
    protected ArrayList<Book> doInBackground(Void... voids) {
        ArrayList<Book>result;
        try {
            URL getBooksUrl=new URL(ServerContacts.GET_BOOKS_URL);
            HttpURLConnection connection= (HttpURLConnection) getBooksUrl.openConnection();

            connection.setDoInput(true);
            connection.setRequestMethod("GET");

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
            result=Utility.getBooksFromJsonArray(response);
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
            getBooksListener.onBooksNotReceived();
        else getBooksListener.onBooksReceived(books);
        getBooksListener=null;
    }

    @Override
    protected void onCancelled() {
        getBooksListener.onBooksNotReceived();
        getBooksListener=null;
    }

    public interface GetBooksListener{
        void onBooksReceived(ArrayList<Book>result);
        void onBooksNotReceived();
    }
}
