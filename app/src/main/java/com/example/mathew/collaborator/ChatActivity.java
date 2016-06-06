package com.example.mathew.collaborator;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private int groupID, userID;
    private ListView listView;
    private EditText chatbox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        listView = (ListView) findViewById(R.id.listView2);
        chatbox = (EditText)findViewById(R.id.editText);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            groupID = extras.getInt("group");
            userID = extras.getInt("userid");
            setTitle("Collaborator : " + extras.getString("groupname"));
        }

        TextView.OnEditorActionListener enterListener = new TextView.OnEditorActionListener(){
            public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_NULL
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    sendChatMessage();
                }
                return true;
            }
        };

        chatbox.setOnEditorActionListener(enterListener);

        new getChatTask().execute();
    }

    private void sendChatMessage()
    {
        sendChatTask task = new sendChatTask();
        try
        {
            task.message = URLEncoder.encode(chatbox.getText().toString(), "utf-8");
        }
        catch(Exception ex)
        {

        }
        chatbox.setText("");
        task.execute();
    }

    class sendChatTask extends AsyncTask<Void, Void, Boolean> {

        private Exception exception;
        public String message = "";

        protected Boolean doInBackground(Void... params) {
            try
            {
                ArrayList<String> chats = new ArrayList<>();
                URL url = new URL(Config.API_BASE + "SendChat&group=" + groupID + "&user=" + userID + "&message=" + message);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                return true;
            }
            catch (Exception e)
            {
                return false;
            }
        }

        protected void onPostExecute(Boolean result) {

            new getChatTask().execute();
        }
    }

    class getChatTask extends AsyncTask<Void, Void, ArrayList<String>> {

        private Exception exception;

        protected ArrayList<String> doInBackground(Void... params) {
            try
            {
                ArrayList<String> chats = new ArrayList<>();
                URL url = new URL(Config.API_BASE + "GetChats&group=" + groupID);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                for (String line = reader.readLine(); line != null; line = reader.readLine())
                {
                    String[] split = line.split(";");
                    chats.add(split[2] + ": " + split[0]);
                }
                return chats;
            }
            catch (Exception e)
            {
                return null;
            }
        }

        protected void onPostExecute(ArrayList<String> result) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChatActivity.this,android.R.layout.simple_list_item_1, android.R.id.text1, result);
            listView.setAdapter(adapter);
            listView.setSelection(adapter.getCount() - 1);
        }
    }
}
