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

public class SearchResultActivity extends ListActivity {
    private String searchInput;
    private String roomName;
    private String roomBaseUrl;
    private Firebase mFirebaseRef;
    private QuestionListAdapter mChatListAdapter;
    private int sortIndex;

    private DBUtil dbutil;

    public DBUtil getDbutil() {
        return dbutil;
    }

    public int getSortIndex(){return sortIndex;}

    public void setSortIndex(int i){sortIndex = i;}

    public String getRoomName(){return roomName;}

    public String getRoomBaseUrl(){return roomBaseUrl;}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialized once with an Android context.
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_search_result);
        Intent intent = getIntent();

        roomName = intent.getStringExtra("ROOM_NAME");
        searchInput = intent.getStringExtra("SEARCH_INPUT");
        roomBaseUrl = intent.getStringExtra("ROOM_BASE_URL");

        this.sortIndex = 1;
        mFirebaseRef = new Firebase(roomBaseUrl).child("questions");

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
                this, R.layout.question_search, searchInput);
        listView.setAdapter(mChatListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String input = ((TextView) view.findViewById(R.id.name)).getText().toString();
                //EnterSearchResult(view, input);
                ImageButton replyButton = (ImageButton) view.findViewById(R.id.QuestionReply);
                replyButton.performClick();
            }
        });
        //The list is already formed
        ((TextView) findViewById(R.id.searchResult)).setText(R.string.search_no_result);
        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                setSearchResult((TextView) findViewById(R.id.searchResult), searchInput, mChatListAdapter.getCount()); //This is the base case for having results.
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
                        Log.e("Like update:", "" + likeValue);

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
                        Log.e("Dislike update:", "" + dislikeValue);

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

    public void setSearchResult(TextView view, String searchStr, int count)
    {
            view.setText(Html.fromHtml("There are " + count + " result(s) for the search \"" + searchStr + "\"."));
    }
    public void Close(View view) {
        finish();
    }
}
