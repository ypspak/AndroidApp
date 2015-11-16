package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import hk.ust.cse.hunkim.questionroom.hashtag.Hashtag;
import hk.ust.cse.hunkim.questionroom.hashtag_extracter.Hashtag_extracter;
import hk.ust.cse.hunkim.questionroom.question.Question;

/**
 * Created by CAI on 27/10/2015.
 */
public class PostQuestion extends Activity {


    private String roomName;
    private String Firebase_URL;
    private Firebase mFirebaseRef;
    private Firebase mFirebaseRef_Hashtag;
    private EditText titleInput;
    private EditText bodyInput;
    private Button postQuestion;
    private Button cancel;
    private Hashtag_extracter hashtag_extracter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_question_popbox);
//
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        //Defining size of the popwindow
        //Todo: Make the background out of the pop up windows to be Translucent
        getWindow().setLayout((int)(width*.8),(int)(height*.45));

        Intent intent = getIntent();
        assert (intent != null);
        roomName = intent.getStringExtra(MainActivity.ROOM_NAME);
        Firebase_URL = intent.getStringExtra(MainActivity.m_FirebaseURL);
        setTitle("Room name: " + roomName);
        // Setup our Firebase mFirebaseRef

        mFirebaseRef = new Firebase(Firebase_URL).child(roomName).child("questions");
        //mFirebaseRef_Hashtag = new Firebase(Firebase_URL).child(roomName).child("tags");

        //todo: collaborate with the sendMessage() function, how to disable the button when some fields are empty;
        // Setup our input methods. Enter key on the keyboard or pushing the send button
//        EditText titleInput = (EditText) findViewById(R.id.QuestionTitle);
//        titleInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
//                    sendMessage();
//                }
//                return true;
//            }
//        });
        // make sure the keyboard wont pop up when I first time enter this interface
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        postQuestion = (Button) findViewById(R.id.PostQuestion);
        postQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postQuestion(view);
            }
        });

        cancel = (Button) findViewById(R.id.Cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Close(view);
            }
        });
    }



    private void postQuestion(View view) {
        titleInput = (EditText) findViewById(R.id.QuestionTitle);
        titleInput.setError(null);
        bodyInput = (EditText) findViewById(R.id.QuestionBody);
        String title = titleInput.getText().toString();
        String body = bodyInput.getText().toString();
        if (!TextUtils.isEmpty(title)) { //todo: limitation on length of title, more outcome for preventing html attack for Q title
            // Before creating our 'model', we have to replace substring so that prevent code injection
            title = title.replace("<", "&lt;");
            title = title.replace(">", "&gt;");
            //todo: more outcome for preventing html attack for Q body
            // Before creating our 'model', we have to replace substring so that prevent code injection
            body = body.replace("<", "&lt;");
            body = body.replace(">", "&gt;");
            // Create our 'model', a Chat object

            Question question = new Question(title,body);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.push().setValue(question);
            PushHashTag(body);
            Close(view);
        }else {
            titleInput.setError(getString(R.string.error_field_required));
        }//warning to force user input title
    }

    private void PushHashTag(String body)
    {
        //Now push those hashtag
        mFirebaseRef = new Firebase(Firebase_URL).child(roomName).child("tags");
        hashtag_extracter = new Hashtag_extracter(body);
        for (int i = 0; i < hashtag_extracter.getListCount(); i++) {
            final String tagsName = hashtag_extracter.getListItem(i);
            final Query mRef = mFirebaseRef.orderByChild("name").equalTo(tagsName).limitToFirst(1);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getChildrenCount() == 0) {
                        Hashtag pushTags = new Hashtag(tagsName);
                        mFirebaseRef.push().setValue(pushTags);
                    } else {

                        String key = null;
                        HashMap<String, HashMap<String, Object>> tags = (HashMap<String, HashMap<String, Object>>) dataSnapshot.getValue();
                        for (Map.Entry<String, HashMap<String, Object>> entry : tags.entrySet()) {
                            key = entry.getKey();   //Actually only iterate once.
                        }

                        //I swear, this method is extremely bad.
                        HashMap<String, Object> hashtags = tags.get(key);
                        Long used = (Long) hashtags.get("used");
                        hashtags.put("used", used + 1);

                        mFirebaseRef.child(key).setValue(hashtags);
                    }

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }

    public void Close(View view) {
        finish();
    }
}
