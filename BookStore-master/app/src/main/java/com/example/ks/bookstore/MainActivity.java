package com.example.ks.bookstore;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.ks.bookstore.Account.AppData;
import com.example.ks.bookstore.Account.UserProfile;
import com.example.ks.bookstore.Networking.GetBooksTask;
import com.example.ks.bookstore.RecylerAdapters.BookAdapter;
import com.example.ks.bookstore.UI.BookActivity;
import com.example.ks.bookstore.UI.BookListActivity;
import com.example.ks.bookstore.UI.LoginActivity;
import com.example.ks.bookstore.UI.ProfileActivity;
import com.example.ks.bookstore.UI.SearchableActivity;
import com.example.ks.bookstore.Utility.Constants;
import com.example.ks.bookstore.Utility.Utility;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private int selectedItem=-1;
    private static ArrayList<Book>books=new ArrayList<>();
    private static long lastPing=0;
    private static BroadcastReceiver internetMonitor=null;
    private static BookAdapter liveContentAdapter=new BookAdapter(books,Constants.ONLINE_BOOKS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startUpChecks();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View v= navigationView.getHeaderView(0);
        TextView greeting=v.findViewById(R.id.userName);

        RecyclerView recyclerView = findViewById(R.id.mainRecycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(liveContentAdapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(BookActivity.class);    }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                if (selectedItem != -1 && newState == DrawerLayout.STATE_IDLE)
                    switch (selectedItem) {
                        case R.id.my_books:
                            launchActivity(BookListActivity.class, Constants.MY_BOOKS);
                            break;
                        case R.id.saved_books:
                            launchActivity(BookListActivity.class, Constants.WISH_LIST_BOOKS);
                            break;

                        case R.id.Logout:
                            AppData.logOut(MainActivity.this);
                            Intent restartIntent=new Intent(MainActivity.this,MainActivity.class);
                            restartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(restartIntent);
                            selectedItem=-1;
                            break;

                        case R.id.profile:
                            launchActivity(ProfileActivity.class);
                    }
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        if (System.currentTimeMillis()-lastPing>Constants.MIN_PING_INTERVAL&&UserProfile.getUser(this)!=null) {
            getAndShowOnlineBooks();
            greeting.setText("Hi "+ UserProfile.getUser(this).getName());
        }
    }


    private void startUpChecks(){
        if (!AppData.isLogIn(this)) {
            launchActivity(LoginActivity.class);
            finish();
        }
    }

    @Override
    protected void onResume() {
        internetMonitor=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Utility.isInternetAvailable(MainActivity.this)){
                    getAndShowOnlineBooks();
                }
            }
        };
        registerReceiver(internetMonitor,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (internetMonitor!=null)
            unregisterReceiver(internetMonitor);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        ComponentName componentName=new ComponentName(this, SearchableActivity.class);
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    private void launchActivity(Class activityClass){
        selectedItem=-1;
        Intent intent=new Intent(this,activityClass);
        startActivity(intent);
    }

    private void launchActivity(Class activityClass,int resolve){
        selectedItem=-1;
        Intent intent=new Intent(this,activityClass);
        intent.putExtra("resolve",resolve);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        selectedItem=item.getItemId();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static ArrayList<Book> getBooks() {
        return books;
    }

    public static void getAndShowOnlineBooks(GetBooksTask.GetBooksListener listener){
        GetBooksTask getBooksTask=new GetBooksTask(listener);
        getBooksTask.execute();
        lastPing=System.currentTimeMillis();
    }

    private void getAndShowOnlineBooks(){
        liveContentAdapter.showProgressBar();
        getAndShowOnlineBooks(new GetBooksTask.GetBooksListener() {
            @Override
            public void onBooksReceived(ArrayList<Book> result) {
                liveContentAdapter.showBooks(result);
            }

            @Override
            public void onBooksNotReceived() {
                liveContentAdapter.removeProgressBar();
            }
        });

    }

}
