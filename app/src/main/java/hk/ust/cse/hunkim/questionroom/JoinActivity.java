package hk.ust.cse.hunkim.questionroom;

import android.app.Dialog;
import android.support.v7.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import hk.ust.cse.hunkim.questionroom.room.Room;


/**
 * A login screen that offers login via email/password.
 */
public class JoinActivity extends AppCompatActivity implements ActionBar.TabListener {
    public static final String FIREBASE_URL = "https://cmkquestionsdb.firebaseio.com/";
    public static final String ROOM_NAME = "Room_name";

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    // Tab titles
    private String[] tabs = { "Join Room", "Room List", "Create Room" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_join);
         mFirebaseRef = new Firebase(FIREBASE_URL);
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    boolean connected = (Boolean) dataSnapshot.getValue();
                    if (connected) {
                        Toast.makeText(JoinActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(JoinActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });

        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(mAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (String tab_name : tabs) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(tab_name)
                            .setTabListener(this));
        }

    }

    public void tryJoin(final String roomName, final Room room){
        if(!room.getIsPrivate()){
            join(roomName);
        }
        else{
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.password_join_room_dialog);
            dialog.setTitle("Password Required");
            final EditText pwField = (EditText) dialog.findViewById(R.id.password);
            Button cancel= (Button) dialog.findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            Button submit= (Button) dialog.findViewById(R.id.submit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwField.setError(null);
                    String tempPw = pwField.getText().toString();
                    if(!room.getPassword().equals(tempPw)){
                        if(TextUtils.isEmpty(tempPw))
                            pwField.setError(getString(R.string.error_field_required));
                        else
                            pwField.setError(getString(R.string.error_incorrect_password));
                    }else{

                        dialog.dismiss();
                        join(roomName);
                    }
                }
            });
            dialog.show();
            return;
        }
    }

    private void join(String roomName){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(ROOM_NAME, roomName);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }
}

