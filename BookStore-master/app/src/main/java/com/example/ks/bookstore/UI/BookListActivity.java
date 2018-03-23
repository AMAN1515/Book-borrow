package com.example.ks.bookstore.UI;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.ks.bookstore.Account.AppData;
import com.example.ks.bookstore.Book;
import com.example.ks.bookstore.Networking.GetMyBooksTask;
import com.example.ks.bookstore.Networking.GetMyWishList;
import com.example.ks.bookstore.R;
import com.example.ks.bookstore.RecylerAdapters.BookAdapter;
import com.example.ks.bookstore.Utility.Constants;

import java.util.ArrayList;
import java.util.BitSet;

public class BookListActivity extends AppCompatActivity {
    private RecyclerView bookListRecycler;
    private GridLayoutManager gridLayoutManager;
    private BookAdapter bookAdapter;
    int type;
    private static ArrayList<Book>books=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        bookListRecycler =findViewById(R.id.bookListRecycler);
        final int resolve;
        resolve=getIntent().getIntExtra("resolve",-1);
        if (resolve==Constants.MY_BOOKS)
            type=Constants.MY_BOOKS;
        else if (resolve==Constants.WISH_LIST_BOOKS)
            type=Constants.WISH_LIST_BOOKS;

        books.clear();
        bookAdapter=new BookAdapter(books,type);
        gridLayoutManager =new GridLayoutManager(this,1);
        bookListRecycler.setLayoutManager(gridLayoutManager);
        bookListRecycler.setAdapter(bookAdapter);
    }


    public static Book getMyBook(int pos) {
        return books.get(pos);
    }
}
