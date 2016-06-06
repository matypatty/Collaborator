package com.example.mathew.collaborator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

public class Register extends AppCompatActivity {

    private View mProgressView;
    private View mRegisterFormView;
    private EditText date;
    private Handler h;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegisterFormView = findViewById(R.id.registerview);
        mProgressView = findViewById(R.id.progressBarregister);

        date = (EditText)findViewById(R.id.editText_dob);
        date.addTextChangedListener(tw);

        //For sending toasts from background threads
         h = new Handler() {
            public void handleMessage(Message msg){
                    Toast.makeText(getApplicationContext(), (String)msg.obj, Toast.LENGTH_SHORT).show();
            }
        };
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



    public void malechecked(View v)
    {
        ((RadioButton)findViewById(R.id.radioButton2female)).setChecked(false);
    }

    public void femalechecked(View v)
    {
        ((RadioButton)findViewById(R.id.radioButtonmale)).setChecked(false);
    }

    //Borrowed this from stackoverflow @Juan Cortés
    TextWatcher tw = new TextWatcher() {
        private String current = "";
        private String ddmmyyyy = "DDMMYYYY";
        private Calendar cal = Calendar.getInstance();

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals(current)) {
                String clean = s.toString().replaceAll("[^\\d.]", "");
                String cleanC = current.replaceAll("[^\\d.]", "");

                int cl = clean.length();
                int sel = cl;
                for (int i = 2; i <= cl && i < 6; i += 2) {
                    sel++;
                }
                //Fix for pressing delete next to a forward slash
                if (clean.equals(cleanC)) sel--;

                if (clean.length() < 8){
                    clean = clean + ddmmyyyy.substring(clean.length());
                }else{
                    //This part makes sure that when we finish entering numbers
                    //the date is correct, fixing it otherwise
                    int day  = Integer.parseInt(clean.substring(0,2));
                    int mon  = Integer.parseInt(clean.substring(2,4));
                    int year = Integer.parseInt(clean.substring(4,8));

                    if(mon > 12) mon = 12;
                    cal.set(Calendar.MONTH, mon-1);
                    year = (year<1900)?1900:(year>2100)?2100:year;
                    cal.set(Calendar.YEAR, year);
                    // ^ first set year for the line below to work correctly
                    //with leap years - otherwise, date e.g. 29/02/2012
                    //would be automatically corrected to 28/02/2012

                    day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                    clean = String.format("%02d%02d%02d",day, mon, year);
                }

                clean = String.format("%s/%s/%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8));

                sel = sel < 0 ? 0 : sel;
                current = clean;
                date.setText(current);
                date.setSelection(sel < current.length() ? sel : current.length());
            }
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    };

    public void register(View v)
    {
        //Load all the gui elements
        String email = ((EditText)findViewById(R.id.editText_email)).getText().toString();
        String pass = ((EditText)findViewById(R.id.editText_password)).getText().toString();
        String name = ((EditText)findViewById(R.id.editText_name)).getText().toString();
        String gender = (((RadioButton)findViewById(R.id.radioButtonmale)).isChecked() ? "Male" : "Female");
        String dob = ((EditText)findViewById(R.id.editText_dob)).getText().toString();

        //Server checks everything because server is always right :)

        //Register process
        showProgress(true);
        UserRegisterTask mAuthTask = new UserRegisterTask(email, pass, name, gender, dob);
        mAuthTask.execute((Void) null);
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private String mEmail;
        private String mPassword;
        private String mName;
        private String mGender;
        private String mDOB;

        UserRegisterTask(String email, String password, String name, String gender, String dob) {
            try
            {
                mEmail = URLEncoder.encode(email, "utf-8");
                mPassword = URLEncoder.encode(password, "utf-8");
                mName = URLEncoder.encode(name, "utf-8");
                mGender = URLEncoder.encode(gender, "utf-8");
                mDOB = URLEncoder.encode(dob, "utf-8");
            }
            catch(Exception ex)
            {

            }
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

                //Handle response.
                if(response.equals("true"))
                {
                    return true;
                }
                else
                {
                    Message message = h.obtainMessage();
                    message.obj = response;
                    h.sendMessage(message);
                    return false;
                }
            }
            catch(Exception ex)
            {
                //The only error here would be not being able to reach the server.
                Message message = h.obtainMessage();
                message.obj = "Unable to connect to server";
                h.sendMessage(message);
                return false;
            }
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
