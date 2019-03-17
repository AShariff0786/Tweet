package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {
    public static final int MAX_TWEET_LENGTH=140;
    private EditText etCompose;
    private Button btnTweet;
    private TwitterClient client;
    private TextView tvCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        tvCounter= findViewById(R.id.tvCounter);
        client= TwitterApp.getRestClient(this);
        etCompose= findViewById(R.id.etCompose);
        btnTweet= findViewById(R.id.btnTweet);

        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                count= etCompose.length();
                if(count <= MAX_TWEET_LENGTH) {
                    tvCounter.setText(count + "/140");
                    tvCounter.setTextColor(Color.BLACK);
                    btnTweet.setEnabled(Boolean.parseBoolean("true"));
                }
                if(count > MAX_TWEET_LENGTH){
                    tvCounter.setText(count + "/140");
                    tvCounter.setTextColor(Color.RED);
                    btnTweet.setEnabled(Boolean.parseBoolean("false"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               final String TweetContent= etCompose.getText().toString();
               //Error-handling
                if(TweetContent.isEmpty()){
                    Toast.makeText(ComposeActivity.this, "Your tweet is empty!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(TweetContent.length() > MAX_TWEET_LENGTH){
                    Toast.makeText(ComposeActivity.this, "Your tweet is too long!", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(ComposeActivity.this, TweetContent, Toast.LENGTH_LONG).show();
               //Make API call to Twitter to publish the content in edit text
                client.composeTweet(TweetContent, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("TwitterClient", "Successfully posted tweet!" + response.toString());
                        try {
                            Tweet tweet= Tweet.fromJson(response);
                            Intent data= new Intent();
                            data.putExtra("tweet", Parcels.wrap(tweet));
                            //set result code and bundle data for response
                            setResult(RESULT_OK, data);
                            //closes the activity, pass data to parent
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("TwitterClient", "Failed to post tweet! " + responseString);
                    }
                });
            }
        });
    }
}
