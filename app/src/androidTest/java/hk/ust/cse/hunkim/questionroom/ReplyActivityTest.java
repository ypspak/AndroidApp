package hk.ust.cse.hunkim.questionroom;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import hk.ust.cse.hunkim.questionroom.question.Question;

/**
 * Created by CAI on 2/11/2015.
 */


public class ReplyActivityTest extends ActivityInstrumentationTestCase2<ReplyActivity> {
    private String roomBaseUrl = "https://cmkquestionsdb.firebaseio.com//rooms/TestRoom/";

    private EditText replyInput;
    private ImageButton sendButton;
    private ImageButton parentLike;
    private ImageButton parentDislike;
    private TextView timeText;
    private Button titleText;
    private TextView descText;
    private TextView likeText;
    private TextView dislikeText;



    private Intent mStartIntent;
    private ReplyActivity activity;

    public ReplyActivityTest() {
        super(ReplyActivity.class);
    }

    protected void setUp() throws Exception{
        super.setUp();
        final String tempKey = "pretendingKey";
        Question tempQuestion = new Question("This is a #temp question #title", "This is a temp question body");

        mStartIntent = new Intent(Intent.ACTION_MAIN);
        mStartIntent.putExtra("PUSHED_ID", tempKey);
        mStartIntent.putExtra("ROOM_NAME", "TEST");
        mStartIntent.putExtra("ROOM_BASE_URL", roomBaseUrl);
        mStartIntent.putExtra("NUM_LIKE", tempQuestion.getLike());
        mStartIntent.putExtra("NUM_DISLIKE", tempQuestion.getDislike());
        mStartIntent.putExtra("NUM_REPLY", tempQuestion.getReplies());
        mStartIntent.putExtra("HEAD", tempQuestion.getHead());
        mStartIntent.putExtra("DESC", tempQuestion.getDesc());
        mStartIntent.putExtra("TIMESTAMP", tempQuestion.getTimestamp());
        mStartIntent.putExtra("TAGS", tempQuestion.getTags());
        setActivityIntent(mStartIntent);

        Firebase.setAndroidContext(getActivity());
        Firebase testQuestionUrl = new Firebase(roomBaseUrl).child("questions").child(tempKey);
        testQuestionUrl.setValue(tempQuestion);

        replyInput = (EditText) getActivity().findViewById(R.id.reply_input_field);
        sendButton = (ImageButton) getActivity().findViewById(R.id.send_reply_button);
        parentDislike=(ImageButton) getActivity().findViewById(R.id.parent_question_dislike_button);
        parentLike=(ImageButton) getActivity().findViewById(R.id.parent_question_like_button);
        timeText = (TextView) getActivity().findViewById((R.id.parent_question_time_text));
        titleText = (Button) getActivity().findViewById((R.id.parent_question_head));
        descText = (TextView) getActivity().findViewById((R.id.parent_question_desc));
        likeText = (TextView) getActivity().findViewById((R.id.parent_question_like_text));
        dislikeText = (TextView) getActivity().findViewById(R.id.parent_question_dislike_text);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if(roomBaseUrl!=null){
            Firebase mFirebaseRef = new Firebase(roomBaseUrl);
            mFirebaseRef.removeValue();
        }
    }

    public void testPrecondition(){
        assertNotNull("ReplyActivity is null", getActivity());
        assertNotNull("ReplyMessage field is null", replyInput);
        assertNotNull("ReplySending Button is null", sendButton);
        assertNotNull(timeText);
        assertNotNull(titleText);
        assertNotNull(descText);
        assertNotNull(likeText);
        assertNotNull(dislikeText);
    }

    public void testParentLikeButton(){
        assertNotNull("Parent Question has no like button", parentLike);
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, parentLike);

        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, parentLike);
        getInstrumentation().waitForIdleSync();
    }

    public void testParentDislikeButton(){
        assertNotNull("Parent Question has no like button", parentDislike);
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, parentDislike);
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, parentDislike);
        getInstrumentation().waitForIdleSync();
    }

    public void testReplyWithoutMessage() {
        TouchUtils.clickView(this, sendButton);
        getInstrumentation().waitForIdleSync();
        assertEquals("There s not error message from the reply input", "This field is required", replyInput.getError());
    }

    public void testReplyWithMessage() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                replyInput.requestFocus();
            }
        });
        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        getInstrumentation().sendStringSync("Testing Reply");
        getInstrumentation().waitForIdleSync();

        String actualText = replyInput.getText().toString();
        while(actualText==null){
            actualText = replyInput.getText().toString();
        }

        TouchUtils.clickView(this, sendButton);
        getInstrumentation().waitForIdleSync();
        assertEquals("There s not error message from the reply input", null, replyInput.getError());
    }
}

