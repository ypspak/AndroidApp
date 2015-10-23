package hk.ust.cse.hunkim.questionroom;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;

/**
 * Created by CAI on 21/10/2015.
 */
public class ReplyActivity extends ListActivity {
    private static final String FIREBASE_URL = "https://cmkquestionsdb.firebaseio.com/";

    private TextView questionContent;
    private String key;
    private String roomName;
    private Firebase mFirebaseRef;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        Intent intent = getIntent();
        assert (intent != null);

        //currently just for testing that I am entered the replying room corresponding to the question
        key = intent.getStringExtra(QuestionListAdapter.REPLIED_QEUSTION);
        roomName = intent.getStringExtra(QuestionListAdapter.ROOM_NAME);
        mFirebaseRef = new Firebase(FIREBASE_URL).child(roomName).child("questions").child(key);
        questionContent = (TextView) findViewById(R.id.Question);

        questionContent.setText(key + "," + roomName);




    }
    public void Close(View view) {
        finish();
    }
}
