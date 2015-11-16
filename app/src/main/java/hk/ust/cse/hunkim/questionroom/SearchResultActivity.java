package hk.ust.cse.hunkim.questionroom;

import android.app.ListActivity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
        mFirebaseRef = new Firebase(Firebase_URL).child("rooms").child(roomName).child("questions");

        // get the DB Helper
        DBHelper mDbHelper = new DBHelper(this);
        dbutil = new DBUtil(mDbHelper);

        // Inflate the view and add it to the header
        final ListView listView = getListView();
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup headerview =  (ViewGroup) inflater.inflate(R.layout.search_header, listView, false);
        listView.addHeaderView(headerview);
    }



    @Override
    public void onStart() {
        super.onStart();

        //GUI design initialization <26/10/2015 by Peter Yeung>
        //This is due to Android default, all buttons are come with capitalized.
        Button quitButton = (Button) findViewById(R.id.close);
        quitButton.setText("Search result");
        quitButton.setTransformationMethod(null);

        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = getListView();
        // Tell our list adapter that we only want 200 messages at a time
        mChatListAdapter = new QuestionListAdapter(
                mFirebaseRef.orderByChild("timestamp").limitToFirst(200),
                this, R.layout.question_search, roomName, searchInput);
        listView.setAdapter(mChatListAdapter);
        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                //listView.setSelection(mChatListAdapter.getCount() - 1);  NO NEED TO SCROLL DOWN AFTER UPDATING/LOADING LISTVIEW (PETER YEUNG 2015/11/16)
                setSearchResult((TextView) findViewById(R.id.searchResult), searchInput, mChatListAdapter.getCount()); //This is the base case for having results.
            }
        });

        // Finally, a little indication of connection status
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(SearchResultActivity.this, "Search completed", Toast.LENGTH_SHORT).show();
                    setSearchResult((TextView) findViewById(R.id.searchResult), searchInput, mChatListAdapter.getCount()); //This also needed, it is for no result
                } else {
                    Toast.makeText(SearchResultActivity.this, "Disconnected from Firebase. Searching cannot be done.", Toast.LENGTH_SHORT).show();
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
                        Log.e("Like update:", "" + likeValue);

                        //Add 1 value to the echoValue
                        likeRef.setValue(likeValue + 1);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );

        final Firebase orderRef = mFirebaseRef.child(key).child("order");
        orderRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //double orderValue = (double) dataSnapshot.getValue();
                        double orderValue;
                        if (dataSnapshot.getValue() instanceof Long) {
                            orderValue = ((Long) (dataSnapshot.getValue())).doubleValue();
                            Log.e("Type", "Long");
                        } else {
                            orderValue = (double) dataSnapshot.getValue();
                            Log.e("Type", "Double");
                        }
                        Log.e("Order update:", "" + orderValue);

                        orderRef.setValue(orderValue - 1);  //Need clarification, the higher value of order, the lower priorty sorted in firebase?
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
                        Log.e("Dislike update:", "" + dislikeValue);

                        //Add 1 value to the dislikeValue
                        dislikeRef.setValue(dislikeValue + 1);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );

        final Firebase orderRef = mFirebaseRef.child(key).child("order");
        orderRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        double orderValue;
                        //double orderValue = (double) dataSnapshot.getValue();
                        if (dataSnapshot.getValue() instanceof Long) {
                            orderValue = ((Long) (dataSnapshot.getValue())).doubleValue();
                        } else {
                            orderValue = (double) dataSnapshot.getValue();
                        }

                        Log.e("Order update:", "" + orderValue);

                        orderRef.setValue(orderValue + 1); //Need clarification, the higher value of order, the lower priorty sorted in firebase?
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );

        // Update SQLite DB
        dbutil.put(key);
    }

    public void setSearchResult(TextView view, String searchStr, int count)
    {
        if (count >= 1)
            view.setText(Html.fromHtml("There are " + count + " result(s) for the search \"" + searchStr + "\"."));
        else
            view.setText("No results are found.");
    }
    public void Close(View view) {
        finish();
    }
}
