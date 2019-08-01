package com.example.twitterclone;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class SendTweetActivity extends AppCompatActivity {

    private EditText edtTweet;
    private ListView listViewTweets;
    private Button btnSendTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_send_tweet );

        edtTweet = findViewById(R.id.edtSendTweet);
        btnSendTweet = findViewById( R.id.btnSendTweet );
        listViewTweets = findViewById( R.id.listViewTweets );

        final ArrayList<HashMap<String, String>> tweetList = new ArrayList<>();
        final SimpleAdapter adapter = new SimpleAdapter(SendTweetActivity.this, tweetList, android.R.layout.simple_list_item_2, new String[]
                {"tweetUserName", "tweetValue"}, new int[]{android.R.id.text1, android.R.id.text2});
        try{
            ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("MyTweet");
            parseQuery.whereContainedIn("user", ParseUser.getCurrentUser().getList("fanOf"));
            parseQuery.orderByDescending("createdAt");
            parseQuery.findInBackground( new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {
                        for (ParseObject tweetObject : objects) {
                            HashMap<String, String> userTweet = new HashMap<>();
                            userTweet.put("tweetUserName", tweetObject.getString("user"));
                            userTweet.put("tweetValue", tweetObject.getString("tweet"));
                            tweetList.add(userTweet);
                        }
                        listViewTweets.setAdapter( adapter );
                    }
                }
            } );
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendTweet(View view) {

        ParseObject parseObject = new ParseObject("MyTweet");
        parseObject.put("tweet", edtTweet.getText().toString());
        parseObject.put("user", ParseUser.getCurrentUser().getUsername());

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toasty.success( SendTweetActivity.this, "Sent", Toasty.LENGTH_SHORT ).show();
                } else {
                    Toasty.error( SendTweetActivity.this, e.getMessage(), Toasty.LENGTH_SHORT ).show();
                }
                progressDialog.dismiss();
            }
        });
    }
}
