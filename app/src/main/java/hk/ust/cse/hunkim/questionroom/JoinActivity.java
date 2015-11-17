package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


/**
 * A login screen that offers login via email/password.
 */
public class JoinActivity extends Activity {
    private static final String FIREBASE_URL = "https://cmkquestionsdb.firebaseio.com/";
    public static final String ROOM_NAME = "Room_name";
    // UI references.
    private EditText roomNameField;
    private Button joinRoom;
    private Button createRoom;
    private Button allRooms;
    private Dialog dialog;
    //Variable references
    private Firebase roomListRef;
    private ValueEventListener checkExistenceListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_join);

        roomNameField = (EditText) findViewById(R.id.room_name);
        joinRoom = (Button) findViewById(R.id.join_button);
        createRoom = (Button) findViewById(R.id.create_room);
        allRooms = (Button) findViewById(R.id.all_rooms);
        roomListRef = new Firebase(FIREBASE_URL).child("roomList");
    }

    @Override
    protected void onStart() {
        super.onStart();

        joinRoom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                attemptJoin(v);
            }
        });

        createRoom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CreateRoomActivity.class);
                startActivity(intent);
            }
        });

        allRooms.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RoomActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        String temp = roomNameField.getText().toString();
        if(checkExistenceListener!=null)
            roomListRef.child(temp).removeEventListener(checkExistenceListener);
    }


    public void attemptJoin(View view) {
        roomNameField.setError(null);

        // Store values at the time of the login attempt.
        String room_name = roomNameField.getText().toString();

        boolean cancel = false;

        // Check for a valid email address.
        if (TextUtils.isEmpty(room_name)) {
            roomNameField.setError(getString(R.string.error_field_required));

            cancel = true;
        } else if (!isEmailValid(room_name)) {
            roomNameField.setError(getString(R.string.error_invalid_room_name));
            cancel = true;
        }

        if (cancel) {
            roomNameField.setText("");
            roomNameField.requestFocus();
            return;
        }
        existRoomAndJoin(room_name, view);
    }

    private void existRoomAndJoin(String input, final View view){
        checkExistenceListener = roomListRef.child(input).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    roomNameField.setError(getString(R.string.error_not_exist_room));
                } else {
                    assert (dataSnapshot.child("ifPrivate").getValue() != null);
                    boolean tempBool = (boolean) dataSnapshot.child("ifPrivate").getValue();
                    assert (dataSnapshot.child("password").getValue() != null);
                    String tempString = (String) dataSnapshot.child("password").getValue();

                    tryJoin(dataSnapshot.getKey(), tempBool, tempString, view);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }

    private void tryJoin(final String roomName, boolean isPrivate, final String password, View v){
        if(!isPrivate){
            join(v,roomName);
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
                    if(!password.equals(tempPw)){
                        if(TextUtils.isEmpty(tempPw))
                            pwField.setError(getString(R.string.error_field_required));
                        else
                            pwField.setError(getString(R.string.error_incorrect_password));
                    }else{

                        dialog.dismiss();
                        join(v,roomName);
                    }
                }
            });
            dialog.show();
            return;
        }
    }

    private void join(View v, String roomName){
        Intent intent = new Intent(v.getContext(), MainActivity.class);
        intent.putExtra(ROOM_NAME, roomName);
        startActivity(intent);
    }

    private boolean isEmailValid(String room_name) {
        // http://stackoverflow.com/questions/8248277
        // Make sure alphanumeric characters
        return !room_name.matches("^.*[^a-zA-Z0-9 ].*$");
    }
}

