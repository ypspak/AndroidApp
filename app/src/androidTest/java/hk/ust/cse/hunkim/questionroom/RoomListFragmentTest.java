package hk.ust.cse.hunkim.questionroom;

import android.app.Dialog;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.InstrumentationTestCase;
import android.test.TouchUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.firebase.client.Firebase;

import java.util.Date;

import hk.ust.cse.hunkim.questionroom.room.Room;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


/**
 * Created by hunkim on 7/15/15.
 * based on http://evgenii.com/blog/testing-activity-in-android-studio-tutorial-part-3/
 * and
 * http://developer.android.com/training/testing.html
 */
public class RoomListFragmentTest extends ActivityInstrumentationTestCase2<JoinActivity> {
    Intent mStartIntent;
    JoinActivity activity;
    private static final String roomBaseUrl = "https://ypspakclassroom.firebaseio.com";
    private static final String RoomString_1 = "AppTestcase_Room1" + String.valueOf(new Date().getTime());
    private static final String RoomString_2 = "AppTestcase_Room2" + String.valueOf(new Date().getTime());
    private static final String RoomString_3 = "AppTestcase_Room3" + String.valueOf(new Date().getTime());
    private static final int SHORT_TIMEOUT_IN_MS = 3000;
    private static final int TIMEOUT_IN_MS = 5000;
    private Firebase mFirebaseRef;
    public ListView room_list;

