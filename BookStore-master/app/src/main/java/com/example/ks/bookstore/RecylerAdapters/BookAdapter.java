package com.example.ks.bookstore.RecylerAdapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ks.bookstore.Account.AppData;
import com.example.ks.bookstore.Book;
import com.example.ks.bookstore.MainActivity;
import com.example.ks.bookstore.Networking.DeleteBookTask;
import com.example.ks.bookstore.Networking.GetBooksTask;
import com.example.ks.bookstore.Networking.GetMyBooksTask;
import com.example.ks.bookstore.Networking.GetMyWishList;
import com.example.ks.bookstore.Networking.RemoveBookTask;
import com.example.ks.bookstore.R;
import com.example.ks.bookstore.UI.BookViewActivity;
import com.example.ks.bookstore.Utility.Constants;
import com.example.ks.bookstore.Utility.Utility;

import java.util.ArrayList;

import static com.example.ks.bookstore.Utility.Constants.MY_BOOKS;
import static com.example.ks.bookstore.Utility.Constants.RESOLVE_BOOK_TYPE;
import static com.example.ks.bookstore.Utility.Constants.SEARCH_RESULTS;
import static com.example.ks.bookstore.Utility.Constants.WISH_LIST_BOOKS;

public class
BookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Book>books=new ArrayList<>();
    private int contentType=Constants.NO_CONTENT;
    private int progressbarDecider =1;

    public BookAdapter(ArrayList<Book>books,int contentType){
        this.books=books;
        this.contentType=contentType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ViewType.ITEM) {
            return new BookViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_book_view,parent,false));
        } else if (viewType == ViewType.FOOTER) {
            return new FooterViewHolder(new ProgressBar(parent.getContext()));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (position==books.size()-1&&Utility.isInternetAvailable(holder.itemView.getContext()))
            progressbarDecider=1;
        if (holder instanceof BookViewHolder){
            final BookViewHolder bookViewHolder=(BookViewHolder)holder;
            bookViewHolder.nameView.setText(books.get(position).getName());
            bookViewHolder.authorView.setText(books.get(position).getAuthor());
            bookViewHolder.tagView.setText(books.get(position).getTag());
            bookViewHolder.tagView.setBackgroundColor(getRandomColor());

            bookViewHolder.nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bookViewIntent=new Intent(v.getContext(), BookViewActivity.class);
                if (contentType==Constants.MY_BOOKS)
                    bookViewIntent.putExtra(RESOLVE_BOOK_TYPE, Constants.MY_BOOKS);
                else if (contentType==Constants.WISH_LIST_BOOKS)
                    bookViewIntent.putExtra(RESOLVE_BOOK_TYPE,Constants.WISH_LIST_BOOKS);
                else if (contentType==Constants.ONLINE_BOOKS)
                    bookViewIntent.putExtra(RESOLVE_BOOK_TYPE, Constants.ONLINE_BOOKS);
                else if (contentType==Constants.SEARCH_RESULTS)
                    bookViewIntent.putExtra(RESOLVE_BOOK_TYPE,SEARCH_RESULTS);
                bookViewIntent.putExtra("index",position);
                v.getContext().startActivity(bookViewIntent);
            }
            });

            if (contentType==MY_BOOKS||contentType==WISH_LIST_BOOKS){
                bookViewHolder.nameView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (contentType==MY_BOOKS){
                            DeleteBookTask deleteBookTask=new DeleteBookTask();
                            deleteBookTask.execute(books.get(position).getServerId());
                            books.remove(position);
                            notifyDataSetChanged();
                        }else if (contentType==WISH_LIST_BOOKS){
                            RemoveBookTask removeBookTask=new RemoveBookTask();
                            removeBookTask.execute(books.get(position).getServerId(),
                                    AppData.getServerId(bookViewHolder.authorView.getContext()));
                            books.remove(position);
                            notifyDataSetChanged();
                        }
                        return true;
                    }
                });
            }
        }else if (holder instanceof FooterViewHolder) {
            if (contentType==Constants.ONLINE_BOOKS){
                MainActivity.getAndShowOnlineBooks(new GetBooksTask.GetBooksListener() {
                    @Override
                    public void onBooksReceived(ArrayList<Book> result) {
                        removeProgressBar();
                        showBooks(result);
                }

                    @Override
                    public void onBooksNotReceived() {
                    removeProgressBar();
                }
                });
            }if (contentType== Constants.MY_BOOKS){
                GetMyBooksTask myBooksTask=new GetMyBooksTask(new GetMyBooksTask.GetMyBooksListener() {
                    @Override
                    public void onMyBooksReceived(ArrayList<Book> result) {
                        removeProgressBar();
                        showBooks(result);
                    }

                    @Override
                    public void onMyBooksNotReceived() {
                        removeProgressBar();
                    }
                });
                myBooksTask.execute(AppData.getServerId(holder.itemView.getContext()));
            }else if (contentType==Constants.WISH_LIST_BOOKS){
                GetMyWishList wishListTask =new GetMyWishList(new GetMyWishList.GetMyWishListListener() {
                    @Override
                    public void onMyWishListReceived(ArrayList<Book> result) {
                        removeProgressBar();
                        showBooks(result);
                    }

                    @Override
                    public void onMyWishListNotReceived() {
                        removeProgressBar();
                    }
                });
                wishListTask.execute(AppData.getServerId(holder.itemView.getContext()));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == books.size()){
            return ViewType.FOOTER;
        }
        return ViewType.ITEM;
    }

    @Override
    public int getItemCount() {
        return books.size()+ progressbarDecider;//+1 for footer
    }

    public void showBooks(ArrayList<Book>onlineBooks){
        int start=onlineBooks.size();
        books.addAll(onlineBooks);
        notifyItemChanged(start,onlineBooks.size());
    }

    public void removeProgressBar(){
        progressbarDecider =0;
        notifyItemRemoved(books.size());
    }

    public void showProgressBar(){
        progressbarDecider=1;
        notifyItemChanged(books.size());
    }

    //temporary code
    private static int getRandomColor(){
        int red=(int)(Math.random()*128+127);
        int green=(int)(Math.random()*128+127);
        int blue=(int)(Math.random()*128+127);

        //noinspection NumericOverflow
        return 0xff<<24|(red<<16)|(green<<8)|blue;
    }

    private static class FooterViewHolder extends RecyclerView.ViewHolder {
        FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class BookViewHolder extends RecyclerView.ViewHolder{
        private TextView nameView;
        private TextView authorView;
        private TextView tagView;

        BookViewHolder(View itemView) {
            super(itemView);
            nameView =itemView.findViewById(R.id.book_name);
            authorView =itemView.findViewById(R.id.book_author);
            tagView =itemView.findViewById(R.id.book_tag);
        }
    }

    private interface ViewType{
        int FOOTER = 1;
        int ITEM = 2;
    }
}
