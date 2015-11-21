package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by CAI on 18/11/2015.
 */
public class WelcomeActivity extends Activity{
    public static final String FIREBASE_URL = "https://cmkquestionsdb.firebaseio.com/";

    private Button enter;
    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    private boolean isInFront;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_welcome);
        enter = (Button) findViewById(R.id.try_button);
        enter.setVisibility(View.INVISIBLE);
        mFirebaseRef = new Firebase(FIREBASE_URL);
        mConnectedListener = mFirebaseRef.child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    connected();
                } else {
                    waitingConnect();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        enter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent nextActivity = new Intent(v.getContext(),JoinActivity.class);
                nextActivity.putExtra("ROOT_URL", FIREBASE_URL);
                startActivity(nextActivity);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        isInFront = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isInFront = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private void connected(){
        if(this.isInFront=false){
            Toast.makeText(WelcomeActivity.this, "Reconnected", Toast.LENGTH_SHORT).show();
        }else{
            enter.setVisibility(View.VISIBLE);
        }
    }

    private void waitingConnect(){
        if(this.isInFront=false){
            Toast.makeText(WelcomeActivity.this, "Lost connection", Toast.LENGTH_SHORT).show();
        }else{

        }
    }
}
