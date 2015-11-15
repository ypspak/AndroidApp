package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import hk.ust.cse.hunkim.questionroom.room.Room;

/**
 * Created by CAI on 15/11/2015.
 */
public class CreateRoomActivity extends Activity {
    private static final String FIREBASE_URL = "https://cmkquestionsdb.firebaseio.com/";

    private EditText roomNameField;
    private EditText passwordField;
    private Button cancel;
    private Button createRoom;
    private CheckBox isPrivate;
    private Firebase roomListRef;
    private ValueEventListener checkExistenceListener;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_create_room);


        roomNameField = (EditText) findViewById(R.id.room_name);
        passwordField = (EditText) findViewById(R.id.password);
        cancel = (Button) findViewById(R.id.cancel_action);
        createRoom = (Button) findViewById(R.id.create_room);
        isPrivate = (CheckBox) findViewById(R.id.checkbox_isPrivate);
        passwordField.setEnabled(false);
        roomListRef = new Firebase(FIREBASE_URL).child("roomList");
    }

    @Override
    protected void onStart() {
        super.onStart();
        isPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordField.setEnabled(true);
                } else {
                    passwordField.setEnabled(false);
                    passwordField.setText(null);
                }
            }
        });



        createRoom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                attemptCreateRoom(v);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Close(v);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        String temp = roomNameField.getText().toString();
        roomListRef.child(temp).removeEventListener(checkExistenceListener);
    }

    private void attemptCreateRoom(final View v){
        if(!isValidInput(roomNameField)) return;
        if(isPrivate.isChecked()){
            if(!isValidInput(passwordField))
                return;
        }
        roomNameField.setError(null);
        String input = roomNameField.getText().toString();
        checkExistenceListener = roomListRef.child(input).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    roomNameField.setError(getString(R.string.error_exist_room));
                } else {
                    roomNameField.setError(null);
                    CreateRoom();
                    Close(v);
                    Toast.makeText(CreateRoomActivity.this, "The room is successfully created!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }

    private boolean isValidInput(EditText editView){
        editView.setError(null);
        String input = editView.getText().toString();
        if(TextUtils.isEmpty(input)){
            editView.setError(getString(R.string.error_field_required));
            return false;
        }

        if (editView.getId()==R.id.room_name) {
            if(!isEmailValid(input)){
                editView.setError(getString(R.string.error_invalid_room_name));
                return false;
            }
        }
        return true;
    }

    private void CreateRoom(){
        String roomName = roomNameField.getText().toString();
        String pw = passwordField.getText().toString();
        Firebase roomRef = roomListRef.child(roomName);

        Room roomToAdd = new Room(isPrivate.isChecked(), pw);
        roomRef.setValue(roomToAdd);
    }

    private boolean isEmailValid(String input) {
        return !input.matches("^.*[^a-zA-Z0-9 ].*$");
    }

    public void Close(View view) {
        finish();
    }
}
