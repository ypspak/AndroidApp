package hk.ust.cse.hunkim.questionroom;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


/**
 * Created by hunkim on 7/15/15.
 * based on http://evgenii.com/blog/testing-activity-in-android-studio-tutorial-part-3/
 * and
 * http://developer.android.com/training/testing.html
 */
public class EnterRoomFragmentTest extends ActivityInstrumentationTestCase2<JoinActivity> {
    Intent mStartIntent;
    JoinActivity activity;
    private static final String RoomString_1 = "#AppTestcase1" + String.valueOf(new Date().getTime());
    private static final String RoomString_2 = "AppTestcase1" + String.valueOf(new Date().getTime());
    private static final int TIMEOUT_IN_MS = 5000;
    public EditText roomNamefield;
    public Button joinRoom;

    public EnterRoomFragmentTest() {
        super(JoinActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mStartIntent = new Intent(Intent.ACTION_MAIN);
        mStartIntent.putExtra("ROOT_URL", "https://cmkquestionsdb.firebaseio.com/");
        setActivityIntent(mStartIntent);
        activity = getActivity();

        this.roomNamefield = (EditText) activity.findViewById(R.id.join_room_name);
        this.joinRoom = (Button) activity.findViewById(R.id.join_button);
    }

    public void testEnterRoom_Fail() throws Throwable {

        Instrumentation inst = new Instrumentation();
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(MainActivity.class.getName(), null, false);



        //Case 1
        TouchUtils.clickView(this, joinRoom);
        getInstrumentation().waitForIdleSync();
        MainActivity mainActivity = (MainActivity) receiverActivityMonitor
                .waitForActivityWithTimeout(TIMEOUT_IN_MS);
        assertNull("MainActivity should be null.", mainActivity);
        assertEquals("Room name field should ask for input", getActivity().getResources().getString(R.string.error_field_required), roomNamefield.getError());
        //Case 2
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                EditText roomNameField = (EditText) activity.findViewById(R.id.join_room_name);
                roomNameField.setText(RoomString_1);
            }
        });

        TouchUtils.clickView(this, joinRoom);
        getInstrumentation().waitForIdleSync();
        mainActivity = (MainActivity) receiverActivityMonitor
                .waitForActivityWithTimeout(TIMEOUT_IN_MS);
        assertNull("MainActivity should be null.", mainActivity);
        assertEquals("Room name field should say invalid input", getActivity().getResources().getString(R.string.error_invalid_room_name), roomNamefield.getError());

        //Case 3
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                EditText roomNameField = (EditText) activity.findViewById(R.id.join_room_name);
                roomNameField.setText(RoomString_2);
            }
        });

        TouchUtils.clickView(this, joinRoom);
        getInstrumentation().waitForIdleSync();
        mainActivity = (MainActivity) receiverActivityMonitor
                .waitForActivityWithTimeout(TIMEOUT_IN_MS);
        assertNull("MainActivity should be null.", mainActivity);
        assertEquals("Room name field should say not exist room.", getActivity().getResources().getString(R.string.error_not_exist_room), roomNamefield.getError());
    }
}