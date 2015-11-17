package hk.ust.cse.hunkim.questionroom;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import hk.ust.cse.hunkim.questionroom.room.Room;

/**
 * Created by CAI on 17/11/2015.
 */
public class JoinRoomFragment extends Fragment {
    // UI references.
    private EditText roomNameField;
    private Button joinRoom;
    private Dialog dialog;
    //Variable references
    private Firebase roomListRef;
    private Firebase roomsRef;
    private ValueEventListener checkExistenceListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_join_room, container, false);
        roomNameField = (EditText) rootView.findViewById(R.id.room_name);
        joinRoom = (Button) rootView.findViewById(R.id.join_button);
        roomListRef = new Firebase(JoinActivity.FIREBASE_URL).child("roomList");
        roomsRef = new Firebase(JoinActivity.FIREBASE_URL).child("rooms");
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        joinRoom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                attemptJoin(v);
            }
        });
    }

    @Override
    public void onStop() {
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
                    assert (dataSnapshot.getValue(Room.class) != null);
                    tryJoin(dataSnapshot.getKey(), dataSnapshot.getValue(Room.class) , view);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }

    private void tryJoin(final String roomName, final Room room, View v){
        if(!room.getIsPrivate()){
            join(v,roomName);
        }
        else{
            final Dialog dialog = new Dialog(getActivity());
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
        intent.putExtra(JoinActivity.ROOM_NAME, roomName);
        intent.putExtra(JoinActivity.FIREBASE_URL, roomsRef.toString());
        startActivity(intent);
    }

    private boolean isEmailValid(String room_name) {
        return !room_name.matches("^.*[^a-zA-Z0-9 ].*$");
    }
}
