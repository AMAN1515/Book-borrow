package com.example.ks.bookstore.UI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.example.ks.bookstore.Account.AppData;
import com.example.ks.bookstore.Account.UserData;
import com.example.ks.bookstore.MainActivity;
import com.example.ks.bookstore.R;
import com.example.ks.bookstore.Utility.Constants;
import com.example.ks.bookstore.Utility.ServerContacts;
import com.example.ks.bookstore.Utility.Utility;

import org.json.JSONObject;

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


public class SignUpActivity extends AppCompatActivity{
    private UserLoginTask mAuthTask = null;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView,phoneView,nameView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        nameView=findViewById(R.id.name);
        phoneView=findViewById(R.id.phoneNumber);

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLoginOrSignUp();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    private void attemptLoginOrSignUp() {
        if (mAuthTask != null) {
            return;
        }

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String name=nameView.getText().toString();
        String phone=phoneView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }else if (!isPhoneValid(phone)){
            focusView=phoneView;
            cancel=true;
        }else if (!isNameValid(name)){
            focusView=nameView;
            cancel=true;

        }

        if (cancel) focusView.requestFocus();
         else {
            showProgress(true);
            mAuthTask = new UserLoginTask(email,password,name,phone);


            String keys[]={"email","password","name","no"};
            String values[]={email,password,name,phone};
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mAuthTask.execute(Utility.getJsonObject(keys,values));
        }
    }

    private boolean isNameValid(String name) {
        return !TextUtils.isEmpty(name)&&name.length()>2;
    }

    private boolean isPhoneValid(String phone) {
        return !TextUtils.isEmpty(phone)&&phone.length()>=10;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@")&&email.length()>8;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4||!TextUtils.isEmpty(password);
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        if (mAuthTask!=null)
            mAuthTask.cancel(true);
    }

    @SuppressLint("StaticFieldLeak")
    public class UserLoginTask extends AsyncTask<JSONObject, Void, String> {

        String email,password,name,phone;
        UserLoginTask(String email,String password,String name,String phone){
            this.email=email;
            this.password=password;
            this.name=name;
            this.phone=phone;
        }

        @Override
        protected String doInBackground(JSONObject... params) {
            String result;
            try {
                URL registerUrl=new URL(ServerContacts.SIGN_UP_URL);
                HttpURLConnection connection= (HttpURLConnection) registerUrl.openConnection();

                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("content-Type","application/json");

                OutputStream os=connection.getOutputStream();
                BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                bw.write(params[0].toString());
                bw.close();
                os.close();

                InputStream in = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in,"iso-8859-1");
                BufferedReader response=new BufferedReader(inputStreamReader);

                StringBuilder data= new StringBuilder();
                String temp;
                while ((temp=response.readLine())!=null)
                    data.append(temp);

                inputStreamReader.close();
                in.close();
                result=data.toString();
            } catch (MalformedURLException ignored) {
                return null;
            } catch (IOException ignored) {
                return null;
            }
            return result;
        }

        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            showProgress(false);

            if (result!=null&&!result.equals(Constants.RESULT_FAILURE)) {
                AppData.logIn(SignUpActivity.this,email,password,result.substring(1,result.length()-1));
                UserData.setUpProfile(SignUpActivity.this,name,phone);
                Intent restartIntent=new Intent(mEmailView.getContext(),MainActivity.class);
                restartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(restartIntent);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled(){
            mAuthTask = null;
            showProgress(false);
        }
    }
}