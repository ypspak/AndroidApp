package hk.ust.cse.hunkim.questionroom;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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


    private int question_NumLike;
    private int question_NumDislike;
    private int question_NumReply;
    private String question_Head;
    private String question_Desc;
    private Long question_Timestamp;
    private String key;
    private String roomName;
    private ImageButton likePQB;
    private ImageButton dislikePQB;
    private Firebase replyContainerRef;
    private Firebase questionUrl;
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

        //currently just for testing that I am entered the replying room corresponding to the question
        key = intent.getStringExtra(QuestionListAdapter.REPLIED_QEUSTION);
        roomName = intent.getStringExtra(QuestionListAdapter.ROOM_NAME);
        question_NumLike = intent.getIntExtra("NUM_LIKE", 0);
        question_NumDislike = intent.getIntExtra("NUM_DISLIKE", 0);
        question_NumReply = intent.getIntExtra("NUM_REPLY", 0);
        question_Head = intent.getStringExtra("HEAD");
        question_Desc = intent.getStringExtra("DESC");
        question_Timestamp = intent.getLongExtra("TIMESTAMP", 0);


        replyContainerRef = new Firebase(FIREBASE_URL).child("rooms").child(roomName).child("replies");
        // make sure the keyboard wont pop up when I first time enter this interface
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setTitle("Room Name:" + roomName);

        // get the DB Helper
        DBHelper mDbHelper = new DBHelper(this);
        dbutil = new DBUtil(mDbHelper);
    }

    public void onStart() {
        super.onStart();
        questionUrl = new Firebase(FIREBASE_URL).child("rooms").child(roomName).child("questions").child(key);
        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes

        final ListView listView = getListView();
        // Tell our list adapter that we only want 200 messages at a time
        mChatListAdapter = new ReplyListAdapter(
                replyContainerRef.orderByChild("parentID").equalTo(key).limitToFirst(200),
                this, R.layout.reply);

        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                //listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });

        // Inflate the view and add it to the header
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup headerview =  (ViewGroup) inflater.inflate(R.layout.reply_header, listView, false);
        listView.addHeaderView(headerview);

        //For the like & dislike button in headerview
        likePQB = (ImageButton) findViewById(R.id.likeParentQuestion);
        dislikePQB = (ImageButton) findViewById(R.id.dislikeParentQuestion);
        checkButtonPressed();
        likePQB.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateLikeDislike("like");
                        TextView likeText = (TextView) findViewById((R.id.likeText));
                        likeText.setText("" + (Integer.parseInt((String) likeText.getText()) + 1));
                        checkButtonPressed();
                    }
                }
        );

        dislikePQB.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateLikeDislike("dislike");
                        TextView dislikeText = (TextView) findViewById((R.id.dislikeText));
                        dislikeText.setText("" + (Integer.parseInt((String) dislikeText.getText()) + 1));
                        checkButtonPressed();
                    }
                }
        );

        //Now update the header, and update altogether  by setting the listview
        UpdateHeader();
        listView.setAdapter(mChatListAdapter);
        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });
    }

    public void onStop() {
        super.onStop();
        mChatListAdapter.cleanup();
    }

    private void checkButtonPressed()
    {
        DBUtil dbUtil = this.getDbutil();
        boolean clickable = !dbUtil.contains(key);

        likePQB.setClickable(clickable);
        likePQB.setEnabled(clickable);
        dislikePQB.setClickable(clickable);
        dislikePQB.setEnabled(clickable);

        if (clickable) {
            likePQB.getBackground().setColorFilter(null);
            dislikePQB.getBackground().setColorFilter(null);
        } else {
            likePQB.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_OVER);
            dislikePQB.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_OVER);
        }
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
            Reply reply = new Reply(input, key);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            replyContainerRef.push().setValue(reply);
            inputText.setText("");
            updateQuestionReply();
            inputText.requestFocus();
        }else {
            inputText.setError(getString(R.string.error_field_required));
        }//warning to force user input reply

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void UpdateHeader() {
        TextView timeText = (TextView) findViewById((R.id.timetext));
        timeText.setText("" + getDate(question_Timestamp));
        Button titleText = (Button) findViewById((R.id.head_reply));
        titleText.setText("" + question_Head);
        TextView descText = (TextView) findViewById((R.id.desc));
        descText.setText("" + question_Desc);
        TextView likeText = (TextView) findViewById((R.id.likeText));
        likeText.setText("" + String.valueOf(question_NumLike));
        TextView dislikeText = (TextView) findViewById(R.id.dislikeText);
        dislikeText.setText("" + String.valueOf(question_NumDislike));
        titleText.setTransformationMethod(null);
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
                        if (dataSnapshot.getValue() != null) {
                            long orderValue = (long) dataSnapshot.getValue();
                            Log.e("Order update:", "" + orderValue);

                            orderRef.setValue(orderValue + value);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );

        EditText reply = (EditText) findViewById(R.id.replyInput);
        reply.requestFocus();
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }

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
                        if (dataSnapshot.getValue() != null) {
                            long orderValue = (long) dataSnapshot.getValue();
                            Log.e("Order update:", "" + orderValue);
                            orderRef.setValue(orderValue + 1);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );

        // Update SQLite DB
        dbutil.put(key);
    }

    public void updateQuestionReply() {
        questionUrl.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("replies").getValue()!=null){
                            long replyValue = (long) dataSnapshot.child("replies").getValue();
                            Log.e("Reply update:", "" + replyValue);
                            //Add 1 value to the dislikeValue
                            questionUrl.child("replies").setValue(replyValue + 1);
                        }
                        if(dataSnapshot.child("lastTimestamp").getValue()!=null){
                            questionUrl.child("lastTimestamp").setValue(new Date().getTime());
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );
    }

    //If you want to know more about the function, plz visit here http://developer.android.com/reference/android/text/format/DateUtils.html
    private String getDate(long postTime)
    {
        long currentTime = new Date().getTime();
        long timeResolution = 0;
        long timeDiff = currentTime-postTime;
        if(timeDiff < DateUtils.SECOND_IN_MILLIS*5){
            return "Just now";
        }

        if(timeDiff/DateUtils.MINUTE_IN_MILLIS == 0){
            timeResolution = DateUtils.SECOND_IN_MILLIS;
        }else if(timeDiff/DateUtils.HOUR_IN_MILLIS == 0){
            timeResolution = DateUtils.MINUTE_IN_MILLIS;
        }else if(timeDiff/DateUtils.DAY_IN_MILLIS == 0){
            timeResolution = DateUtils.HOUR_IN_MILLIS;
        }else{
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd KK:mm aa");
            Date date = (new Date(postTime));
            return df.format(date);
        }
        return DateUtils.getRelativeTimeSpanString(postTime, currentTime, timeResolution).toString();
    }

    public void Close(View view) {
        finish();
    }
}
