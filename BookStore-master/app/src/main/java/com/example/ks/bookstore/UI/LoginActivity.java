package com.example.ks.bookstore.UI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

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

import com.example.ks.bookstore.Account.AppData;
import com.example.ks.bookstore.Account.UserData;
import com.example.ks.bookstore.Account.UserProfile;
import com.example.ks.bookstore.MainActivity;
import com.example.ks.bookstore.R;
import com.example.ks.bookstore.Utility.Constants;
import com.example.ks.bookstore.Utility.ServerContacts;
import com.example.ks.bookstore.Utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private UserLoginTask mAuthTask = null;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

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
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            setRequestedOrientation(getResources().getConfiguration().orientation);
            showProgress(true);
            mAuthTask = new UserLoginTask(mEmailView, mPasswordView, email,password);
            mAuthTask.execute();
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@")&&email.length()>4;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
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

    public void signUp(View view) {
        Intent signUp=new Intent(this,SignUpActivity.class);
        startActivity(signUp);
    }

    @SuppressLint("StaticFieldLeak")
 public class UserLoginTask extends AsyncTask<Void, Void, String[]> {

        private final AutoCompleteTextView mEmail;
        private final EditText mPassword;
        private String email, password;

        UserLoginTask(AutoCompleteTextView email, EditText password, String email1, String username) {
            mEmail = email;
            mPassword = password;
            this.email = email1;
            this.password = username;
        }

        @Override
        protected String[] doInBackground(Void... params) {

            String result[]=null;
            try {
                URL registerUrl=new URL(ServerContacts.Log_IN_URL);
                HttpURLConnection connection= (HttpURLConnection) registerUrl.openConnection();

                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("content-Type","application/json");

                OutputStream os=connection.getOutputStream();
                BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                String values[]={email,password};
                String keys[]={"email","password"};
                bw.write(Utility.getJsonObject(keys,values).toString());
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
                JSONObject object=new JSONObject(data.toString());
                String status=object.getString("status");
                if (status.equals(Constants.RESULT_SUCCESS)){
                    result=new String[3];
                    result[0]=object.getString("name");
                    result[1]=object.getString("no");
                    result[2]=object.getString("id");
                }
             } catch (MalformedURLException ignored) {
                return null;
            } catch (IOException ignored) {
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String [] results) {
            mAuthTask = null;
            showProgress(false);
            if (results!=null) {
                AppData.logIn(mEmail.getContext(),email,password,results[2]);
                UserData.setUpProfile(mEmail.getContext(),results[0],results[1]);
                Intent restartIntent=new Intent(mEmail.getContext(),MainActivity.class);
                restartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(restartIntent);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

