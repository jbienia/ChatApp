package com.jason.livechat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Iterator;

import static com.loopj.android.http.AsyncHttpClient.log;

public class Login extends AppCompatActivity {

    String username;
    String password;
    FirebaseDatabase mDatabase;
    DatabaseReference reference;
    DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference();
    JSONObject jSon;
    JSONObject jSonUsers;
    String passwordFromDatabase;
    DataSnapshot tester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();

                tester = dataSnapshot;
               // DataSnapshot convert = dataSnapshot.child("users").child("BJones");
                //Object users = convert.getValue();



                Gson gSon = new Gson();

                try
                {
                     jSon = new JSONObject(value.toString());
                   // jSonUsers = new JSONObject(users.toString());

                }
                catch(Exception e) {
                    Log.d("JB", e.getMessage().toString());
                }

                try
                {
                    // Retrieves the password given a specific user name
                    log.d("JB",jSonUsers.optString("password").toString());

                    //JSONArray thing =  jSon.getJSONArray("users");
                    //Log.d("THING",jSon.getJSONObject("users").get("Lury"));

                    //Object lury = jSon.getJSONObject("users").get("Lury");

                    // substing retrives the password
                   // Log.d("THING", thing.substring(12,thing.length() - 1));
                    // used to detect if there is a current user in the database
                    log.d("BOOL",""+jSon.getJSONObject("users").has("ben"));
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

    public void register(View view) {
        EditText userNameTextField = (EditText) findViewById(R.id.email);
        username = userNameTextField.getText().toString();
        EditText passwordTextField = (EditText) findViewById(R.id.password);
        password = passwordTextField.getText().toString();

        // Creates a parent node with two children. Put new username and password into the database
        myRef1.child("users").child(username).child("password").setValue(password);
    }

    public void login(View view)
    {
       // DataSnapshot value = convert.child("users");
        // Changes the button text to register a new user
       // Button signIn = (Button)findViewById(R.id.email_sign_in_button);
        //signIn.setText("Register");

        EditText userNameTextField = (EditText)findViewById(R.id.email);
        username = userNameTextField.getText().toString();
        EditText passwordTextField = (EditText) findViewById(R.id.password);
        password = passwordTextField.getText().toString();

        // gets a particular snapshot of just the children of the entered user name
        DataSnapshot convert = tester.child("users").child(username);

        // Converts to an object. Used to create a json object of the snapshot
        Object users = convert.getValue();

        try{
            jSonUsers = new JSONObject(users.toString());
        }catch (Exception e){
            e.getMessage();
        }

        Log.d("USER", jSonUsers.toString());





        try
        {
            if(jSon.getJSONObject("users").has(username))
            {

                Iterator<String> keys = (Iterator<String>)jSonUsers.keys();

                try {
                    while(keys.hasNext())
                    {
                        String key = keys.next();
                        Log.d("keys", key);
                        if(key.equals("password") ) {
                            Log.d("Equls", "Will it ever equals ");
                            passwordFromDatabase = jSonUsers.getString(key);
                            Log.d("DBpwrd", passwordFromDatabase);
                        }





                        // Access each value for each key
                        //Log.d("What is this", jSon.getString(key).toString());
                    }
                }catch (Exception e) {
                    Log.d("join Error", e.getMessage());

                }


                // the password from the database
                //passwordFromDatabase = jSon.getJSONObject("users").optString(username);
                //log.d("JB",passwordFromDatabase.substring(12,passwordFromDatabase.length() - 1));
                //log.d("PWORD",password);

//                try {
//                    String pWord = jSon.getJSONObject("BJones").optString("password");
//                    Log.d("Blep",pWord);
//
//                }catch (Exception e)
//                {
//                    Log.d("TESTER", "works");
//                    e.getMessage();
//                }


                // Checks that the password from the datbase is the same as the one entered by the user
                if(passwordFromDatabase.equals(password))
                {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("userName",username);
                    editor.commit();

                    Intent intent = new Intent(this,HomeActivity.class);
                    startActivity(intent);
                }
            }
        }
        catch(Exception e)
        {
            e.getMessage();
        }


        // These next three lines are unused
        mDatabase = FirebaseDatabase.getInstance();
        reference = mDatabase.getReference();
        reference.orderByChild("Users");

    }
}
