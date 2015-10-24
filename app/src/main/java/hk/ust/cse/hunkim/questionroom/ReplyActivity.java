package hk.ust.cse.hunkim.questionroom;

import android.app.ListActivity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.reply.Reply;

/**
 * Created by CAI on 21/10/2015.
 */
public class ReplyActivity extends ListActivity {
    private static final String FIREBASE_URL = "https://ypspakclassroom.firebaseio.com/";

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
        setTitle("Room Name:" + roomName);
        mFirebaseRef = new Firebase(FIREBASE_URL).child(roomName).child("replies").child(key);
        questionContent = (TextView) findViewById(R.id.Question);


        EditText inputText = (EditText) findViewById(R.id.replyInput);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    public void onStart() {
        super.onStart();
    }

    //Make this function private only because I want it triggered by the SendReply Button. For security.
    private void sendMessage() {
        EditText inputText = (EditText) findViewById(R.id.replyInput);
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            // Before creating our 'model', we have to replace substring so that prevent code injection
            input = input.replace("<", "&lt;");
            input = input.replace(">", "&gt;");
            // Create our 'model', a Chat object
            Reply reply = new Reply(input);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.push().setValue(reply);
            inputText.setText("");
        }
    }
    public void Close(View view) {
        finish();
    }
}
