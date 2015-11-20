package hk.ust.cse.hunkim.questionroom;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import org.junit.Test;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


/**
 * Created by hunkim on 7/15/15.
 * based on http://evgenii.com/blog/testing-activity-in-android-studio-tutorial-part-3/
 * and
 * http://developer.android.com/training/testing.html
 */
public class WelcomeActivityTest extends ActivityInstrumentationTestCase2<WelcomeActivity> {

    Intent mStartIntent;
    WelcomeActivity activity;
    private static final int TIMEOUT_IN_MS = 5000;
    public WelcomeActivityTest() {
        super(WelcomeActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mStartIntent = new Intent(Intent.ACTION_MAIN);
        mStartIntent.putExtra("ROOT_URL", "https://cmkquestionsdb.firebaseio.com/");
        setActivityIntent(mStartIntent);
        activity = getActivity();
    }

    @MediumTest
    public void testWelcomeActivity_Enter() throws Exception {

        Button enterButton = (Button) activity.findViewById(R.id.try_button);
        Instrumentation inst = new Instrumentation();
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(JoinActivity.class.getName(), null, false);

        Thread.sleep(TIMEOUT_IN_MS);

        TouchUtils.clickView(this, enterButton);

        JoinActivity joinActivity = (JoinActivity) receiverActivityMonitor
                .waitForActivityWithTimeout(TIMEOUT_IN_MS);

        assertNotNull("joinActivity should be not null.", joinActivity);
        getInstrumentation().removeMonitor(receiverActivityMonitor);
    }

    /*public void testWelcomeActivity_Exit() throws Throwable {

        Button enterButton = (Button) activity.findViewById(R.id.try_button);

        Instrumentation inst = new Instrumentation();
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(JoinActivity.class.getName(), null, false);

        final WelcomeActivity runActivity = activity;
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                runActivity.onBackPressed();
            }
        });

        TouchUtils.clickView(this, enterButton);

        JoinActivity joinActivity = (JoinActivity) receiverActivityMonitor
                .waitForActivityWithTimeout(TIMEOUT_IN_MS);

        assertNull("joinActivity should be null.", joinActivity);
        assertNotNull("welcomeActivity should still not be null.", activity);
        getInstrumentation().removeMonitor(receiverActivityMonitor);
    }*/
}
