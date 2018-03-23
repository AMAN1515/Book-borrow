package com.example.ks.bookstore.UI;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ks.bookstore.Account.UserData;
import com.example.ks.bookstore.Account.UserProfile;
import com.example.ks.bookstore.MainActivity;
import com.example.ks.bookstore.Networking.ViewProfileTask;
import com.example.ks.bookstore.R;
import com.example.ks.bookstore.Utility.Constants;

public class ProfileActivity extends AppCompatActivity {
    TextView nameText, phoneNumberText;
    private ViewProfileTask profileTask=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        nameText =findViewById(R.id.name);
        phoneNumberText =findViewById(R.id.phoneNumber);

        int resolve=getIntent().getIntExtra("WHOSE", Constants.MY_PROFILE);
        if (resolve==Constants.MY_PROFILE){
            UserProfile profile=UserData.getUserProfile(this);
            nameText.setText(profile.getName());
            phoneNumberText.setText(profile.getPhone());
        }else {
            String id=getIntent().getStringExtra("id");
            profileTask=new ViewProfileTask(new ViewProfileTask.ProfileListener() {
                @Override
                public void profileReceived(String name, String phone) {
                    nameText.setText(name);
                    phoneNumberText.setText(phone);
                }

                @Override
                public void profileNotReceived() {

                }
            });
            profileTask.execute(id);
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (profileTask!=null&&profileTask.getStatus()!= AsyncTask.Status.FINISHED)
            profileTask.cancel(true);
        profileTask=null;
    }
}
