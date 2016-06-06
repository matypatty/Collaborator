package com.example.mathew.collaborator;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private EditText groupSetter;
    private int userID;
    private HashMap<String, Integer> groups = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        groupSetter = (EditText)findViewById(R.id.editText3);
        listView = (ListView) findViewById(R.id.listView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGroupTask task = new setGroupTask();
                try
                {
                    task.group = URLEncoder.encode(groupSetter.getText().toString(), "utf-8");
                    task.friendly = groupSetter.getText().toString();
                }
                catch(Exception ex)
                {

                }
                task.execute();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            userID = extras.getInt("userid");
        }
        //Load all of the groups
        loadContent();
    }

    private void loadContent()
    {
        //http://www.collaborator.pw/api.php?Action=GetGroups
        new getGroupTask().execute();
    }

    class setGroupTask extends AsyncTask<Void, Void, Integer> {

        public String group, friendly;
        private Exception exception;

        protected Integer doInBackground(Void... params) {
            try
            {
                ArrayList<String> rooms = new ArrayList<>();
                URL url = new URL(Config.API_BASE + "AddGroup&user=" + userID + "&group=" + group);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                return Integer.parseInt(reader.readLine());
            }
            catch (Exception e)
            {
                return -1;
            }
        }

        protected void onPostExecute(Integer result) {
            groupSetter.setText("");
            if(result >= 0)
            {
                loadContent();
                Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                i.putExtra("group", result);
                i.putExtra("groupname", friendly);
                i.putExtra("userid", userID);
                startActivity(i);
            }

        }
    }

    class getGroupTask extends AsyncTask<Void, Void, ArrayList<String>> {

        private Exception exception;

        protected ArrayList<String> doInBackground(Void... params) {
            try
            {
                ArrayList<String> rooms = new ArrayList<>();
                URL url = new URL(Config.API_BASE + "GetGroups");
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                for (String line = reader.readLine(); line != null; line = reader.readLine())
                {
                    String[] split = line.split(";");
                    rooms.add(split[0]);
                    groups.put(split[0], Integer.parseInt(split[1]));
                }
                return rooms;
            }
            catch (Exception e)
            {
                return null;
            }
        }

        protected void onPostExecute(ArrayList<String> result) {

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, android.R.id.text1, result);
            listView.setAdapter(adapter);
            listView.setSelection(adapter.getCount() - 1);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    // ListView Clicked item index
                    int itemPosition     = position;

                    // ListView Clicked item value
                    String  itemValue    = (String) listView.getItemAtPosition(position);
                    Integer group = groups.get(itemValue);
                    // Show Alert
                    Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                    i.putExtra("group",group);
                    i.putExtra("groupname",itemValue);
                    i.putExtra("userid",userID);
                    startActivity(i);

                }

            });
        }
    }

}
