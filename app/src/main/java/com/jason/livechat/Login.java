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

import static com.loopj.android.http.AsyncHttpClient.log;

public class Login extends AppCompatActivity {

    String username;
    String password;
    FirebaseDatabase mDatabase;
    DatabaseReference reference;
    DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference();
    JSONObject jSon;
    String passwordFromDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();

                Gson gSon = new Gson();

                try
                {
                     jSon = new JSONObject(value.toString());
                }
                catch(Exception e) {
                    Log.d("JB", e.getMessage().toString());
                }

                try
                {
                    // Retrieves the password given a specific user name
                    log.d("JB",jSon.getJSONObject("users").getString("Lury"));

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
        // Changes the button text to register a new user
       // Button signIn = (Button)findViewById(R.id.email_sign_in_button);
        //signIn.setText("Register");

        EditText userNameTextField = (EditText)findViewById(R.id.email);
        username = userNameTextField.getText().toString();
        EditText passwordTextField = (EditText) findViewById(R.id.password);
        password = passwordTextField.getText().toString();

        try
        {
            if(jSon.getJSONObject("users").has(username))
            {
                // the password from the database
                passwordFromDatabase = jSon.getJSONObject("users").optString(username);
                log.d("JB",passwordFromDatabase.substring(12,passwordFromDatabase.length() - 1));
                log.d("PWORD",password);

                // Checks that the password from the datbase is the same as the one entered by the user
                if(passwordFromDatabase.substring(12,passwordFromDatabase.length() - 1).equals(password))
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
