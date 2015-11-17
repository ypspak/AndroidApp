//package hk.ust.cse.hunkim.questionroom;
//
//import android.app.Instrumentation;
//import android.content.Intent;
//import android.test.ActivityInstrumentationTestCase2;
//import android.test.TouchUtils;
//import android.test.suitebuilder.annotation.MediumTest;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import java.util.Date;
//
///**
// * Created by hunkim on 7/20/15.
// */
//public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
//
//    MainActivity activity;
//    Intent mStartIntent;
//    ImageButton mButton;
//    Button mSendQuestion;
//    Button mCancel;
//    TextView mTitle;
//    TextView mBody;
//    Long timestamp = new Date().getTime();
//    Integer mCounter = 0;
//    String RoomString = "AppTestcase:" + String.valueOf(timestamp);;
//    String FIREBASE_URL = "https://cmkquestionsdb.firebaseio.com/";
//
//    public MainActivityTest() {
//        super(MainActivity.class);
//    }
//
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//
//        mStartIntent = new Intent(Intent.ACTION_MAIN);
//        //timestamp = new Date().getTime();
//        //RoomString = "Testcase:" + String.valueOf(timestamp);
//        mStartIntent.putExtra(JoinActivity.ROOM_NAME, RoomString);
//        setActivityIntent(mStartIntent);
//        activity = getActivity();
//        mButton = (ImageButton) getActivity().findViewById(R.id.postQuestion);
//    }
//
//
//    @MediumTest
//    public void testPostingMessage_ValidString_Like() throws Exception {
//
//        Instrumentation inst = new Instrumentation();
//        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
//                .addMonitor(PostQuestion.class.getName(), null, false);
//
//        final ListView lView = getActivity().getListView();
//
//        TouchUtils.clickView(this, mButton);
//
//        PostQuestion postQuestion = (PostQuestion) receiverActivityMonitor
//                .waitForActivityWithTimeout(5000);
//
//        mSendQuestion = (Button) postQuestion.findViewById(R.id.PostQuestion);
//        mCancel = (Button) postQuestion.findViewById(R.id.Cancel);
//        mTitle = (TextView) postQuestion.findViewById(R.id.QuestionTitle);
//        mBody = (TextView) postQuestion.findViewById(R.id.QuestionBody);
//
//
//        //Verify that MainActivity was started
//        assertNotNull("mSendQuestion is null", mSendQuestion);
//        assertNotNull("mCancel is null", mCancel);
//        assertNotNull("mTitle is null", mTitle);
//        assertNotNull("mBody is null", mBody);
//        assertNotNull("ReceiverActivity is null", postQuestion);
//        assertEquals("Monitor for postQuestion has been called", 1,
//                receiverActivityMonitor.getHits());
//
//        //Test case 1: Normal String
//        getInstrumentation().runOnMainSync(new Runnable() {
//            @Override
//            public void run() {
//                mTitle.requestFocus();
//            }
//        });
//        getInstrumentation().waitForIdleSync();
//        getInstrumentation().sendStringSync("<Like message>");
//        getInstrumentation().waitForIdleSync();
//
//        String actualText = mTitle.getText().toString();
//        while (actualText.isEmpty())
//            actualText = mTitle.getText().toString();
//        assertEquals("<Like message>", actualText);
//
//        TouchUtils.clickView(this, mSendQuestion);
//
//        //This part should be separated by a new test
//        View listElement = lView.getChildAt(lView.getCount() - 1);
//        assertNotNull(listElement);
//
//        ImageButton mLikeButton = (ImageButton) listElement.findViewById(R.id.QuestionLike);
//        ImageButton mDislikeButton = (ImageButton) listElement.findViewById(R.id.QuestionDislike);
//
//        //Should exist
//        assertNotNull(mLikeButton);
//        assertNotNull(mDislikeButton);
//        Thread.sleep(1000);
//        //Click like button
//        TouchUtils.clickView(this, mLikeButton);
//        //Should be not clickable now
//        assertFalse(mLikeButton.isClickable());
//        assertFalse(mDislikeButton.isClickable());
//        //Try to update like/dislike by function call instead
//        activity.updateLike((String) listElement.getTag());
//        activity.updateDislike((String) listElement.getTag());
//        getInstrumentation().removeMonitor(receiverActivityMonitor);
//    }
//
//@MediumTest
//    public void testPostingMessage_ValidString_Dislike() throws Exception {
//
//        Instrumentation inst = new Instrumentation();
//        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
//                .addMonitor(PostQuestion.class.getName(), null, false);
//
//        final ListView lView = getActivity().getListView();
//
//        TouchUtils.clickView(this, mButton);
//
//        PostQuestion postQuestion = (PostQuestion) receiverActivityMonitor
//                .waitForActivityWithTimeout(5000);
//
//        mSendQuestion = (Button) postQuestion.findViewById(R.id.PostQuestion);
//        mCancel = (Button) postQuestion.findViewById(R.id.Cancel);
//        mTitle = (TextView) postQuestion.findViewById(R.id.QuestionTitle);
//        mBody = (TextView) postQuestion.findViewById(R.id.QuestionBody);
//
//
//        //Verify that MainActivity was started
//        assertNotNull("mSendQuestion is null", mSendQuestion);
//        assertNotNull("mCancel is null", mCancel);
//        assertNotNull("mTitle is null", mTitle);
//        assertNotNull("mBody is null", mBody);
//        assertNotNull("ReceiverActivity is null", postQuestion);
//        assertEquals("Monitor for postQuestion has been called", 1,
//                receiverActivityMonitor.getHits());
//
//        //Test case 1: Normal String
//        getInstrumentation().runOnMainSync(new Runnable() {
//            @Override
//            public void run() {
//                mTitle.requestFocus();
//            }
//        });
//        getInstrumentation().waitForIdleSync();
//        getInstrumentation().sendStringSync("<Dislike message>");
//        getInstrumentation().waitForIdleSync();
//
//        String actualText = mTitle.getText().toString();
//        while (actualText.isEmpty())
//            actualText = mTitle.getText().toString();
//        assertEquals("<Dislike message>", actualText);
//
//        TouchUtils.clickView(this, mSendQuestion);
//
//        //This part should be separated by a new test
//        View listElement = lView.getChildAt(lView.getCount() - 1);
//        assertNotNull(listElement);
//
//        ImageButton mLikeButton = (ImageButton) listElement.findViewById(R.id.QuestionLike);
//        ImageButton mDislikeButton = (ImageButton) listElement.findViewById(R.id.QuestionDislike);
//        //Should exist
//        assertNotNull(mLikeButton);
//        assertNotNull(mDislikeButton);
//        //Click dislike button
//        Thread.sleep(1000);
//        TouchUtils.clickView(this, mDislikeButton);
//        //Should be not clickable now
//        assertFalse(mLikeButton.isClickable());
//        assertFalse(mDislikeButton.isClickable());
//        //Try to update like/dislike by function call instead
//        activity.updateLike((String) listElement.getTag());
//        activity.updateDislike((String) listElement.getTag());
//
//    getInstrumentation().removeMonitor(receiverActivityMonitor);
//    }
//
//
//    @MediumTest
//    public void testPostingMessage_EmptyString() {
//
//        Instrumentation inst = new Instrumentation();
//        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
//                .addMonitor(PostQuestion.class.getName(), null, false);
//
//        final ListView lView = getActivity().getListView();
//
//        TouchUtils.clickView(this, mButton);
//
//        PostQuestion postQuestion = (PostQuestion) receiverActivityMonitor
//                .waitForActivityWithTimeout(5000);
//
//        mSendQuestion = (Button) postQuestion.findViewById(R.id.PostQuestion);
//        mCancel = (Button) postQuestion.findViewById(R.id.Cancel);
//        mTitle = (TextView) postQuestion.findViewById(R.id.QuestionTitle);
//        mBody = (TextView) postQuestion.findViewById(R.id.QuestionBody);
//
//
//        //Verify that MainActivity was started
//        assertNotNull("mSendQuestion is null", mSendQuestion);
//        assertNotNull("mCancel is null", mCancel);
//        assertNotNull("mTitle is null", mTitle);
//        assertNotNull("mBody is null", mBody);
//        assertNotNull("ReceiverActivity is null", postQuestion);
//        assertEquals("Monitor for postQuestion has been called", 1,
//                receiverActivityMonitor.getHits());
//
//        //Test case 2: Empty string
//        getInstrumentation().runOnMainSync(new Runnable() {
//            @Override
//            public void run() {
//                mTitle.requestFocus();
//            }
//        });
//        getInstrumentation().waitForIdleSync();
//        getInstrumentation().sendStringSync("");
//        getInstrumentation().waitForIdleSync();
//
//        String actualText = mTitle.getText().toString();
//        actualText = mTitle.getText().toString();
//        assertTrue(actualText.isEmpty());
//
//        TouchUtils.clickView(this, mSendQuestion);
//        TouchUtils.clickView(this, mCancel);
//
//        getInstrumentation().removeMonitor(receiverActivityMonitor);
//    }
//
//    @MediumTest
//    public void testReplyMessage_LikeDislike() throws Exception{
//
//        Instrumentation inst = new Instrumentation();
//        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
//                .addMonitor(PostQuestion.class.getName(), null, false);
//        Instrumentation.ActivityMonitor receiverActivityMonitor2 = getInstrumentation()
//                .addMonitor(ReplyActivity.class.getName(), null, false);
//
//        final ListView lView = getActivity().getListView();
//
//        TouchUtils.clickView(this, mButton);
//
//        PostQuestion postQuestion = (PostQuestion) receiverActivityMonitor
//                .waitForActivityWithTimeout(5000);
//
//        mSendQuestion = (Button) postQuestion.findViewById(R.id.PostQuestion);
//        mCancel = (Button) postQuestion.findViewById(R.id.Cancel);
//        mTitle = (TextView) postQuestion.findViewById(R.id.QuestionTitle);
//        mBody = (TextView) postQuestion.findViewById(R.id.QuestionBody);
//
//
//        //Verify that MainActivity was started
//        assertNotNull("mSendQuestion is null", mSendQuestion);
//        assertNotNull("mCancel is null", mCancel);
//        assertNotNull("mTitle is null", mTitle);
//        assertNotNull("mBody is null", mBody);
//        assertNotNull("ReceiverActivity is null", postQuestion);
//        assertEquals("Monitor for postQuestion has been called", 1,
//                receiverActivityMonitor.getHits());
//
//        //Test case 3
//        getInstrumentation().runOnMainSync(new Runnable() {
//            @Override
//            public void run() {
//                mTitle.requestFocus();
//            }
//        });
//        getInstrumentation().waitForIdleSync();
//        getInstrumentation().sendStringSync("<Reply button and like testing>");
//        getInstrumentation().waitForIdleSync();
//
//        String actualText = mTitle.getText().toString();
//        while (actualText.isEmpty())
//            actualText = mTitle.getText().toString();
//        assertEquals("<Reply button and like testing>", actualText);
//
//        TouchUtils.clickView(this, mSendQuestion);
//        getInstrumentation().removeMonitor(receiverActivityMonitor);
//        //This part should be separated by a new test
//        View listElement = lView.getChildAt(lView.getCount() - 1);
//        assertNotNull(listElement);
//
//        ImageButton mReplyButton = (ImageButton) listElement.findViewById(R.id.QuestionReply);
//        //Should exist
//        assertNotNull(mReplyButton);
//        //Click dislike button
//        Thread.sleep(1000);
//        TouchUtils.clickView(this, mReplyButton);
//
//
//        ReplyActivity replyActivity = (ReplyActivity) receiverActivityMonitor2
//                .waitForActivityWithTimeout(5000);
//
//        ImageButton mLikeButton = (ImageButton) replyActivity.findViewById(R.id.likeParentQuestion);
//        ImageButton mDislikeButton = (ImageButton) replyActivity.findViewById(R.id.dislikeParentQuestion);
//        //Should exist
//        assertNotNull(mLikeButton);
//        assertNotNull(mDislikeButton);
//        //Click dislike button
//        Thread.sleep(1000);
//        TouchUtils.clickView(this, mLikeButton);
//        //Should be not clickable now
//        assertFalse(mLikeButton.isClickable());
//        assertFalse(mDislikeButton.isClickable());
//    }
//}
