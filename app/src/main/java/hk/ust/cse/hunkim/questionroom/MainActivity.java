package hk.ust.cse.hunkim.questionroom;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.hashtag.Hashtag;
import hk.ust.cse.hunkim.questionroom.hashtag.Hashtag_extracter;
import hk.ust.cse.hunkim.questionroom.question.Question;

public class MainActivity extends ListActivity {
    private String roomName;
    private String roomBaseUrl;
    private Firebase mFirebaseRef;
    private Firebase mFirebaseRef_Hashtag;
    private ImageButton sortButton; //Added by Marvin
    private ImageButton searchButton; //Added by Peter
    private ImageButton postQ;
    private int sortIndex;
    private QuestionListAdapter mChatListAdapter;
    private Hashtag_extracter hashtag_extracter;

    private DBUtil dbutil;

    public DBUtil getDbutil() {
        return dbutil;
    }

    public int getSortIndex(){return sortIndex;}

    public String getRoomName(){return roomName;}

    public String getRoomBaseUrl(){return roomBaseUrl;}

    public void setSortIndex(int i){sortIndex = i;}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialized once with an Android context.
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();

        this.sortIndex = 0;
        // Make it a bit more reliable
        roomName = intent.getStringExtra("ROOM_NAME");
        roomBaseUrl = intent.getStringExtra("ROOM_BASE_URL");

        setTitle("Room name: " + roomName);
        // Setup our Firebase mFirebaseRef
        mFirebaseRef = new Firebase(roomBaseUrl).child("questions");
        mFirebaseRef_Hashtag = new Firebase(roomBaseUrl).child("tags");
        postQ = (ImageButton) findViewById(R.id.postQuestion);

        // get the DB Helper
        DBHelper mDbHelper = new DBHelper(this);
        dbutil = new DBUtil(mDbHelper);
    }

    @Override
    public void onStart() {
        super.onStart();

        postQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postQuestion(view);
            }
        });
        //GUI design initialization <26/10/2015 by Peter Yeung>
        //This is due to Android default, all buttons are come with capitalized.
        Button quitButton = (Button) findViewById(R.id.close);
        quitButton.setText("" + roomName);
        quitButton.setTransformationMethod(null);

        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = getListView();
        // Tell our list adapter that we only want 200 messages at a time
        mChatListAdapter = new QuestionListAdapter(
                mFirebaseRef.orderByChild("timestamp").limitToFirst(200),
                this, R.layout.question);

        listView.setAdapter(mChatListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String input = ((TextView) view.findViewById(R.id.name)).getText().toString();
                //EnterSearchResult(view, input);
                ImageButton replyButton = (ImageButton) view.findViewById(R.id.QuestionReply);
                replyButton.performClick();
            }
        });
//        listView.post(new Runnable() {
//            @Override
//            public void run() {
//                listView.smoothScrollToPositionFromTop(0,0);
//            }
//        });

        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                //listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });

        searchButton = (ImageButton) findViewById(R.id.search);
        searchButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), SearchMainActivity.class);
                        intent.putExtra("ROOM_NAME", roomName);
                        intent.putExtra("ROOM_BASE_URL", roomBaseUrl);
                        view.getContext().startActivity(intent);
                    }
                }
        );

        sortButton = (ImageButton) findViewById(R.id.sort);
        sortButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Creating the instance of PopupMenu
                        PopupMenu popup = new PopupMenu(MainActivity.this, sortButton);
                        //Inflating the Popup using xml file
                        popup.getMenuInflater().inflate(R.menu.popup_menu_sort, popup.getMenu());

                        for (int i = 0; i < popup.getMenu().size(); ++i) {
                            MenuItem mi = popup.getMenu().getItem(i);
                            // check the Id as you wish
                            if (i==getSortIndex()) {
                                mi.setChecked(true);
                            }
                        }
                        //registering popup with OnMenuItemClickListener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                item.setChecked(true);
                                switch(item.getItemId()){
                                    case R.id.newest: setSortIndex(0); break;
                                    case R.id.hot: setSortIndex(1); break;
                                    case R.id.like: setSortIndex(2); break;
                                    case R.id.dislike: setSortIndex(3); break;
                                    case R.id.lastreplied: setSortIndex(4); break;
                                }
                                mChatListAdapter.setSortMethod(getSortIndex());
                                listView.setAdapter(mChatListAdapter);
                                return true;
                            }
                        });
                        popup.show();//showing popup menu
                    }
                });
    }

    //todo: Leave it here, probably will work on this part later
//    @Override
//    public void onResume(){
//
//    }
//
//    @Override
//    public void onPause(){
//
//    }

    @Override
    public void onStop() {
        super.onStop();
        mChatListAdapter.cleanup();
    }

    //Update Like here. For every person who have liked, their key is stored at database.
    public void updateLike(String key) {

        if (dbutil.contains(key)) {
            Log.e("Dupkey", "Key is already in the DB!");
            return;
        }

        final Firebase likeRef = mFirebaseRef.child(key).child("like");
        likeRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long likeValue = (long) dataSnapshot.getValue();
                        //Add 1 value to the echoValue
                        likeRef.setValue(likeValue + 1);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );
        // Update SQLite DB
        dbutil.put(key);
    }

    public void updateDislike(String key) {
        if (dbutil.contains(key)) {
            Log.e("Dupkey", "Key is already in the DB!");
            return;
        }

        final Firebase dislikeRef = mFirebaseRef.child(key).child("dislike");
        dislikeRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long dislikeValue = (long) dataSnapshot.getValue();
                        //Add 1 value to the dislikeValue
                        dislikeRef.setValue(dislikeValue + 1);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );
        // Update SQLite DB
        dbutil.put(key);
    }

    //Operation for the Question Post Button
    private void postQuestion(View view){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.post_question_popbox);
        final EditText titleInput = (EditText) dialog.findViewById(R.id.QuestionTitle);
        final EditText bodyInput = (EditText) dialog.findViewById(R.id.QuestionBody);
        Button cancel= (Button) dialog.findViewById(R.id.Cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button submit= (Button) dialog.findViewById(R.id.PostQuestion);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleInput.setError(null);
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

                    Question question = new Question(title, body);
                    // Create a new, auto-generated child of that chat location, and save our chat data there
                    mFirebaseRef.push().setValue(question);
                    PushHashTag(body + " " + title);
                    dialog.dismiss();
                } else {
                    titleInput.setError(getString(R.string.error_field_required));
                }//warning to force user input title
            }
        });
        dialog.show();
        return;
    }

    private void PushHashTag(String body)
    {
        //Now push those hashtag
        hashtag_extracter = new Hashtag_extracter(body);
        for (int i = 0; i < hashtag_extracter.getListCount(); i++) {
            final String tagsName = hashtag_extracter.getListItem(i);
            final Query mRef = mFirebaseRef_Hashtag.orderByChild("name").equalTo(tagsName).limitToFirst(1);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getChildrenCount() == 0) {
                        Hashtag pushTags = new Hashtag(tagsName);
                        mFirebaseRef_Hashtag.push().setValue(pushTags);
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

                        mFirebaseRef_Hashtag.child(key).setValue(hashtags);
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
