package hk.ust.cse.hunkim.questionroom;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;

import org.junit.Test;

import java.util.Date;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


/**
 * Created by hunkim on 7/15/15.
 * based on http://evgenii.com/blog/testing-activity-in-android-studio-tutorial-part-3/
 * and
 * http://developer.android.com/training/testing.html
 */
public class CreateRoomFragmentTest extends ActivityInstrumentationTestCase2<JoinActivity> {

    Intent mStartIntent;
    JoinActivity activity;
    private static final String RoomString_1 = "AppTestcase1" + String.valueOf(new Date().getTime());
    private static final int SHORT_TIMEOUT = 2500;
    private static final String roomBaseUrl = "https://ypspakclassroom.firebaseio.com";
    public EditText Room;
    public Button CreateButton;
    public EditText Password;
    public CheckBox isPrivate;

    public CreateRoomFragmentTest() {
        super(JoinActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mStartIntent = new Intent(Intent.ACTION_MAIN);
        mStartIntent.putExtra("ROOT_URL", roomBaseUrl);

        setActivityIntent(mStartIntent);
        activity = getActivity();

        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ViewPager pager = (ViewPager) activity.findViewById(R.id.pager);
                    pager.setCurrentItem(2);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        Room = (EditText) activity.findViewById(R.id.create_room_name);
        CreateButton = (Button) activity.findViewById(R.id.create_room);
        Password = (EditText) activity.findViewById(R.id.create_password);
        isPrivate = (CheckBox) activity.findViewById(R.id.checkbox_isPrivate);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        Firebase mFirebaseRef = new Firebase(roomBaseUrl).child("roomList").child(RoomString_1);
        mFirebaseRef.removeValue();
    }

    public void testCreatePublicRoom_Fail_EmptyString() throws Throwable {

        Instrumentation inst = new Instrumentation();
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(MainActivity.class.getName(), null, false);

        final EditText Room = this.Room;
        final Button CreateButton = this.CreateButton;

        getInstrumentation().waitForIdleSync();

        //Case 1:Empty String
        Thread.sleep(SHORT_TIMEOUT);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                Room.setText("");
            }
        });
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, CreateButton);
        getInstrumentation().waitForIdleSync();
        assertEquals("It should be invalid room name", getActivity().getResources().getString(R.string.error_field_required), Room.getError());
    }

    public void testCreatePublicRoom_Fail_InvalidString() throws Throwable {

        Instrumentation inst = new Instrumentation();
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(MainActivity.class.getName(), null, false);

        final EditText Room = this.Room;
        final Button CreateButton = this.CreateButton;

        getInstrumentation().waitForIdleSync();
        //Case 2: Invalid String
        Thread.sleep(SHORT_TIMEOUT);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                Room.requestFocus();
            }
        });

        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("#123");
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, CreateButton);
        getInstrumentation().waitForIdleSync();

        assertEquals("It should state invalid room name", getActivity().getResources().getString(R.string.error_invalid_room_name), Room.getError());
    }


    public void testCreatePrivateRoom_Fail_EmptyPassword() throws Throwable {

        final EditText Room = this.Room;
        final EditText Password = this.Password;
        final CheckBox isPrivate = this.isPrivate;
        final Button CreateButton = this.CreateButton;

        Instrumentation inst = new Instrumentation();
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(MainActivity.class.getName(), null, false);

        getInstrumentation().waitForIdleSync();

        //Case 1: Invalid Password
        Thread.sleep(SHORT_TIMEOUT);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                isPrivate.setChecked(true);
                Room.setText(RoomString_1);
            }
        });
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, CreateButton);
        assertTrue(RoomString_1.equals(Room.getText().toString()));
        assertTrue("".equals(Password.getText().toString()));
    }


    public void testCreatePrivateRoom_Fail_DuplicateRoom() throws Throwable {

        final EditText Room = this.Room;
        final EditText Password = this.Password;
        final CheckBox isPrivate = this.isPrivate;
        final Button CreateButton = this.CreateButton;

        Instrumentation inst = new Instrumentation();
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(MainActivity.class.getName(), null, false);

        getInstrumentation().waitForIdleSync();

        Thread.sleep(SHORT_TIMEOUT);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                isPrivate.setChecked(true);
                Room.setText(RoomString_1);
                Password.setText("#!@:!");
            }
        });
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, CreateButton);
        assertTrue("Room field should be cleared", "".equals(Room.getText().toString()));
        assertTrue("Password field should be cleared", "".equals(Password.getText().toString()));

        Thread.sleep(SHORT_TIMEOUT);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                isPrivate.setChecked(true);
                Room.setText(RoomString_1);
                Password.setText("!!!!!!!");
            }
        });
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, CreateButton);
        assertEquals("It should state invalid room name", getActivity().getResources().getString(R.string.error_exist_room), Room.getError());
    }

    public void testCreatePrivateRoom_Success_validPassword() throws Throwable {

        final EditText Room = this.Room;
        final EditText Password = this.Password;
        final CheckBox isPrivate = this.isPrivate;
        final Button CreateButton = this.CreateButton;

        Instrumentation inst = new Instrumentation();
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(MainActivity.class.getName(), null, false);

        getInstrumentation().waitForIdleSync();

        Thread.sleep(SHORT_TIMEOUT);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                isPrivate.setChecked(true);
                Room.setText(RoomString_1);
                Password.setText("#!@:!");
            }
        });
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, CreateButton);
        assertTrue("Room field should be cleared", "".equals(Room.getText().toString()));
        assertTrue("Password field should be cleared", "".equals(Password.getText().toString()));
        //assertEquals("Room field should be cleared", "", Room.getText());
        //assertEquals("Password should be cleared", "", Password.getText());
    }

    public void testCreatePublicRoom_Success_validPassword() throws Throwable {

        final EditText Room = this.Room;
        final Button CreateButton = this.CreateButton;

        Instrumentation inst = new Instrumentation();
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(MainActivity.class.getName(), null, false);

        getInstrumentation().waitForIdleSync();

        Thread.sleep(SHORT_TIMEOUT);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                Room.setText(RoomString_1);
            }
        });
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, CreateButton);
        assertTrue("Room field should be cleared", "".equals(Room.getText().toString()));
    }

    public void testCreateRoomButton() throws Throwable {

        final EditText Room = this.Room;
        final EditText Password = this.Password;
        final CheckBox isPrivate = this.isPrivate;
        final Button CreateButton = this.CreateButton;

        Instrumentation inst = new Instrumentation();
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(MainActivity.class.getName(), null, false);

        getInstrumentation().waitForIdleSync();

        Thread.sleep(SHORT_TIMEOUT);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                isPrivate.setChecked(true);
            }
        });

        getInstrumentation().waitForIdleSync();
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                isPrivate.setChecked(false);
            }
        });
        getInstrumentation().waitForIdleSync();
        assertFalse("Checkbox should be disabled", isPrivate.isChecked());
    }




