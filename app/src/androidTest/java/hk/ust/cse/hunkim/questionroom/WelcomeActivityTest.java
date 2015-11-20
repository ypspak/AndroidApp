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
    int TIMEOUT_IN_MS = 5000;
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
    public void testExitActivity() throws Exception {

        Button enterButton = (Button) activity.findViewById(R.id.try_button);
        Instrumentation inst = new Instrumentation();
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(JoinActivity.class.getName(), null, false);

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TouchUtils.clickView(this, enterButton);

        JoinActivity joinActivity = (JoinActivity) receiverActivityMonitor
                .waitForActivityWithTimeout(TIMEOUT_IN_MS);

        assertNotNull("joinActivity should be not null.", joinActivity);
        getInstrumentation().removeMonitor(receiverActivityMonitor);
    }
}
