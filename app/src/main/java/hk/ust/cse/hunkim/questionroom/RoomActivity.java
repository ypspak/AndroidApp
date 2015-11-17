package hk.ust.cse.hunkim.questionroom;

import android.app.ListActivity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.firebase.client.Firebase;

/**
 * Created by CAI on 17/11/2015.
 */
public class RoomActivity extends ListActivity {
    private static final String FIREBASE_URL = "https://cmkquestionsdb.firebaseio.com/";

    private Firebase mFirebaseRef;
    private ListView listView;
    private RoomListAdapter mRoomListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.fragment_room_list);
        Intent intent = getIntent();

        listView = getListView();
        mFirebaseRef = new Firebase(FIREBASE_URL).child("roomList");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRoomListAdapter = new RoomListAdapter(
                mFirebaseRef.orderByKey(),
                this, R.layout.room);

        listView.setAdapter(mRoomListAdapter);

        mRoomListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mRoomListAdapter.getCount() - 1);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRoomListAdapter.cleanup();
    }

    public void Close(View view) {
        finish();
    }
}
