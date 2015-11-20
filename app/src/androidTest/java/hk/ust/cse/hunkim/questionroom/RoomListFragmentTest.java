package hk.ust.cse.hunkim.questionroom;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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
    private RoomListAdapter mRoomListAdapter;
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

    public void testRoomListCount() throws Throwable {

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

            }
        });

        //Test Change object: Can be improved
        mFirebaseRef.child(RoomString_2).setValue(room1);

        Thread.sleep(SHORT_TIMEOUT_IN_MS);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listview = (ListView) getActivity().findViewById(R.id.room_list);
                Log.e("COUNT", String.valueOf(listview.getCount()));
                assertEquals("Number of room object should be 2", 2, listview.getCount());

            }
        });

        //Clean up
        mFirebaseRef.child(RoomString_1).removeValue();
        mFirebaseRef.child(RoomString_2).removeValue();
    }
}