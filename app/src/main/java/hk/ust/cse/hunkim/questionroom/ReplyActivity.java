package hk.ust.cse.hunkim.questionroom;

import android.app.ListActivity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.reply.Reply;

/**
 * Created by CAI on 21/10/2015.
 */
public class ReplyActivity extends ListActivity {
    private static final String FIREBASE_URL = "https://cmkquestionsdb.firebaseio.com/";

    private String key;
    private String roomName;
    private ImageButton likePQB;
    private ImageButton dislikePQB;
    private Firebase replyContainerRef;
    private Firebase questionUrl;
    private ValueEventListener mConnectedListener;
    private ReplyListAdapter mChatListAdapter;
    private EditText inputText;
    private DBUtil dbutil;

    public DBUtil getDbutil() {
        return dbutil;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        Intent intent = getIntent();
        assert (intent != null);

        //currently just for testing that I am entered the replying room corresponding to the question
        key = intent.getStringExtra(QuestionListAdapter.REPLIED_QEUSTION);
        roomName = intent.getStringExtra(QuestionListAdapter.ROOM_NAME);
        setTitle("Room Name:" + roomName);
        replyContainerRef = new Firebase(FIREBASE_URL).child(roomName).child("replies").child(key);
        // make sure the keyboard wont pop up when I first time enter this interface
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        // get the DB Helper
        DBHelper mDbHelper = new DBHelper(this);
        dbutil = new DBUtil(mDbHelper);
    }

    public void onStart() {
        super.onStart();
        questionUrl = new Firebase(FIREBASE_URL).child(roomName).child("questions").child(key);
        likePQB = (ImageButton) findViewById(R.id.likeParentQuestion);
        dislikePQB = (ImageButton) findViewById(R.id.dislikeParentQuestion);

        likePQB.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateLikeDislike("like");
                    }
                }

        );

        dislikePQB.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateLikeDislike("dislike");
                    }
                }

        );
        UpdateHeader();

        //Like & dislike buttons


        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = getListView();
        // Tell our list adapter that we only want 200 messages at a time
        mChatListAdapter = new ReplyListAdapter(
                replyContainerRef.orderByChild("dislike").limitToFirst(200),
                this, R.layout.reply);
        listView.setAdapter(mChatListAdapter);

        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });

        mConnectedListener = replyContainerRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(ReplyActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ReplyActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }

    public void onStop() {
        super.onStop();
        replyContainerRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mChatListAdapter.cleanup();
    }

    //Make this function private only because I want it triggered by the SendReply Button. For security.
    private void sendMessage() {
        inputText = (EditText) findViewById(R.id.replyInput);
        inputText.setError(null);

        String input = inputText.getText().toString();
        if (!TextUtils.isEmpty(input)) { //todo: limitation on length of title, more outcome for preventing html attack for Q title
            // Before creating our 'model', we have to replace substring so that prevent code injection
            input = input.replace("<", "&lt;");
            input = input.replace(">", "&gt;");
            // Create our 'model', a Chat object
            Reply reply = new Reply(input);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            replyContainerRef.push().setValue(reply);
            inputText.setText("");
            updateQuestionReply();
        }else {
            inputText.setError(getString(R.string.error_field_required));
        }//warning to force user input reply
    }

    public void UpdateHeader() {
        TextView timeText = (TextView) findViewById((R.id.timetext));
        TextView titleText = (TextView) findViewById((R.id.head));
        TextView descText = (TextView) findViewById((R.id.desc));
        //timeText.setText("" + getDate(questionUrl.child("timestamp").get));
        retrieveQuestionDetails("timestamp", timeText, true);
        retrieveQuestionDetails("head", titleText, false);
        retrieveQuestionDetails("desc", descText, false);
        /*likeNumText.setText("" + question.getLike());
        dislikeNumText.setText("" + question.getDislike());
        replyNumText.setText("" + question.getReplies());*/
    }

    //Update Like here. For every person who have liked, their key is stored at database.
    public void updateOrder(String key, final int value) {

        if (dbutil.contains(key)) {
            Log.e("Dupkey", "Key is already in the DB!");
            return;
        }

        final Firebase orderRef = replyContainerRef.child(key).child("order");
        orderRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long orderValue = (Long) dataSnapshot.getValue();
                        Log.e("Order update:", "" + orderValue);

                        orderRef.setValue(orderValue + value);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );

        // Update SQLite DB
        dbutil.put(key);
    }

    public void updateLikeDislike(String attri) {

        if (dbutil.contains(key)) {
            Log.e("Dupkey", "Key is already in the DB!");
            return;
        }

        final Firebase orderRef = questionUrl.child(attri);
        orderRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long orderValue = (Long) dataSnapshot.getValue();
                        Log.e("Order update:", "" + orderValue);

                        orderRef.setValue(orderValue + 1);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );

        // Update SQLite DB
        dbutil.put(key);
    }

    public void retrieveQuestionDetails(String childName, final TextView textView, final boolean IsDate)
    {
        final Firebase childRef = questionUrl.child(childName);
        childRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (IsDate) {
                            Long tempTimestamp = (Long) dataSnapshot.getValue();
                            textView.setText("" + getDate((Long) tempTimestamp));
                        }
                        else {
                            String tempStr = (String) dataSnapshot.getValue();
                            textView.setText("" + tempStr);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );
    }

    public void updateQuestionReply() {
        final Firebase replyRef = questionUrl.child("replies");
        replyRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long replyValue = (Long) dataSnapshot.getValue();
                        Log.e("Reply update:", "" + replyValue);

                        //Add 1 value to the dislikeValue
                        replyRef.setValue(replyValue + 1);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );
    }

    private String getDate(long timestamp)
    {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = (new Date(timestamp));
        return df.format(date);
    }
    public void Close(View view) {
        finish();
    }
}
