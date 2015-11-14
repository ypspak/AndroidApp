package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

/**
 * Created by CAI on 15/11/2015.
 */
public class CreateRoomActivity extends Activity {
    private EditText roomNameField;
    private EditText passwordField;
    private Button cancel;
    private Button createRoom;
    private CheckBox isPrivate;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        roomNameField = (EditText) findViewById(R.id.room_name);
        passwordField = (EditText) findViewById(R.id.password);
        cancel = (Button) findViewById(R.id.cancel_action);
        createRoom = (Button) findViewById(R.id.create_room);
        isPrivate = (CheckBox) findViewById(R.id.checkbox_isPrivate);
        passwordField.setEnabled(false);
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
                attemptCreateRoom();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Close(v);
            }
        });
    }

    public void attemptCreateRoom(){

    }

    public void Close(View view) {
        finish();
    }
}
