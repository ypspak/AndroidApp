package hk.ust.cse.hunkim.questionroom;

import android.app.ListActivity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
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

public class MainActivity extends ListActivity {

    // TODO: change this to your own Firebase URL
    private static final String FIREBASE_URL = "https://cmkquestionsdb.firebaseio.com/";
    public static final String ROOM_NAME = "ROOM_NAME";

    private String roomName;
    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    private QuestionListAdapter mChatListAdapter;

    private DBUtil dbutil;

    public DBUtil getDbutil() {
        return dbutil;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialized once with an Android context.
        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        assert (intent != null);

        // Make it a bit more reliable
        roomName = intent.getStringExtra(JoinActivity.ROOM_NAME);
        if (roomName == null || roomName.length() == 0) {
            roomName = "all";
        }

        setTitle("Room name: " + roomName);
        // Setup our Firebase mFirebaseRef
        mFirebaseRef = new Firebase(FIREBASE_URL).child(roomName).child("questions");

        ImageButton postQ = (ImageButton) findViewById(R.id.postQuestion);
        postQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PostQuestion.class);
                intent.putExtra(ROOM_NAME, getRoomName());
                startActivity(intent);
            }
        });

        // get the DB Helper
        DBHelper mDbHelper = new DBHelper(this);
        dbutil = new DBUtil(mDbHelper);
    }

    @Override
    public void onStart() {
        super.onStart();

        //GUI design initialization <26/10/2015 by Peter Yeung>
        //Set the roomText located at the top of the screen, showing which room they are currently in
        Button closeButton = (Button) findViewById(R.id.close);
        closeButton.setText("◀ Room: " + roomName);
        //This is due to Android default, all buttons are come with capitalized.
        Button quitButton = (Button) findViewById(R.id.close);
        quitButton.setTransformationMethod(null);
        //Set the header color
        LinearLayout header = (LinearLayout) findViewById(R.id.listHeader);
        header.setBackgroundColor(getResources().getColor(R.color.HeaderGrey)); //Get the color through getResource class

        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = getListView();
        // Tell our list adapter that we only want 200 messages at a time
        mChatListAdapter = new QuestionListAdapter(
                mFirebaseRef.orderByChild("timestamp").limitToFirst(200),
                this, R.layout.question, roomName);
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
                    Toast.makeText(MainActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
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
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mChatListAdapter.cleanup();
    }

    private String getRoomName(){
        return roomName;
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
                        Long likeValue = (Long) dataSnapshot.getValue();
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
                        double orderValue = (double) dataSnapshot.getValue();
                        Log.e("Order update:", "" + orderValue);

                        orderRef.setValue(orderValue - 1);
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
                        Long dislikeValue = (Long) dataSnapshot.getValue();
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
                        double orderValue = (double) dataSnapshot.getValue();
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

    public void Close(View view) {
        finish();
    }
}
