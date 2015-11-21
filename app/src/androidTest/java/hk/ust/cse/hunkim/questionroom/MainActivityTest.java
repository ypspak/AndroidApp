package hk.ust.cse.hunkim.questionroom;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.MediumTest;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.firebase.client.Firebase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import hk.ust.cse.hunkim.questionroom.question.Question;

/**
 * Created by hunkim on 7/20/15.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {


    // /private String roomBaseUrl =  "https://cmkquestionsdb.firebaseio.com//rooms/TestRoom/";

    private ImageButton sortBtn;
    private ImageButton postBtn;
    private ListView listview;
    private String roomBaseUrl =  "https://ypspakclassroom.firebaseio.com/";
    private static final String RoomString = "AppTestcase1" + String.valueOf(new Date().getTime());
    private Firebase mFirebaseRef;
    private static final int SHORT_TIMEOUT_IN_MS = 2000;
    private static final int TIMEOUT_IN_MS = 3000;


    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        roomBaseUrl = roomBaseUrl + "/rooms/" + RoomString;

        Intent mStartIntent = new Intent(Intent.ACTION_MAIN);
        mStartIntent.putExtra("ROOM_NAME", RoomString);
        mStartIntent.putExtra("ROOM_BASE_URL", roomBaseUrl);
        setActivityIntent(mStartIntent);

        Firebase.setAndroidContext(getActivity());
        mFirebaseRef = new Firebase(roomBaseUrl).child("questions");
        /*Question question1 = new Question("Question 1 q1", "");
        Question question2 = new Question("Question 2 #Firebase mFirebaseRef = new Firebase(roomBaseUrl);
        mFirebaseRef.removeValue();#abc look out");
        testQuestionUrl.push().setValue(question1);
        testQuestionUrl.push().setValue(question2);
        testQuestionUrl.push().setValue(question3);*/

        sortBtn = (ImageButton) getActivity().findViewById(R.id.question_sort_button);
        postBtn = (ImageButton) getActivity().findViewById(R.id.post_question_button);
        listview = getActivity().getListView();


    }


    @Override
    protected void tearDown() throws Exception {

        mFirebaseRef.removeValue();
    }

    public void testPrecondition() throws Throwable {

        assertNotNull("Post question button should not be null", postBtn);
        assertNotNull("Sort question button should not be null", sortBtn);
        assertNotNull("List view should not be null", listview);

        TouchUtils.clickView(this, postBtn);

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                EditText body = (EditText) getActivity().getDialog().findViewById(R.id.QuestionTitle);
                EditText content = (EditText) getActivity().getDialog().findViewById(R.id.QuestionBody);
                Button postQuestion = (Button) getActivity().getDialog().findViewById(R.id.PostQuestion);
                Button cancel = (Button) getActivity().getDialog().findViewById(R.id.Cancel);

                assertNotNull("Body field should not be null", body);
                assertNotNull("Content field should not be null", content);
                assertNotNull("Post question button should not be null", postQuestion);
                assertNotNull("Cancel button should not be null", cancel);
            }
        });

        getActivity().finish();
    }

    public void testPostQuestion_EmptyTitle() throws Throwable {

        TouchUtils.clickView(this, postBtn);

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                EditText body = (EditText) getActivity().getDialog().findViewById(R.id.QuestionTitle);
                EditText content = (EditText) getActivity().getDialog().findViewById(R.id.QuestionBody);
                Button postQuestion = (Button) getActivity().getDialog().findViewById(R.id.PostQuestion);
                Button cancel = (Button) getActivity().getDialog().findViewById(R.id.Cancel);

                content.setText("This is question 1 testing.");

                postQuestion.performClick();
                assertEquals("There s not error message from the reply input", getActivity().getResources().getString(R.string.error_field_required), body.getError());
            }
        });

        getActivity().finish();
    }

    public void testPostQuestion_CancelButton() throws Throwable {

        TouchUtils.clickView(this, postBtn);

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                EditText body = (EditText) getActivity().getDialog().findViewById(R.id.QuestionTitle);
                EditText content = (EditText) getActivity().getDialog().findViewById(R.id.QuestionBody);
                Button postQuestion = (Button) getActivity().getDialog().findViewById(R.id.PostQuestion);
                Button cancel = (Button) getActivity().getDialog().findViewById(R.id.Cancel);

                content.setText("This is question 1 testing.");

                cancel.performClick();
                assertFalse("Dialog should be gone", getActivity().getDialog().isShowing());
            }
        });

        getActivity().finish();
    }

    public void testPostQuestion_ManyHashtags() throws Throwable {

        TouchUtils.clickView(this, postBtn);

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                EditText body = (EditText) getActivity().getDialog().findViewById(R.id.QuestionTitle);
                EditText content = (EditText) getActivity().getDialog().findViewById(R.id.QuestionBody);
                Button postQuestion = (Button) getActivity().getDialog().findViewById(R.id.PostQuestion);
                Button cancel = (Button) getActivity().getDialog().findViewById(R.id.Cancel);

                body.setText("This is question 1. #Q1 #Testcase1 #Q1 #Q4");
                content.setText("As explained, this is question 1 testing. #Test #ManyTags");

                postQuestion.performClick();
                assertFalse("Dialog should be gone", getActivity().getDialog().isShowing());
            }
        });

        Thread.sleep(SHORT_TIMEOUT_IN_MS);
        assertEquals("Listview should only have 1 element", 1, listview.getCount());
        getActivity().finish();
    }

    public void testPostQuestion_Precondition() throws Throwable {

        Question question = new Question("Question 2 #q2 ", "This is question 2 \n # dealwith it haha.");
        mFirebaseRef.push().setValue(question);

        Thread.sleep(SHORT_TIMEOUT_IN_MS);
        assertEquals("Listview should only have 1 element", 1, listview.getCount());

        View listElement = listview.getChildAt(0);

        ImageButton qLike = (ImageButton) listElement.findViewById(R.id.QuestionLike);
        ImageButton qDisLike = (ImageButton) listElement.findViewById(R.id.QuestionDislike);
        ImageButton qReply = (ImageButton) listElement.findViewById(R.id.QuestionReply);
        TextView qTime = (TextView) listElement.findViewById(R.id.parent_question_time_text);
        TextView qLikeText = (TextView) listElement.findViewById(R.id.likenumber);
        TextView qDisLikeText = (TextView) listElement.findViewById(R.id.dislikenumber);
        TextView qReplyText = (TextView) listElement.findViewById(R.id.replynumber);
        TextView qHashtagText = (TextView) listElement.findViewById(R.id.hashtagText);

        assertNotNull("Like button for question should not be null", qLike);
        assertNotNull("Dislike button for question should not be null", qDisLike);
        assertNotNull("Reply button for question should not be null", qReply);
        assertNotNull("Time text for question should not be null", qTime);
        assertNotNull("Like number text for question should not be null", qLikeText);
        assertNotNull("Dislike number text for question should not be null", qDisLikeText);
        assertNotNull("Reply number text for question should not be null", qReplyText);
        assertNotNull("Hashtag text for question should not be null", qHashtagText);

        getActivity().finish();
    }

    public void testPostQuestion_Like() throws Throwable {

        Question question = new Question("Like question", "");
        mFirebaseRef.push().setValue(question);

        Thread.sleep(SHORT_TIMEOUT_IN_MS);
        assertEquals("Listview should only have 1 element", 1, listview.getCount());

        View listElement = listview.getChildAt(0);
        ImageButton qLike = (ImageButton) listElement.findViewById(R.id.QuestionLike);
        TextView qLikeText = (TextView) listElement.findViewById(R.id.likenumber);
        TextView qDisLikeText = (TextView) listElement.findViewById(R.id.dislikenumber);

        TouchUtils.clickView(this, qLike);
        assertEquals("Like number should be 1", "1", qLikeText.getText());
        assertEquals("DisLike number should be 0", "0", qDisLikeText.getText());

        getActivity().updateLike((String) listElement.getTag());
        getActivity().updateDislike((String) listElement.getTag());

        assertEquals("Like number should be 1", "1", qLikeText.getText());
        assertEquals("DisLike number should be 0", "0", qDisLikeText.getText());

        getActivity().finish();
    }

    public void testPostQuestion_ReplyLike() throws Throwable {

        Instrumentation inst = new Instrumentation();
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(ReplyActivity.class.getName(), null, false);

        Question question = new Question("Reply Like question", "#yolo");
        mFirebaseRef.push().setValue(question);

        Thread.sleep(SHORT_TIMEOUT_IN_MS);
        assertEquals("Listview should only have 1 element", 1, listview.getCount());

        View listElement = listview.getChildAt(0);
        ImageButton qReply = (ImageButton) listElement.findViewById(R.id.QuestionReply);
        TextView qLikeText = (TextView) listElement.findViewById(R.id.likenumber);
        TextView qDisLikeText = (TextView) listElement.findViewById(R.id.dislikenumber);

        TouchUtils.clickView(this, qReply);

        ReplyActivity replyActivity = (ReplyActivity) receiverActivityMonitor
                .waitForActivityWithTimeout(TIMEOUT_IN_MS);
        assertNotNull("ReplyActivity should not be null.", replyActivity);

        ImageButton replyQuestionLike = (ImageButton) replyActivity.findViewById(R.id.parent_question_like_button);
        TextView replyQuestionLikeText = (TextView) replyActivity.findViewById(R.id.parent_question_like_text);
        TextView replyQuestionDislikeText = (TextView) replyActivity.findViewById(R.id.parent_question_dislike_text);

        assertEquals("Number of like should be 0", "0", replyQuestionLikeText.getText());
        assertEquals("Number of dislike should be 0", "0", replyQuestionDislikeText.getText());

        TouchUtils.clickView(this, replyQuestionLike);

        assertEquals("Number of like should be 1", "1", replyQuestionLikeText.getText());
        assertEquals("Number of dislike should be 0", "0", replyQuestionDislikeText.getText());

        getActivity().finish();
    }

    public void testPostQuestion_ReplyDislike() throws Throwable {

        Instrumentation inst = new Instrumentation();
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(ReplyActivity.class.getName(), null, false);

        Question question = new Question("Reply Dislike question", "#yolo");
        mFirebaseRef.push().setValue(question);

        Thread.sleep(SHORT_TIMEOUT_IN_MS);
        assertEquals("Listview should only have 1 element", 1, listview.getCount());

        View listElement = listview.getChildAt(0);
        ImageButton qReply = (ImageButton) listElement.findViewById(R.id.QuestionReply);
        TextView qLikeText = (TextView) listElement.findViewById(R.id.likenumber);
        TextView qDisLikeText = (TextView) listElement.findViewById(R.id.dislikenumber);

        TouchUtils.clickView(this, qReply);

        ReplyActivity replyActivity = (ReplyActivity) receiverActivityMonitor
                .waitForActivityWithTimeout(TIMEOUT_IN_MS);
        assertNotNull("ReplyActivity should not be null.", replyActivity);

        ImageButton replyQuestionDislike = (ImageButton) replyActivity.findViewById(R.id.parent_question_dislike_button);
        TextView replyQuestionLikeText = (TextView) replyActivity.findViewById(R.id.parent_question_like_text);
        TextView replyQuestionDislikeText = (TextView) replyActivity.findViewById(R.id.parent_question_dislike_text);

        assertEquals("Number of like should be 0", "0", replyQuestionLikeText.getText());
        assertEquals("Number of dislike should be 0", "0", replyQuestionDislikeText.getText());

        TouchUtils.clickView(this, replyQuestionDislike);

        assertEquals("Number of like should be 0", "0", replyQuestionLikeText.getText());
        assertEquals("Number of dislike should be 1", "1", replyQuestionDislikeText.getText());

        getActivity().finish();
    }

    public void testPostQuestion_DisLike() throws Throwable {

        Question question = new Question("Dislike question", "");
        mFirebaseRef.push().setValue(question);

        Thread.sleep(SHORT_TIMEOUT_IN_MS);
        assertEquals("Listview should only have 1 element", 1, listview.getCount());

        View listElement = listview.getChildAt(0);
        ImageButton qDisLike = (ImageButton) listElement.findViewById(R.id.QuestionDislike);
        TextView qLikeText = (TextView) listElement.findViewById(R.id.likenumber);
        TextView qDisLikeText = (TextView) listElement.findViewById(R.id.dislikenumber);

        TouchUtils.clickView(this, qDisLike);
        assertEquals("Like number should be 0", "0", qLikeText.getText());
        assertEquals("Like number should be 0", "1", qDisLikeText.getText());

        getActivity().updateLike((String) listElement.getTag());
        getActivity().updateDislike((String) listElement.getTag());

        assertEquals("Like number should be 0", "0", qLikeText.getText());
        assertEquals("Like number should be 0", "1", qDisLikeText.getText());

        getActivity().finish();
    }

    public void testPostQuestion_SetTimeStamp() throws Throwable {

        Question question = new Question("Timestamp problem", "");
        mFirebaseRef.push().setValue(question);

        Thread.sleep(SHORT_TIMEOUT_IN_MS);
        assertEquals("Listview should only have 1 element", 1, listview.getCount());

        View listElement = listview.getChildAt(0);
        String key = (String) listElement.getTag();
        Long timestamp = question.getLastTimestamp();
        TextView qTime = (TextView) listElement.findViewById(R.id.parent_question_time_text);
        assertTrue("Time should be just now", qTime.getText().equals("Just now"));

        mFirebaseRef.child(key).child("lastTimestamp").setValue((Long) ((new Date().getTime()) - DateUtils.MINUTE_IN_MILLIS));
        Thread.sleep(SHORT_TIMEOUT_IN_MS);
        assertTrue("Time should be 1 minute ago", qTime.getText().equals("1 minute ago"));

        mFirebaseRef.child(key).child("lastTimestamp").setValue((Long) ((new Date().getTime()) - DateUtils.HOUR_IN_MILLIS));
        Thread.sleep(SHORT_TIMEOUT_IN_MS);
        assertTrue("Time should be 1 hour ago", qTime.getText().equals("1 hour ago"));

        DateFormat df = new SimpleDateFormat("yyyy/MM/dd KK:mm aa");
        Date date = new Date(new Date().getTime() - DateUtils.DAY_IN_MILLIS);
        mFirebaseRef.child(key).child("lastTimestamp").setValue((Long) ((new Date().getTime()) - DateUtils.DAY_IN_MILLIS));
        Thread.sleep(SHORT_TIMEOUT_IN_MS);
        assertTrue("Time should be exact", qTime.getText().equals(df.format(date)));

        getActivity().finish();
    }

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
}
