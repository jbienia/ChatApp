package com.jason.livechat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    EditText messageInput;
    Button sendButton;
    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ListView messagesView = (ListView) findViewById(R.id.messages_view);

        // get our input field by its ID
        messageInput = (EditText) findViewById(R.id.message_input);


        // get our button by its ID
        sendButton = (Button) findViewById(R.id.send_button);

        // set its click listener
        //sendButton.setOnClickListener('onClick');

        Pusher pusher = new Pusher("ed2beb5c7dad70f7bfad");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Channel channel = pusher.subscribe(preferences.getString("room","noRoom"));


        channel.bind("my-event", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
               runOnUiThread(new Runnable() {
                   public void run() {
                       Gson gson = new Gson();
                       Message message = gson.fromJson(data, Message.class);
                       messageAdapter.add(message);
                       Log.d("EVNT FIRE", "hey");

                       // have the ListView scroll down to the new message
                       messagesView.setSelection(messageAdapter.getCount() - 1);
                   }
               });
               }
        });

        pusher.connect();


        // Create the adapter that will bind to our list view
        messageAdapter = new MessageAdapter(this, new ArrayList<Message>());
        Log.d("JB", messagesView.toString());

        messagesView.setAdapter(messageAdapter);
    }

    // the data that will be in our array list as part of the adapter
    public class Message {
        public String text;
        public String name;
        public long time;
    }

    // The adapter used for the messagesView
    public class MessageAdapter extends BaseAdapter {

        Context messageContext;
        ArrayList<Message> messageList;

        @Override
        public int getCount() {
            return messageList.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return messageList.get(position);
        }

        // Used to convert an item in the array list of messages to a view
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // an wrapper objec that holds a reference to the views that make up the line items
            MessageViewHolder holder;

            // if there is not already a view created for an item in the Message list.
            Log.d("INFLATE", "getView: ");
            if (convertView == null) {
                LayoutInflater messageInflater = (LayoutInflater) messageContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

                // create a view out of our `message.xml` file
                convertView = messageInflater.inflate(R.layout.chat, null);

                // create a MessageViewHolder
                holder = new MessageViewHolder();

                // set the holder's properties to elements in `message.xml`
                holder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.img_thumbnail);
                holder.senderView = (TextView) convertView.findViewById(R.id.message_sender);
                holder.bodyView = (TextView) convertView.findViewById(R.id.message_body);

                // assign the holder to the view we will return
                convertView.setTag(holder);
            } else {

                // otherwise fetch an already-created view holder
                holder = (MessageViewHolder) convertView.getTag();
            }

            // get the message from its position in the ArrayList
            Message message = (Message) getItem(position);

            // set the elements' contents
            holder.bodyView.setText(message.text);
            holder.senderView.setText(message.name);


            return convertView;
        }

        public MessageAdapter(Context context, ArrayList<Message> messages) {
            messageList = messages;
            messageContext = context;


        }

        public void add(Message message){
            messageList.add(message);
            notifyDataSetChanged();
        }

    }

    /**
     * A wrapper for the views that make up each line item
     */
    private static class MessageViewHolder {
        public ImageView thumbnailImageView;
        public TextView senderView;
        public TextView bodyView;
    }

    public void onClick(View v) {
        Toast.makeText(this,"hey",Toast.LENGTH_LONG);
        Log.d("JB", "onClick: ");
        postMessage();
    }

    private void postMessage()  {
        String text = messageInput.getText().toString();

        // return if the text is blank
        if (text.equals("")) {
            return;
        }


        RequestParams params = new RequestParams();

        // get the name of the chat room
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String room = preferences.getString("room","no-data");

        // set our JSON object
        params.put("text", text);
        params.put("room",room);

        // create our HTTP client
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://10.0.2.2";

        client.post(url + "/messages", params, new JsonHttpResponseHandler(){



            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                runOnUiThread(new Runnable() {
                    @Override
                   public void run() {
                        messageInput.setText("");
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                Toast.makeText(
                       getApplicationContext(),
                        "Something went wrong :(" + statusCode + " "+responseString,
                        Toast.LENGTH_LONG
                ).show();
            }

        });

    }
}
