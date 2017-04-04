package com.jason.livechat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class HomeActivity extends AppCompatActivity {

    DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference();
    DatabaseReference roomsRef = myRef1.child("Rooms");
    DatabaseReference user = myRef1.child("users");
    JSONObject jSon;
    JSONObject jSonRooms;
    Intent intent ;
    ArrayList<String> roomsList;
    Iterator<String> keys;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        intent= getIntent();

         roomsList = new ArrayList<String>();

        context = this;



        myRef1.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();
                DataSnapshot convert = dataSnapshot.child("Rooms");
                Object roomValue = convert.getValue();

                try
                {
                    jSon = new JSONObject(value.toString());

                    // an json object of the rooms
                    jSonRooms = new JSONObject(roomValue.toString());

                    // all the keys on the rooms table
                    keys = jSonRooms.keys();
                }
                catch(Exception e) {
                     e.getMessage();
                }

                try
                {
                    // Loops through the keys of the rooms table using an iterator. keys is an iterator object
                    while(keys.hasNext())
                    {
                        String key = keys.next();
                        roomsList.add(jSonRooms.getString(key));
                    }

                    // Sets the adapter after the array list is populated
                    setAdapter(roomsList);
                }

                catch(Exception e) {
                    e.getMessage();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Helper method used to creater a new adapter and set it to the list view
     * @param rooms an array list of rooms from the firebase db
     */
    public void setAdapter(ArrayList<String> rooms)
    {
        UserAdapter adapter = new UserAdapter(context,R.layout.chat,roomsList);

        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.listViewRooms);
        listView.setAdapter(adapter);
    }

    /**
     * The Adapter object
     */
    private class UserAdapter extends ArrayAdapter<String> {

        private ArrayList<String> items;


        public UserAdapter(Context context, int textViewResourceId, ArrayList<String> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        //This method is called once for every item in the ArrayList as the list is loaded.
        //It returns a View -- a list item in the ListView -- for each item in the ArrayList
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d("daROoom",":LKJL:KJLILK" );
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.chat, null);
            }

            String room = items.get(position);

            if (room != null) {
               TextView bodyView = (TextView)v.findViewById(R.id.message_body);


                if (bodyView != null) {
                    bodyView.setText("Room: " + room);
                }
            }
            return v;
        }
    }

    /**
     * Uses a push() to add a new room with a key onto the rooms table.
     * Adds a key value pair of the room to the creater of the room as well
     * @param v
     */
    public void newRoom(View v)
    {
        // Creates a reference to the newly added rooms key and pushes it onto the table
        DatabaseReference roomsKey = myRef1.child("Rooms").push();

        // Retrieves the text from the user entered field. The name of their new room
        EditText newRoom = (EditText)findViewById(R.id.edTxtNewRoom);

        // Sets the value of the newly generated key in rooms table
        roomsKey.setValue(newRoom.getText().toString());

        // retrieves the key in string form
        String keyForRoom = roomsKey.getKey();

        // used to get the username stored in Shared Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Adds the key value pair entered by the user to the users table
        myRef1.child("users").child(preferences.getString("userName","crap")).child(keyForRoom).setValue(newRoom.getText().toString());

       // Log.d("key", roomsKey.toString());
        Log.d("JSON", jSon.toString());

        try {
            Log.d("Jb", ""+ jSonRooms.getString("-KgXRVZiJMbVTfnLllK0"));
        }catch (Exception e) {
            Log.d("error", e.getMessage());;
        }
    }

    /**
     * Iterates throught the rooms keys and returns their values
     * @param V
     */
    public void joinRoom(View V)
    {
        Log.d("Check", jSon.optString("Rooms"));

        // keys used to iterate through the rooms
        Iterator<String> keys = (Iterator<String>)jSonRooms.keys();
       // Log.d("keys", keys.);

        // Loops through the keys in the Rooms table
        try {
            while(keys.hasNext())
            {
                String key = keys.next();


                // Access each value for each key
                Log.d("What is this", jSonRooms.getString(key).toString());
            }
        }catch (Exception e) {
            Log.d("join Error", e.getMessage());

        }
    }
}
