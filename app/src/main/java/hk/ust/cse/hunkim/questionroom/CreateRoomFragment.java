package hk.ust.cse.hunkim.questionroom;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
 * Created by CAI on 17/11/2015.
 */
public class CreateRoomFragment extends Fragment {
    // UI references.
    private EditText roomNameField;
    private EditText passwordField;
    private Button createRoom;
    private CheckBox isPrivate;
    //Variable references
    private String baseUrl;
    private Firebase roomListRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_create_room, container, false);

        roomNameField = (EditText) rootView.findViewById(R.id.create_room_name);
        passwordField = (EditText) rootView.findViewById(R.id.create_password);
        createRoom = (Button) rootView.findViewById(R.id.create_room);
        isPrivate = (CheckBox) rootView.findViewById(R.id.checkbox_isPrivate);

        baseUrl = ((JoinActivity)rootView.getContext()).getBaseUrl();
        roomListRef = new Firebase(baseUrl).child("roomList");

        passwordField.setEnabled(false);

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        isPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordField.setEnabled(true);
                    isPrivate.setTextColor(getResources().getColor(R.color.HeaderColor));
                    passwordField.setHintTextColor(getResources().getColor(R.color.Join_Hint_Color));
                } else {
                    passwordField.setEnabled(false);
                    passwordField.setText(null);
                    isPrivate.setTextColor(getResources().getColor(R.color.Join_Disable_Color));
                    passwordField.setHintTextColor(getResources().getColor(R.color.Join_Disable_Color));
                }
            }
        });



        createRoom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                attemptCreateRoom(v);
            }
        });
    }

    //function will triggered if click the createRoom button
    private void attemptCreateRoom(final View v){
        if(!isValidInput(roomNameField)) return;
        if(isPrivate.isChecked()){
            if(!isValidInput(passwordField))
                return;
        }
        roomNameField.setError(null);
        String input = roomNameField.getText().toString();
        roomListRef.child(input).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    roomNameField.setError(getString(R.string.error_exist_room));
                } else {
                    CreateRoom();
                    roomNameField.setText("");
                    passwordField.setText("");
                    InputMethodManager inputManager =
                            (InputMethodManager) getActivity().
                                    getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(
                            getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    Toast.makeText(getActivity(), "The room is successfully created!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }

    //sub function of attemptCreateRoom, checking if the input is valide for create a room
    private boolean isValidInput(EditText editView){
        editView.setError(null);
        String input = editView.getText().toString();
        if(TextUtils.isEmpty(input)){
            editView.setError(getString(R.string.error_field_required));
            return false;
        }

        if (editView.getId()==R.id.create_room_name) {
            if(!isEmailValid(input)){
                editView.setError(getString(R.string.error_invalid_room_name));
                return false;
            }
        }
        return true;
    }

    //sub function of attemptCreateRoom, checking if the input only have alpha / number
    private boolean isEmailValid(String input) {
        return !input.matches("^.*[^a-zA-Z0-9 ].*$");
    }

    //sub function of attemptCreateRoom, really create a room
    private void CreateRoom(){
        String roomName = roomNameField.getText().toString();
        String pw = passwordField.getText().toString();
        Firebase roomRef = roomListRef.child(roomName);

        Room roomToAdd = new Room(isPrivate.isChecked(), pw);
        roomRef.setValue(roomToAdd);
    }
}
