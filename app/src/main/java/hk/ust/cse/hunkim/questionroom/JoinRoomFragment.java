package hk.ust.cse.hunkim.questionroom;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
    //Variable references
    private String baseUrl;
    private Firebase roomListRef;
    private Firebase roomsRef;

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

        baseUrl = ((JoinActivity)rootView.getContext()).getBaseUrl();
        roomListRef = new Firebase(baseUrl).child("roomList");
        roomsRef = new Firebase(baseUrl).child("rooms");

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        joinRoom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                attemptJoin(v);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
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
        roomListRef.child(input).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    roomNameField.setError(getString(R.string.error_not_exist_room));
                } else {
                    assert (dataSnapshot.getValue(Room.class) != null);
                    ((JoinActivity) view.getContext()).tryJoin(dataSnapshot.getKey(), dataSnapshot.getValue(Room.class));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }

    private boolean isEmailValid(String room_name) {
        return !room_name.matches("^.*[^a-zA-Z0-9 ].*$");
    }
}