/*
    public void testCreatingActivity_EmptyString() throws Exception {

        //Create and add an ActivityMonitor to monitor interaction between the system and the
        //ReceiverActivity
        Instrumentation inst = new Instrumentation();
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(MainActivity.class.getName(), null, false);

        //Request focus on the EditText field. This must be done on the UiThread because?
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                roomNameEditText.requestFocus();
            }
        });
        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();


        //Send the room name
        //Case 1:Empty string
        getInstrumentation().sendStringSync("");
        getInstrumentation().waitForIdleSync();
        String actualText = roomNameEditText.getText().toString();
        assertTrue(actualText.isEmpty());
        //Click on the sendToReceiverButton to send the message to ReceiverActivity
        TouchUtils.clickView(this, joinButton);

        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        //Wait until MainActivity was launched and get a reference to it.
        MainActivity mainActivity = (MainActivity) receiverActivityMonitor
                .waitForActivityWithTimeout(TIMEOUT_IN_MS);

        //Verify that MainActivity was started
        assertNull("ReceiverActivity is not null", mainActivity);
        assertEquals("Monitor for MainActivity has  been called", 0,
                receiverActivityMonitor.getHits());

        //Unregister monitor for ReceiverActivity
        getInstrumentation().removeMonitor(receiverActivityMonitor);
        getInstrumentation().waitForIdleSync();
    }

    public void testCreatingActivity_InvalidString() throws Exception {

        //Create and add an ActivityMonitor to monitor interaction between the system and the
        //ReceiverActivity
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(MainActivity.class.getName(), null, false);

        //Request focus on the EditText field. This must be done on the UiThread because?
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                roomNameEditText.requestFocus();
            }
        });
        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();
        Thread.sleep(TIMEOUT_IN_MS);
        //Send the room name
        //Case 2: Invalid Symbol
        getInstrumentation().sendStringSync("@#$%");
        getInstrumentation().waitForIdleSync();

        //Some hacks here
        String actualText = roomNameEditText.getText().toString();
        while (actualText.isEmpty())
            actualText = roomNameEditText.getText().toString();

        assertEquals("@#$%", actualText);
        //Click on the sendToReceiverButton to send the message to ReceiverActivity
        TouchUtils.clickView(this, joinButton);

        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        //Wait until MainActivity was launched and get a reference to it.
        MainActivity mainActivity = (MainActivity) receiverActivityMonitor
                .waitForActivityWithTimeout(TIMEOUT_IN_MS);

        //Verify that MainActivity was started
        assertNull("ReceiverActivity is not null", mainActivity);
        assertEquals("Monitor for MainActivity has not been called", 0,
                receiverActivityMonitor.getHits());

        //Unregister monitor for ReceiverActivity
        getInstrumentation().removeMonitor(receiverActivityMonitor);
        getInstrumentation().waitForIdleSync();
    }*/
/*
    public void testCreatingActivity_ValidString() throws Exception {

        //Create and add an ActivityMonitor to monitor interaction between the system and the
        //ReceiverActivity
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(MainActivity.class.getName(), null, false);

        //Request focus on the EditText field. This must be done on the UiThread because?
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                roomNameEditText.requestFocus();
            }
        });
        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        //Send the room name
        //Case 3: Valid String
        Thread.sleep(2000);
        getInstrumentation().sendStringSync("TestCase");
        getInstrumentation().waitForIdleSync();

        //Some hacks here
        String actualText = roomNameEditText.getText().toString();
        while (actualText.isEmpty())
            actualText = roomNameEditText.getText().toString();

        assertEquals("TestCase", actualText);
        //Click on the sendToReceiverButton to send the message to ReceiverActivity
        TouchUtils.clickView(this, joinButton);

        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        //Wait until MainActivity was launched and get a reference to it.
        MainActivity mainActivity = (MainActivity) receiverActivityMonitor
                .waitForActivityWithTimeout(TIMEOUT_IN_MS);

        //Verify that MainActivity was started
        assertNotNull("ReceiverActivity is null", mainActivity);
        assertEquals("Monitor for MainActivity has not been called", 1,
                receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", MainActivity.class,
                mainActivity.getClass());

        Intent intent = mainActivity.getIntent();
        assertNotNull("Intent should be set", intent);
        assertEquals("TestCase", intent.getStringExtra(JoinActivity.ROOM_NAME));
        assertEquals("This is set correctly", "Room name: TestCase", mainActivity.getTitle());

        //Unregister monitor for ReceiverActivity
        getInstrumentation().removeMonitor(receiverActivityMonitor);
        getInstrumentation().waitForIdleSync();
    }*/
}
