package hk.ust.cse.hunkim.questionroom;

import android.app.ListActivity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
    private ImageButton backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_roomlist);
        Intent intent = getIntent();

        listView = getListView();
        mFirebaseRef = new Firebase(FIREBASE_URL).child("roomList");
//        backBtn = (ImageButton) findViewById(R.id.gobackbtn);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        backBtn.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Close(view);
//                    }
//                }
//        );
        mRoomListAdapter = new RoomListAdapter(
                mFirebaseRef.orderByKey(),
                this, R.layout.room);

        listView.setAdapter(mRoomListAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void Close(View view) {
        finish();
    }
}
