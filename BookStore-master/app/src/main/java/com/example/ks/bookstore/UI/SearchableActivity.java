package com.example.ks.bookstore.UI;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.ks.bookstore.Book;
import com.example.ks.bookstore.Networking.SearchTask;
import com.example.ks.bookstore.R;
import com.example.ks.bookstore.RecylerAdapters.BookAdapter;
import com.example.ks.bookstore.Utility.Constants;

import java.util.ArrayList;

public class SearchableActivity extends AppCompatActivity {
    private RecyclerView searchResultView;
    private GridLayoutManager gridLayoutManager;
    private BookAdapter searchAdapter;
    private ProgressDialog searchProgress;
    private SearchTask searchTask;
    private static ArrayList<Book>books=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        searchResultView=findViewById(R.id.searchResultView);
        gridLayoutManager=new GridLayoutManager(this,1);
        searchResultView.setLayoutManager(gridLayoutManager);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (books==null||books.size()==0)
                doMySearch(query);
        }
    }

    private void doMySearch(String query) {
        setRequestedOrientation(getResources().getConfiguration().orientation);
        searchProgress=new ProgressDialog(this);
        searchProgress.setIndeterminate(true);
        searchProgress.setCancelable(true);
        searchProgress.setMessage("SEARCHING "+query);
        searchProgress.show();
        searchProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (books==null)
                    finish();
            }
        });
        searchTask=new SearchTask(new SearchTask.SearchResponseListener() {
            @Override
            public void onSearchSuccess(ArrayList<Book> result) {
                searchProgress.cancel();
                books=result;
                searchAdapter=new BookAdapter(result, Constants.SEARCH_RESULTS);
                searchResultView.setAdapter(searchAdapter);
                searchTask.removeRefs();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }

            @Override
            public void onSearchFailed() {
                searchProgress.cancel();
                searchTask.removeRefs();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
        });
        searchTask.execute(query);
    }

    @Override
    public void finish() {
        stopSearchTask();
        super.finish();
    }

    @Override
    public void onBackPressed() {
        if (books!=null)
            books=null;
        stopSearchTask();
        super.onBackPressed();
    }

    private void stopSearchTask(){
        if (searchTask!=null&&searchTask.getStatus()!= AsyncTask.Status.FINISHED){
            searchTask.cancel(true);
            searchTask.removeRefs();
        }
    }

    public static ArrayList<Book> getBooks() {
        return books;
    }
}
