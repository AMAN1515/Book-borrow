package com.example.ks.bookstore.UI;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ks.bookstore.Account.AppData;
import com.example.ks.bookstore.Book;
import com.example.ks.bookstore.Networking.AddBookTask;
import com.example.ks.bookstore.Networking.UpdateBookTask;
import com.example.ks.bookstore.R;
import com.example.ks.bookstore.Utility.Constants;
import com.example.ks.bookstore.Utility.Utility;

public class BookActivity extends AppCompatActivity {
    private EditText nameView, authorView, tagView;
    private ProgressDialog progressDialog;
    private static final boolean DEBUG=true;
    private static final String TAG="addBook";
    private Book book=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        nameView =findViewById(R.id.bookName);
        authorView =findViewById(R.id.authorName);
        tagView =findViewById(R.id.tag);
        progressDialog=new ProgressDialog(this);
        if (DEBUG) Log.e(TAG,progressDialog.toString());

        Button update=findViewById(R.id.updateBook);
        Button addBook=findViewById(R.id.addBook);
        book=null;
        int  index=getIntent().getIntExtra("index", Constants.NO_BOOK);
        if (index!=Constants.NO_BOOK){
            addBook.setVisibility(View.GONE);
            book=BookListActivity.getMyBook(index);
            nameView.setText(book.getName());
            authorView.setText(book.getAuthor());
            tagView.setText(book.getTag());
        }else update.setVisibility(View.GONE);
    }

    public void addBook(View view) {
        addBook();
    }

    private void addBook(){
        setRequestedOrientation(getResources().getConfiguration().orientation);
        if (!isValidInputs())
            Toast.makeText(getApplicationContext(),"Please Provide All Fields",Toast.LENGTH_LONG).show();
        else{
            if (!Utility.isInternetAvailable(this)){
                Toast.makeText(getApplicationContext(),"Please Enable Internet",Toast.LENGTH_LONG).show();
                return;
            }

            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("ADDING BOOK...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            AddBookTask addBookTask = new AddBookTask(new AddBookTask.OnBookAddListener() {
                @Override
                public void onBookAdded(Book b) {
                    progressDialog.cancel();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    Toast.makeText(getApplication(), "Book ADDED", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onBookAddFailed() {
                    progressDialog.cancel();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    Toast.makeText(getApplication(), "Internet Connection Lost", Toast.LENGTH_LONG).show();
                }
            }, new Book(nameView.getText().toString(),
                    authorView.getText().toString(),tagView.getText().toString(), AppData.getServerId(BookActivity.this)));
            addBookTask.execute(AppData.getServerId(BookActivity.this));
        }
    }


    public void updateBook(View view) {
        if (isValidInputs()){
            String name,author,tag;
            name=nameView.getText().toString();
            author=authorView.getText().toString();
            tag=tagView.getText().toString();
            if (name.equals(book.getName())&&author.equals(book.getAuthor())&&tag.equals(book.getTag()))
                Toast.makeText(getApplicationContext(),"NO CHANGES MADE",Toast.LENGTH_LONG).show();
            else {
                setRequestedOrientation(getResources().getConfiguration().orientation);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("UPDATING BOOK...");
                progressDialog.show();
                progressDialog.setCancelable(false);
                book.setName(name);
                book.setAuthor(author);
                book.setTag(tag);
                UpdateBookTask updateBookTask=new UpdateBookTask(new UpdateBookTask.BookUpdateListener() {
                    @Override
                    public void onUpdate() {
                        progressDialog.cancel();
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    }

                    @Override
                    public void onUpdateFailed() {
                        progressDialog.cancel();
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                        Toast.makeText(getApplicationContext(),"UPDATE FAILED",Toast.LENGTH_LONG).show();
                    }
                }, book);
                updateBookTask.execute();
            }
        }
    }

    private boolean isValidInputs(){
        String name= nameView.getText().toString();
        String author= authorView.getText().toString();
        String tag= tagView.getText().toString();
        return !(TextUtils.isEmpty(name) || TextUtils.isEmpty(author) || TextUtils.isEmpty(tag));
    }
}
