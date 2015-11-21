package hk.ust.cse.hunkim.questionroom;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Test;
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

/*
public class JoinActivityTest extends ActivityInstrumentationTestCase2<JoinActivity> {
    Intent mStartIntent;
    JoinActivity activity;
    EditText roomNameEditText;
    Button joinButton;

    private static final int TIMEOUT_IN_MS = 5000;
    private static final int TIMEOUT_IN_MS_2 = 8000;
    public JoinActivityTest() {
        super(JoinActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mStartIntent = new Intent(Intent.ACTION_MAIN);
        mStartIntent.putExtra("ROOT_URL", "https://cmkquestionsdb.firebaseio.com/");
        setActivityIntent(mStartIntent);
        activity = getActivity();

    }

    public void testCreatingNonExistenceRoom() throws Throwable {

        Instrumentation inst = new Instrumentation();
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(MainActivity.class.getName(), null, false);

        EditText roomNameField = (EditText) activity.findViewById(R.id.room_name);
        Button joinRoom = (Button) activity.findViewById(R.id.join_button);

        //Case 1
        TouchUtils.clickView(this, joinRoom);
        MainActivity mainActivity = (MainActivity) receiverActivityMonitor
                .waitForActivityWithTimeout(TIMEOUT_IN_MS);
        assertNull("MainActivity should be null.", mainActivity);

        //Case 2
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                EditText roomNameField = (EditText) activity.findViewById(R.id.room_name);
                roomNameField.setText("#123");
            }
        });

        TouchUtils.clickView(this, joinRoom);
        mainActivity = (MainActivity) receiverActivityMonitor
                .waitForActivityWithTimeout(TIMEOUT_IN_MS);
        assertNull("MainActivity should be null.", mainActivity);

        //Case 3
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                EditText roomNameField = (EditText) activity.findViewById(R.id.room_name);
                roomNameField.setText("1234567832134578732134567");
            }
        });

        TouchUtils.clickView(this, joinRoom);
        mainActivity = (MainActivity) receiverActivityMonitor
                .waitForActivityWithTimeout(TIMEOUT_IN_MS);
        assertNull("MainActivity should be null.", mainActivity);

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
    }

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
//}