    public RoomListFragmentTest() {
        super(JoinActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();


        mStartIntent = new Intent(Intent.ACTION_MAIN);
        mStartIntent.putExtra("ROOT_URL", roomBaseUrl);

        setActivityIntent(mStartIntent);
        activity = getActivity();
        Firebase.setAndroidContext(activity.getBaseContext());
        mFirebaseRef = new Firebase(roomBaseUrl).child("roomList");

        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ViewPager pager = (ViewPager) activity.findViewById(R.id.pager);
                    pager.setCurrentItem(1);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


        this.room_list = (ListView) activity.findViewById(R.id.room_list);
    }

    @Override
    protected void tearDown() throws Exception{

        mFirebaseRef.child(RoomString_1).removeValue();
        mFirebaseRef.child(RoomString_2).removeValue();
        mFirebaseRef.child(RoomString_3).removeValue();
    }

    public void testRoomListAdapter_Counting() throws Throwable {

        Log.e("Entering", "testRoomListAdapter");

        Instrumentation inst = new Instrumentation();
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(MainActivity.class.getName(), null, false);

        Room room1 = new Room(false, "");
        Room room2 = new Room(true, "1234");
        Room room3 = new Room(true, "5678");

        Thread.sleep(SHORT_TIMEOUT_IN_MS);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listview = (ListView) getActivity().findViewById(R.id.room_list);
                Log.e("COUNT", String.valueOf(listview.getCount()));
                assertEquals("Number of room object should be 0 at initialization", 0, listview.getCount());

            }
        });

        //Test Add object
        mFirebaseRef.child(RoomString_1).setValue(room1);
        mFirebaseRef.child(RoomString_2).setValue(room2);
        mFirebaseRef.child(RoomString_3).setValue(room3);

        Thread.sleep(SHORT_TIMEOUT_IN_MS);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listview = (ListView) getActivity().findViewById(R.id.room_list);
                Log.e("COUNT", String.valueOf(listview.getCount()));
                assertEquals("Number of room object should be 3", 3, listview.getCount());

            }
        });

        //Test Delete object
        mFirebaseRef.child(RoomString_3).removeValue();

        Thread.sleep(SHORT_TIMEOUT_IN_MS);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listview = (ListView) getActivity().findViewById(R.id.room_list);
                Log.e("COUNT", String.valueOf(listview.getCount()));
                assertEquals("Number of room object should be 2", 2, listview.getCount());
                View listElement = listview.getChildAt(listview.getCount() - 1);
                assertNotNull("listElement should not be null", listElement);
                ImageView isPrivate_img = (ImageView) listElement.findViewById(R.id.is_private);
                assertTrue("isPrivate icon should be invisible", isPrivate_img.isShown());

            }
        });

        //Test Change object: Can be improved
        mFirebaseRef.child(RoomString_2).setValue(room1);

        Thread.sleep(SHORT_TIMEOUT_IN_MS);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listview = (ListView) getActivity().findViewById(R.id.room_list);
                View listElement = listview.getChildAt(listview.getCount() - 1);
                assertNotNull("listElement should not be null", listElement);
                ImageView isPrivate_img = (ImageView) listElement.findViewById(R.id.is_private);
                assertFalse("isPrivate icon should be invisible", isPrivate_img.isShown());
            }
        });

        //Clean up
        mFirebaseRef.child(RoomString_1).removeValue();
        mFirebaseRef.child(RoomString_2).removeValue();

        Log.e("Finishing", "testRoomListAdapter");

        getActivity().finish();
    }


    public void testRoomListAdapter_EnterPublicRoom_Success() throws Throwable {

        Log.e("Entering", "testRoomList_EnterPublicRoom");
        Instrumentation inst = new Instrumentation();
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(MainActivity.class.getName(), null, false);


        Room room1 = new Room(false, "");
        mFirebaseRef.child(RoomString_1).setValue(room1);

        //Transfer instrumentation test case
        Thread.sleep(SHORT_TIMEOUT_IN_MS);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listview = (ListView) getActivity().findViewById(R.id.room_list);
                assertEquals("Number of room object should be 1 at initialization", 1, listview.getCount());
                View listElement = listview.getChildAt(0);
                assertNotNull("listElement should not be null", listElement);
                ImageView isPrivate_img = (ImageView) listElement.findViewById(R.id.is_private);
                assertFalse("isPrivate icon should be invisible", isPrivate_img.isShown());
                Button Join_Button = (Button) listElement.findViewById(R.id.join_button);
                Join_Button.performClick();
            }
        });

        MainActivity mainActivity = (MainActivity) receiverActivityMonitor
                .waitForActivityWithTimeout(TIMEOUT_IN_MS);

        assertNotNull("ReceiverActivity is null", mainActivity);
        assertEquals("Activity is of wrong type", MainActivity.class,
                mainActivity.getClass());

        mFirebaseRef.child(RoomString_1).removeValue();

        mainActivity.finish();
        getInstrumentation().removeMonitor(receiverActivityMonitor);
        getInstrumentation().waitForIdleSync();

        Log.e("Finishing", "testRoomList_EnterPublicRoom");

        getActivity().finish();
    }

    public void testRoomListAdapter_EnterPrivateRoom_Fail() throws Throwable {

        Log.e("Entering", "testRoomList_EnterPrivateRoom_Fail");
        Room room1 = new Room(true, "123");
        mFirebaseRef.child(RoomString_1).setValue(room1);

        //Transfer instrumentation test case
        Thread.sleep(SHORT_TIMEOUT_IN_MS);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listview = (ListView) getActivity().findViewById(R.id.room_list);
                assertEquals("Number of room object should be 1 at initialization", 1, listview.getCount());
                View listElement = listview.getChildAt(0);
                assertNotNull("listElement should not be null", listElement);
                ImageView isPrivate_img = (ImageView) listElement.findViewById(R.id.is_private);
                assertTrue("isPrivate icon should be visible", isPrivate_img.isShown());
                Button Join_Button = (Button) listElement.findViewById(R.id.join_button);
                Join_Button.performClick();
            }
        });

        //Submit wrong password amd click cancel
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {

                EditText password = (EditText) getActivity().getDialog().findViewById(R.id.password);
                Button sumbit = (Button) getActivity().getDialog().findViewById(R.id.submit);
                Button cancel = (Button) getActivity().getDialog().findViewById(R.id.cancel);
                sumbit.performClick();
                assertEquals("Password field should ask for input", getActivity().getResources().getString(R.string.error_field_required), password.getError());
                password.setText("#123");
                sumbit.performClick();
                assertEquals("Password field should state for incorrect password.t", getActivity().getResources().getString(R.string.error_incorrect_password), password.getError());
                cancel.performClick();
            }
        });

        mFirebaseRef.child(RoomString_1).removeValue();

        getInstrumentation().waitForIdleSync();
        Log.e("Finishing", "testRoomList_EnterPrivateRoom_Fail");

        getActivity().finish();
    }

    public void testRoomListAdapter_EnterPrivateRoom_Success() throws Throwable {

        Log.e("Entering", "testRoomList_EnterPrivateRoom_Success");
        Instrumentation inst = new Instrumentation();
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(MainActivity.class.getName(), null, false);


        Room room1 = new Room(true, "123");
        mFirebaseRef.child(RoomString_1).setValue(room1);

        //Transfer instrumentation test case
        Thread.sleep(SHORT_TIMEOUT_IN_MS);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listview = (ListView) getActivity().findViewById(R.id.room_list);
                assertEquals("Number of room object should be 1 at initialization", 1, listview.getCount());
                View listElement = listview.getChildAt(0);
                assertNotNull("listElement should not be null", listElement);
                ImageView isPrivate_img = (ImageView) listElement.findViewById(R.id.is_private);
                assertTrue("isPrivate icon should be visible", isPrivate_img.isShown());
                Button Join_Button = (Button) listElement.findViewById(R.id.join_button);
                Join_Button.performClick();
            }
        });


        //Submit wrong password amd click cancel
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {

                EditText password = (EditText) getActivity().getDialog().findViewById(R.id.password);
                Button sumbit = (Button) getActivity().getDialog().findViewById(R.id.submit);
                Button cancel = (Button) getActivity().getDialog().findViewById(R.id.cancel);
                sumbit.performClick();
                assertEquals("Password field should ask for input", getActivity().getResources().getString(R.string.error_field_required), password.getError());
                password.setText("123");
                sumbit.performClick();
            }
        });

        MainActivity mainActivity = (MainActivity) receiverActivityMonitor
                .waitForActivityWithTimeout(TIMEOUT_IN_MS);

        assertNotNull("ReceiverActivity shoud not be null", mainActivity);
        assertEquals("Activity is of wrong type", MainActivity.class,
                mainActivity.getClass());

        mFirebaseRef.child(RoomString_1).removeValue();

        mainActivity.finish();
        getInstrumentation().removeMonitor(receiverActivityMonitor);
        getInstrumentation().waitForIdleSync();

        Log.e("Finishing", "testRoomList_EnterPrivateRoom_Success");

        getActivity().finish();
    }
}