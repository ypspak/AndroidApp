package hk.ust.cse.hunkim.questionroom;

import android.app.ListActivity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
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
import hk.ust.cse.hunkim.questionroom.reply.Reply;

public class SearchMainActivity extends ListActivity {

    // TODO: change this to your own Firebase URL

    public static final String ROOM_NAME = "ROOM_NAME"; //This is used as VARIABLE name for sending value of variable through intent, i.e. the left part of Map<string, int/string/double>
    public static final String m_FirebaseURL = "FIREBASE_URL"; //This is used as VARIABLE name for sending value of variable through intent
    public static final String INPUT = "INPUT";
    private String roomName;
    private String Firebase_URL;
    private Firebase mFirebaseRef;
    private EditText searchText;
    private ImageButton searchButton;
    private ValueEventListener mConnectedListener;
    private HashtagListAdapter mHashtagListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialized once with an Android context.
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_search_main);
        Intent intent = getIntent();

        roomName = intent.getStringExtra(MainActivity.ROOM_NAME);
        Firebase_URL = intent.getStringExtra(MainActivity.m_FirebaseURL);
        mFirebaseRef = new Firebase(Firebase_URL).child("rooms").child(roomName).child("tags");

    }

    @Override
    public void onStart() {
        super.onStart();

        //GUI design initialization <26/10/2015 by Peter Yeung>
        //This is due to Android default, all buttons are come with capitalized.
        Button quitButton = (Button) findViewById(R.id.close);
        quitButton.setTransformationMethod(null);
        searchText = (EditText) findViewById(R.id.searchInput);
        searchText.setError(null);

        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = getListView();
        // Tell our list adapter that we only want 200 messages at a time
        mHashtagListAdapter = new HashtagListAdapter(
                mFirebaseRef.orderByChild("used").limitToFirst(10),
                this, R.layout.hashtag_search, roomName);
        listView.setAdapter(mHashtagListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, show a toast with the TextView text or do whatever you need.
                searchText.setText(((TextView) view.findViewById(R.id.name)).getText());
                searchButton.performClick();
                //Log.e("POSITION", "Detect pressed and position is " + String.valueOf(position) + "with id = " + String.valueOf(id));
                //Log.e("POSITION", "Its hashtag is = " + ((TextView) view.findViewById(R.id.name)).getText());
            }
        });

        mHashtagListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mHashtagListAdapter.getCount() - 1);
            }
        });


        // Finally, a little indication of connection status
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(SearchMainActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SearchMainActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
            });

        searchButton = (ImageButton) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String input = searchText.getText().toString();
                        if (!TextUtils.isEmpty(input)) { //todo: limitation on length of title, more outcome for preventing html attack for Q title
                            // Before creating our 'model', we have to replace substring so that prevent code injection
                            input = input.replace("<", "&lt;");
                            input = input.replace(">", "&gt;");
                            Intent intent = new Intent(view.getContext(), SearchResultActivity.class);
                            intent.putExtra(ROOM_NAME, roomName);
                            intent.putExtra(m_FirebaseURL, Firebase_URL);
                            intent.putExtra(INPUT, input);
                            view.getContext().startActivity(intent);
                        } else {
                            searchText.setError(getString(R.string.error_field_required));
                        }//warning to force user input reply
                    }
                }
        );
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
