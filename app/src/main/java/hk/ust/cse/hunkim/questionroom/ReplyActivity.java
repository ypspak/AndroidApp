package hk.ust.cse.hunkim.questionroom;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Date;

import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.hashtag.Hashtag_processor;
import hk.ust.cse.hunkim.questionroom.reply.Reply;
import hk.ust.cse.hunkim.questionroom.timemanager.TimeManager;

/**
 * Created by CAI on 21/10/2015.
 */
public class ReplyActivity extends ListActivity {
    private int question_NumLike;
    private int question_NumDislike;
    private int question_NumReply;
    private String question_Head;
    private String question_Desc;
    private Long question_Timestamp;
    private String[] question_Hashtag;

    private Hashtag_processor hashtag_processor;
    private String key;
    private String roomName;
    private String roomBaseUrl;
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

    public String getRoomBaseUrl(){return roomBaseUrl;}
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        Intent intent = getIntent();

        //currently just for testing that I am entered the replying room corresponding to the question
        key = intent.getStringExtra("PUSHED_ID");
        roomName = intent.getStringExtra("ROOM_NAME");
        roomBaseUrl = intent.getStringExtra("ROOM_BASE_URL");
        question_NumLike = intent.getIntExtra("NUM_LIKE", 0);
        question_NumDislike = intent.getIntExtra("NUM_DISLIKE", 0);
        question_NumReply = intent.getIntExtra("NUM_REPLY", 0);
        question_Head = intent.getStringExtra("HEAD");
        question_Desc = (intent.getStringExtra("DESC")!=null)?intent.getStringExtra("DESC").replace("\n", "<br>"): intent.getStringExtra("DESC");
        question_Timestamp = intent.getLongExtra("TIMESTAMP", 0);
        question_Hashtag = intent.getStringArrayExtra("TAGS");

        if(roomBaseUrl!=null){
            replyContainerRef = new Firebase(roomBaseUrl).child("replies");
        }
        // make sure the keyboard wont pop up when I first time enter this interface
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setTitle("Room Name:" + roomName);

        // get the DB Helper
        DBHelper mDbHelper = new DBHelper(this);
        dbutil = new DBUtil(mDbHelper);

        // Inflate the view and add it to the header
        final ListView listView = getListView();
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup headerview =  (ViewGroup) inflater.inflate(R.layout.reply_header, listView, false);
        listView.addHeaderView(headerview);
    }

    public void onStart() {
        super.onStart();
        if(roomBaseUrl!=null){
            questionUrl = new Firebase(roomBaseUrl).child("questions").child(key);
        }
        findViewById(R.id.send_reply_button).setOnClickListener(new View.OnClickListener() {
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
        if(replyContainerRef!=null){
            mChatListAdapter = new ReplyListAdapter(
                    replyContainerRef.orderByChild("parentID").equalTo(key).limitToFirst(200),
                    this, R.layout.reply);
        }

        //For the like & dislike button in headerview
        likePQB = (ImageButton) findViewById(R.id.parent_question_like_button);
        dislikePQB = (ImageButton) findViewById(R.id.parent_question_dislike_button);
        checkButtonPressed();
        likePQB.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateLikeDislike("like");
                        TextView likeText = (TextView) findViewById((R.id.parent_question_like_text));
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
                        TextView dislikeText = (TextView) findViewById((R.id.parent_question_dislike_text));
                        dislikeText.setText("" + (Integer.parseInt((String) dislikeText.getText()) + 1));
                        checkButtonPressed();
                    }
                }
        );

        //Now update the header, and update altogether  by setting the listview
        UpdateHeader();
        listView.setAdapter(mChatListAdapter);
        if(mChatListAdapter!=null){
            mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    //listView.setSelection(mChatListAdapter.getCount() - 1);
                }
            });
        }
    }

    public void onStop() {
        super.onStop();
        if(mChatListAdapter!=null)
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
        inputText = (EditText) findViewById(R.id.reply_input_field);
        inputText.setError(null);

        String input = inputText.getText().toString();
        if (!TextUtils.isEmpty(input)) { //todo: limitation on length of title, more outcome for preventing html attack for Q title
            // Before creating our 'model', we have to replace substring so that prevent code injection
            input = input.replace("<", "&lt;");
            input = input.replace(">", "&gt;");
            input = input.replace("\n", "<br>");
            // Create our 'model', a Chat object
            Reply reply = new Reply(input, key);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            if(replyContainerRef==null) return;
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
        TextView timeText = (TextView) findViewById((R.id.parent_question_time_text));
        timeText.setText("" + (new TimeManager(question_Timestamp)).getDate());
        Button titleText = (Button) findViewById((R.id.parent_question_head));
        titleText.setText(Html.fromHtml("" + question_Head));
        TextView descText = (TextView) findViewById((R.id.parent_question_desc));
        descText.setText(Html.fromHtml("" + question_Desc));
        TextView likeText = (TextView) findViewById((R.id.parent_question_like_text));
        likeText.setText("" + String.valueOf(question_NumLike));
        TextView dislikeText = (TextView) findViewById(R.id.parent_question_dislike_text);
        dislikeText.setText("" + String.valueOf(question_NumDislike));
        TextView hashtagText = (TextView) findViewById(R.id.parent_question_hashtags);

        if (question_Hashtag == null)
            hashtag_processor = new Hashtag_processor(this.findViewById(android.R.id.content), hashtagText, question_Hashtag, 0);
        else
            hashtag_processor = new Hashtag_processor(this.findViewById(android.R.id.content), hashtagText, question_Hashtag, question_Hashtag.length);

        hashtag_processor.HashtagTextJoin();
        //HashtagTextJoin(question_Hashtag);
        //hashtagText.setText(question_Hashtag != null ?  (TextUtils.join(" ", question_Hashtag)) : "None");
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

        /*EditText reply = (EditText) findViewById(R.id.replyInput);
        reply.requestFocus();
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }*/

        // Update SQLite DB
        dbutil.put(key);
    }

    public void updateLikeDislike(String attri) {

        if (dbutil.contains(key)) {
            Log.e("Dupkey", "Key is already in the DB!");
            return;
        }

        if(questionUrl==null) return;

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
        if(questionUrl == null) return;
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


    public void Close(View view) {
        finish();
    }
}
