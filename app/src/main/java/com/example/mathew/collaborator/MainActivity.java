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
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private int userID;
    private HashMap<String, Integer> groups = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.listView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
