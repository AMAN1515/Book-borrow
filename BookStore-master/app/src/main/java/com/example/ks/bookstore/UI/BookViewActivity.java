package com.example.ks.bookstore.UI;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ks.bookstore.Account.AppData;
import com.example.ks.bookstore.Book;
import com.example.ks.bookstore.MainActivity;
import com.example.ks.bookstore.Networking.AddToWishListTask;
import com.example.ks.bookstore.R;
import com.example.ks.bookstore.Utility.Constants;

import static com.example.ks.bookstore.Utility.Constants.MY_BOOKS;
import static com.example.ks.bookstore.Utility.Constants.NO_BOOK;
import static com.example.ks.bookstore.Utility.Constants.ONLINE_BOOKS;
import static com.example.ks.bookstore.Utility.Constants.RESOLVE_BOOK_TYPE;
import static com.example.ks.bookstore.Utility.Constants.WISH_LIST_BOOKS;
import static com.example.ks.bookstore.Utility.Constants.SEARCH_RESULTS;

public class BookViewActivity extends AppCompatActivity {
    private TextView nameView,authorView,tagView;
    private int index,resolve;
    private Book b=null;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_view);

        nameView =findViewById(R.id.bookNameView);
        authorView =findViewById(R.id.authorNameView);
        tagView =findViewById(R.id.tagView);

        Button bookUserProfileView=findViewById(R.id.viewBookUserProfile);
        Button saveBookView=findViewById(R.id.saveBook);

        FloatingActionButton fab=findViewById(R.id.updateButton);
       // fab.setImageResource(R.drawable.edit);

        resolve=getIntent().getIntExtra(RESOLVE_BOOK_TYPE,MY_BOOKS);
        index =getIntent().getIntExtra("index",NO_BOOK);

        if (index !=NO_BOOK){
            if (resolve==MY_BOOKS){
                b=BookListActivity.getMyBook(index);
                saveBookView.setVisibility(View.GONE);
                bookUserProfileView.setVisibility(View.GONE);
            }
            else if (resolve== WISH_LIST_BOOKS){
               b=BookListActivity.getMyBook(index);
                saveBookView.setVisibility(View.GONE);
            }
            else if (resolve== ONLINE_BOOKS)
                b=MainActivity.getBooks().get(index);
            else if (resolve==SEARCH_RESULTS)
                b=SearchableActivity.getBooks().get(index);
        }

        if (resolve!=MY_BOOKS)
            fab.setVisibility(View.GONE);
        if (b==null)
            finish();
        else setBookContent(b);
    }


    private void  setBookContent(Book b){
        nameView.setText(b.getName());
        authorView.setText(b.getAuthor());
        tagView.setText(b.getTag());
    }

    public void saveBook(View view) {
        if (resolve==MY_BOOKS)
            Toast.makeText(getApplicationContext(),"IT'S IS IN YOUR BOOKS",Toast.LENGTH_LONG).show();
        else if (resolve== ONLINE_BOOKS){
            AddToWishListTask saveBookTask=new AddToWishListTask(new AddToWishListTask.OnAddWishListListener() {
                @Override
                public void onAddedToWishList() {
                    Toast.makeText(BookViewActivity.this,"Saved",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onWishListFailed() {

                }
            });
            saveBookTask.execute(b.getServerId(), AppData.getServerId(this));
        }
    }

    public void viewBookUserProfile(View view) {
        Intent profileView=new Intent(this,ProfileActivity.class);
        profileView.putExtra("WHOSE", Constants.OTHER_PROFILE);
        profileView.putExtra("id", b.getUserId() );
        startActivity(profileView);
    }

    public void launchUpdateActivity(View view) {
        if (resolve==MY_BOOKS){
            Intent updateIntent=new Intent(this,BookActivity.class);
            updateIntent.putExtra("index", index);
            startActivity(updateIntent);
        }
    }
}
