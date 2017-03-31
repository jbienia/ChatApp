package com.jason.livechat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Iterator;

import static com.loopj.android.http.AsyncHttpClient.log;

public class HomeActivity extends AppCompatActivity {

    DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference();
    DatabaseReference roomsRef = myRef1.child("Rooms");
    DatabaseReference user = myRef1.child("users");
    JSONObject jSon;
    JSONObject jSonRooms;
    Intent intent ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        intent= getIntent();


        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();
                DataSnapshot convert = dataSnapshot.child("Rooms");
                Object roomValue = convert.getValue();

                Gson gSon = new Gson();

                try
                {
                    jSon = new JSONObject(value.toString());

                    // an json object of the rooms
                    jSonRooms = new JSONObject(roomValue.toString());

                    //String thing = jSon.toString();
                  // JSONArray array = new JSONArray(value.toString());

                    Log.d("array", jSonRooms.toString());

                    //array = new getJsonArray(value.toString());
                }
                catch(Exception e) {
                    Log.d("Arryerror", e.getMessage().toString());
                }

                try
                {
                    // Retrieves the password given a specific user name
                   // log.d("JB",jSon.getJSONObject("users").getString("Lury"));

                    //JSONArray thing =  jSon.getJSONArray("users");
                    //Log.d("THING",jSon.getJSONObject("users").get("Lury"));

                    //Object lury = jSon.getJSONObject("users").get("Lury");

                    // substing retrives the password
                    // Log.d("THING", thing.substring(12,thing.length() - 1));
                    // used to detect if there is a current user in the database
                    //log.d("BOOL",""+jSon.getJSONObject("users").has("ben"));
                }
                catch(Exception e) {
                    log.d("error","error");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    public void newRoom(View v)
    {
        // saves a reference to the key
        DatabaseReference roomsKey = myRef1.child("Rooms").push();

        //Creates a new room
        roomsKey.setValue("tech");

        // retrieves the key
       //Log.d("KEY", roomsKey.getKey());;
        String keyForRoom = roomsKey.getKey();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //preferences.getString("username","true");
        //Log.d("IntentData",  preferences.getString("userName","crap"));
        myRef1.child("users").child(preferences.getString("userName","crap")).child(keyForRoom).setValue("true");
       // Log.d("key", roomsKey.toString());
        Log.d("JSON", jSon.toString());

        try {
            Log.d("Jb", ""+ jSonRooms.getString("-KgXRVZiJMbVTfnLllK0"));
        }catch (Exception e) {
            Log.d("error", e.getMessage());;
        }

    }

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
