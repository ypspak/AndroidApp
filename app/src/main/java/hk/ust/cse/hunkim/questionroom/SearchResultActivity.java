package hk.ust.cse.hunkim.questionroom;

import android.app.ListActivity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.Question;

public class SearchResultActivity extends ListActivity {

    // TODO: change this to your own Firebase URL
    private String roomName;
    private String searchInput;
    private String Firebase_URL;
    private Firebase mFirebaseRef;
    private QuestionListAdapter mChatListAdapter;
    private ValueEventListener mConnectedListener;
    private int sortIndex;

    private DBUtil dbutil;

    public DBUtil getDbutil() {
        return dbutil;
    }

    public int getSortIndex(){return sortIndex;}

    public void setSortIndex(int i){sortIndex = i;}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialized once with an Android context.
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_search_result);
        Intent intent = getIntent();
        roomName = intent.getStringExtra(SearchMainActivity.ROOM_NAME);
        searchInput = intent.getStringExtra(SearchMainActivity.INPUT);
        Firebase_URL = intent.getStringExtra(SearchMainActivity.m_FirebaseURL);

        //Log.e("Test", roomName);
        //Log.e("Test", searchInput);
        //Log.e("Test", Firebase_URL);

        this.sortIndex = 0;
        // Setup our Firebase mFirebaseRef
        mFirebaseRef = new Firebase(Firebase_URL).child(roomName).child("questions");

        // get the DB Helper
        DBHelper mDbHelper = new DBHelper(this);
        dbutil = new DBUtil(mDbHelper);
    }



    @Override
    public void onStart() {
        super.onStart();

        //GUI design initialization <26/10/2015 by Peter Yeung>
        //This is due to Android default, all buttons are come with capitalized.
        Button quitButton = (Button) findViewById(R.id.close);
        quitButton.setText("Search result of " + searchInput);
        quitButton.setTransformationMethod(null);

        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = getListView();
        // Tell our list adapter that we only want 200 messages at a time
        mChatListAdapter = new QuestionListAdapter(
                mFirebaseRef.orderByChild("timestamp").limitToFirst(200),
                this, R.layout.question, roomName, searchInput);
        listView.setAdapter(mChatListAdapter);

        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });


        // Finally, a little indication of connection status
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(SearchResultActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SearchResultActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
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
        //mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        //mChatListAdapter.cleanup();
    }

    /*private String getRoomName(){
        return roomName;
    }*/

    public void Close(View view) {
        finish();
    }
}
