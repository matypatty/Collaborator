package com.example.mathew.collaborator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Register extends AppCompatActivity {

    private View mProgressView;
    private View mRegisterFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegisterFormView = findViewById(R.id.registerview);
        mProgressView = findViewById(R.id.progressBarregister);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void register(View v)
    {
        //Load all the gui elements
        String email = ((EditText)findViewById(R.id.editText_email)).getText().toString();
        String pass = ((EditText)findViewById(R.id.editText_password)).getText().toString();
        String name = ((EditText)findViewById(R.id.editText_name)).getText().toString();
        Boolean gender = ((RadioButton)findViewById(R.id.radioButtonmale)).isChecked();
        String dob = ((EditText)findViewById(R.id.editText_dob)).getText().toString();

        //Check everything is filled out.
        //<todo>

        //Register process
        showProgress(true);
        UserRegisterTask mAuthTask = new UserRegisterTask(email, pass, name, gender, dob);
        mAuthTask.execute((Void) null);
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mName;
        private final Boolean mGender;
        private final String mDOB;

        UserRegisterTask(String email, String password, String name, Boolean gender, String dob) {
            mEmail = email;
            mPassword = password;
            mName = name;
            mGender = gender;
            mDOB = dob;
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            String response = "";
            try
            {
                //Check login against API
                URL url = new URL(Config.API_BASE + "Register&email=" + mEmail + "&pass=" + mPassword + "&name=" + mName + "&gender=" + mGender + "&dob=" + mDOB);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                response = reader.readLine();

                return response.equals("true");
            }
            catch(Exception ex)
            {
                //The only error here would be not being able to reach the server.
                Toast.makeText(getApplicationContext(), "Unable to connect to the server.", Toast.LENGTH_LONG).show();
            }

            //If we made it this far the login has failed.
            Toast.makeText(getApplicationContext(), "Register failed. Error:" + response, Toast.LENGTH_LONG).show();
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (success) {
                startActivity(new Intent(Register.this,MainActivity.class));
                finish();
            } else {
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }
}
